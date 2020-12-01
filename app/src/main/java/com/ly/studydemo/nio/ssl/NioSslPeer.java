package com.ly.studydemo.nio.ssl;

import android.content.Context;
import android.content.res.AssetManager;

import com.ly.studydemo.utils.DemoLog;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.KeyStore;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public abstract class NioSslPeer {

    private final String TAG = this.getClass().getSimpleName();

    public static final int BUFFER_SIZE = 2560;

    protected ByteBuffer myAppData;
    protected ByteBuffer myNetData;
    protected ByteBuffer peerAppData;
    protected ByteBuffer peerNetData;
    protected ExecutorService executor = Executors.newSingleThreadExecutor();

    protected abstract void read(SocketChannel socketChannel, SSLEngine engine) throws Exception;

    protected abstract void write(SocketChannel socketChannel, SSLEngine engine, String message) throws Exception;

    protected boolean doHandshake(SocketChannel socketChannel, SSLEngine engine) throws IOException {
        SSLEngineResult result;

        int appBufferSize = engine.getSession().getApplicationBufferSize();
        ByteBuffer myAppData = ByteBuffer.allocate(appBufferSize);
        ByteBuffer peerAppData = ByteBuffer.allocate(appBufferSize);
        myNetData.clear();
        peerNetData.clear();

        SSLEngineResult.HandshakeStatus handshakeStatus = engine.getHandshakeStatus();

        DemoLog.INSTANCE.d(TAG,"About to do handshake 00000  handshakeStatus=" + handshakeStatus);

        while (handshakeStatus != SSLEngineResult.HandshakeStatus.FINISHED && handshakeStatus != SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING) {
            switch (handshakeStatus) {
                case NEED_UNWRAP:
                    int read = 0;
                    if ((read = socketChannel.read(peerNetData)) < 0) {
                        if (engine.isInboundDone() && engine.isOutboundDone()) {
                            return false;
                        }
                        try {
                            engine.closeInbound();
                        } catch (SSLException e) {
                            DemoLog.INSTANCE.d(TAG,"This engine was forced to close inbound, without having received the proper SSL/TLS close notification message from the peer, due to end of stream.");
                        }
                        engine.closeOutbound();
                        // After closeOutbound the engine will be set to WRAP state, in order to try to send a close message to the client.
                        handshakeStatus = engine.getHandshakeStatus();
                        break;
                    }

                    if(read == 0) {
                        peerNetData.compact();
                        continue;
                    }

                    peerNetData.flip();
                    try {
                        result = engine.unwrap(peerNetData, peerAppData);
                        handshakeStatus = result.getHandshakeStatus();
                        DemoLog.INSTANCE.d(TAG,"NEED_UNWRAP... peerNetData=" + peerNetData + "; peerAppData=" + peerAppData);
                        DemoLog.INSTANCE.d(TAG,"NEED_UNWRAP... result=" + result);

                        peerNetData.compact();
                    } catch (SSLException sslException) {
                        DemoLog.INSTANCE.e(TAG,"NEED_UNWRAP A problem was encountered while processing the data that caused the SSLEngine to abort. Will try to properly close connection...", sslException);
                        engine.closeOutbound();
                        handshakeStatus = engine.getHandshakeStatus();
                        break;
                    }
                    switch (result.getStatus()) {
                        case OK:
                            break;
                        case BUFFER_OVERFLOW:
                            // Will occur when peerAppData's capacity is smaller than the data derived from peerNetData's unwrap.
                            peerAppData = enlargeApplicationBuffer(engine, peerAppData);
                            break;
                        case BUFFER_UNDERFLOW:
                            // Will occur either when no data was read from the peer or when the peerNetData buffer was too small to hold all peer's data.
                            peerNetData = handleBufferUnderflow(engine, peerNetData);
                            break;
                        case CLOSED:
                            if (engine.isOutboundDone()) {
                                return false;
                            } else {
                                engine.closeOutbound();
                                handshakeStatus = engine.getHandshakeStatus();
                                break;
                            }
                        default:
                            throw new IllegalStateException("Invalid SSL status: " + result.getStatus());
                    }
                    break;
                case NEED_WRAP:
                    myNetData.clear();

                    try {
                        result = engine.wrap(myAppData, myNetData);
                        handshakeStatus = result.getHandshakeStatus();

                        DemoLog.INSTANCE.d(TAG,"NEED_WRAP... myNetData=" + myNetData + "; myAppData=" + myAppData);
                        DemoLog.INSTANCE.d(TAG,"NEED_WRAP... result=" + result);
                    } catch (SSLException sslException) {
                        DemoLog.INSTANCE.e(TAG,"NEED_WRAP A problem was encountered while processing the data that caused the SSLEngine to abort. Will try to properly close connection...", sslException);
                        engine.closeOutbound();
                        handshakeStatus = engine.getHandshakeStatus();
                        break;
                    }
                    switch (result.getStatus()) {
                        case OK :
                            myNetData.flip();
                            while (myNetData.hasRemaining()) {
                                socketChannel.write(myNetData);
                            }
                            break;
                        case BUFFER_OVERFLOW:
                            // Will occur if there is not enough space in myNetData buffer to write all the data that would be generated by the method wrap.
                            // Since myNetData is set to session's packet size we should not get to this point because SSLEngine is supposed
                            // to produce messages smaller or equal to that, but a general handling would be the following:
                            myNetData = enlargePacketBuffer(engine, myNetData);
                            break;
                        case BUFFER_UNDERFLOW:
                            throw new SSLException("Buffer underflow occured after a wrap. I don't think we should ever get here.");
                        case CLOSED:
                            try {
                                myNetData.flip();
                                while (myNetData.hasRemaining()) {
                                    socketChannel.write(myNetData);
                                }
                                // At this point the handshake status will probably be NEED_UNWRAP so we make sure that peerNetData is clear to read.
                                peerNetData.clear();
                            } catch (Exception e) {
                                DemoLog.INSTANCE.d(TAG,"Failed to send server's CLOSE message due to socket channel's failure.");
                                handshakeStatus = engine.getHandshakeStatus();
                            }
                            break;
                        default:
                            throw new IllegalStateException("Invalid SSL status: " + result.getStatus());
                    }
                    break;
                case NEED_TASK:
                    Runnable task;
                    while ((task = engine.getDelegatedTask()) != null) {
                        executor.execute(task);
                    }
                    handshakeStatus = engine.getHandshakeStatus();
                    break;
                case FINISHED:
                    break;
                case NOT_HANDSHAKING:
                    break;
                default:
                    throw new IllegalStateException("Invalid SSL status: " + handshakeStatus);
            }

            DemoLog.INSTANCE.d(TAG,"About to do handshake while handshakeStatus=" + handshakeStatus);
        }

        DemoLog.INSTANCE.d(TAG,"About to do handshake finish.... " + handshakeStatus);

        return true;
    }

    protected ByteBuffer enlargePacketBuffer(SSLEngine engine, ByteBuffer buffer) {
        return enlargeBuffer(buffer, engine.getSession().getPacketBufferSize());
    }

    protected ByteBuffer enlargeApplicationBuffer(SSLEngine engine, ByteBuffer buffer) {
        return enlargeBuffer(buffer, engine.getSession().getApplicationBufferSize());
    }

    protected ByteBuffer enlargeBuffer(ByteBuffer buffer, int sessionProposedCapacity) {
        if (sessionProposedCapacity > buffer.capacity()) {
            buffer = ByteBuffer.allocate(sessionProposedCapacity);
        } else {
            buffer = ByteBuffer.allocate(buffer.capacity() * 2);
        }
        return buffer;
    }

    protected ByteBuffer handleBufferUnderflow(SSLEngine engine, ByteBuffer buffer) {
        if (engine.getSession().getPacketBufferSize() < buffer.limit()) {
            return buffer;
        } else {
            ByteBuffer replaceBuffer = enlargePacketBuffer(engine, buffer);
            buffer.flip();
            replaceBuffer.put(buffer);
            return replaceBuffer;
        }
    }

    protected void closeConnection(SocketChannel socketChannel, SSLEngine engine) throws IOException  {
        engine.closeOutbound();
        doHandshake(socketChannel, engine);
        socketChannel.close();
    }

    protected void handleEndOfStream(SocketChannel socketChannel, SSLEngine engine) throws IOException  {
        try {
            engine.closeInbound();
        } catch (Exception e) {
            DemoLog.INSTANCE.d(TAG,"This engine was forced to close inbound, without having received the proper SSL/TLS close notification message from the peer, due to end of stream.");
        }
        closeConnection(socketChannel, engine);
    }

    protected KeyManager[] createKeyManagers(Context context, String assetsName, String keystorePassword, String keyPassword) throws Exception {
        AssetManager am = context.getAssets();

        //KeyStore.getInstance("JKS");
        DemoLog.INSTANCE.d(TAG, "keystore type: " + KeyStore.getDefaultType());
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        InputStream keyStoreIS = am.open(assetsName);
        try {
            keyStore.load(keyStoreIS, keystorePassword.toCharArray());
        } finally {
            if (keyStoreIS != null) {
                keyStoreIS.close();
            }
            }
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, keyPassword.toCharArray());
        return kmf.getKeyManagers();
    }

    protected TrustManager[] createTrustManagers(String filepath, String keystorePassword) throws Exception {
        KeyStore trustStore = KeyStore.getInstance("JKS");
        InputStream trustStoreIS = new FileInputStream(filepath);
        try {
            trustStore.load(trustStoreIS, keystorePassword.toCharArray());
        } finally {
            if (trustStoreIS != null) {
                trustStoreIS.close();
            }
        }
        TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustFactory.init(trustStore);
        return trustFactory.getTrustManagers();
    }
}
