package com.ly.studydemo.nio.ssl;

import android.content.Context;

import com.ly.studydemo.utils.DemoLog;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;

public class NioSslClient extends NioSslPeer {

    private static final String TAG = NioSslClient.class.getSimpleName();

    private String remoteAddress;
    private int port;
    private SSLEngine engine;
    private SocketChannel socketChannel;

    public NioSslClient(Context context, String protocol, String remoteAddress, int port) throws Exception {

        this.remoteAddress = remoteAddress;
        this.port = port;

        SSLContext sslContext = SSLContext.getInstance(protocol);
        sslContext.init(null, null, null);
//        sslContext.init(createKeyManagers(context, "client.jks", "storepass", "keypass"),
//                null,
//                new SecureRandom());
        engine = sslContext.createSSLEngine(remoteAddress, port);
        engine.setUseClientMode(true);

        SSLSession session = engine.getSession();
        myAppData = ByteBuffer.allocate(1024);
        myNetData = ByteBuffer.allocate(session.getPacketBufferSize());
        peerAppData = ByteBuffer.allocate(1024);
        // 扩大内存，解决读数据失败问题
        peerNetData = ByteBuffer.allocate(session.getPacketBufferSize()*2);

    }

    public boolean connect() throws Exception {
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress(remoteAddress, port));
        // 等待TCP连接建立，即三次握手完成
        while (!socketChannel.finishConnect()) {
            // can do something here...
        }
        // TCP建立连接后，进行SSL握手
        engine.beginHandshake();
        return doHandshake(socketChannel, engine);
    }

    public void read() throws Exception {
        read(socketChannel, engine);
    }

    public void write(String message) throws IOException {
        write(socketChannel, engine, message);
    }

    @Override
    protected void write(SocketChannel socketChannel, SSLEngine engine, String message) throws IOException {

        DemoLog.INSTANCE.d(TAG, "About to write to the server...");

        myAppData.clear();
        myAppData.put(message.getBytes());
        myAppData.flip();
        while (myAppData.hasRemaining()) {
            // The loop has a meaning for (outgoing) messages larger than 16KB.
            // Every wrap call will remove 16KB from the original message and send it to the remote peer.
            myNetData.clear();
            SSLEngineResult result = engine.wrap(myAppData, myNetData);
            switch (result.getStatus()) {
                case OK:
                    myNetData.flip();
                    while (myNetData.hasRemaining()) {
                        socketChannel.write(myNetData);
                    }
                    DemoLog.INSTANCE.d(TAG,"Message sent to the server: " + message);
                    break;
                case BUFFER_OVERFLOW:
                    myNetData = enlargePacketBuffer(engine, myNetData);
                    break;
                case BUFFER_UNDERFLOW:
                    throw new SSLException("Buffer underflow occured after a wrap. I don't think we should ever get here.");
                case CLOSED:
                    closeConnection(socketChannel, engine);
                    return;
                default:
                    throw new IllegalStateException("Invalid SSL status: " + result.getStatus());
            }
        }

    }

    @Override
    protected void read(SocketChannel socketChannel, SSLEngine engine) throws Exception {
        DemoLog.INSTANCE.d(TAG, "About to read from the server...");

        peerNetData.clear();
        int waitToReadMillis = 50;
        boolean exitReadLoop = false;
        while (!exitReadLoop) {
            int bytesRead = socketChannel.read(peerNetData);
            if (bytesRead > 0) {
                peerNetData.flip();
                while (peerNetData.hasRemaining()) {
                    peerAppData.clear();
                    SSLEngineResult result = engine.unwrap(peerNetData, peerAppData);
                    switch (result.getStatus()) {
                        case OK:
                            peerAppData.flip();
                            DemoLog.INSTANCE.d(TAG, "Server response: " + new String(peerAppData.array()));
                            exitReadLoop = true;
                            break;
                        case BUFFER_OVERFLOW:
                            peerAppData = enlargeApplicationBuffer(engine, peerAppData);
                            break;
                        case BUFFER_UNDERFLOW:
                            peerNetData = handleBufferUnderflow(engine, peerNetData);
                            break;
                        case CLOSED:
                            closeConnection(socketChannel, engine);
                            return;
                        default:
                            throw new IllegalStateException("Invalid SSL status: " + result.getStatus());
                    }
                }
            } else if (bytesRead < 0) {
                handleEndOfStream(socketChannel, engine);
                return;
            }
            Thread.sleep(waitToReadMillis);
        }
    }

    public void shutdown() throws IOException {
        DemoLog.INSTANCE.d(TAG, "About to close connection with the server...");
        closeConnection(socketChannel, engine);
        executor.shutdown();
        DemoLog.INSTANCE.d(TAG, "Goodbye!");
    }

}
