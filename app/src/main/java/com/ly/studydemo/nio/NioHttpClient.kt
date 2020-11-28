package com.ly.studydemo.nio

import com.ly.studydemo.utils.DemoLog
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.SocketChannel

class NioHttpClient(private val host: String, private val port: Int, private val path: String):Runnable {

    private val mSelector: Selector = Selector.open()
    private var mServerThread: Thread? = null
    private var mInnerChannel: SocketChannel? = null

    private val writeBuffer: ByteBuffer = ByteBuffer.allocate(1024)
    private val readBuffer: ByteBuffer = ByteBuffer.allocate(1024)

    fun request() {
        mInnerChannel = SocketChannel.open()
        mInnerChannel?.configureBlocking(false)

        mServerThread?.interrupt()
        mServerThread = Thread(this, "HttpsClient")
        mServerThread?.start()
    }

    override fun run() {
        try {
            mSelector.wakeup();
            mInnerChannel?.register(mSelector, SelectionKey.OP_CONNECT)

            mInnerChannel?.connect(InetSocketAddress(host, port))

            while (!Thread.currentThread().isInterrupted) {
                val select = mSelector.select()
                if (select == 0) {
                    Thread.sleep(5)
                    continue
                }

                val selectionKeys = mSelector.selectedKeys() ?: continue
                val keyIterator = selectionKeys.iterator()
                while (keyIterator.hasNext()) {
                    val key = keyIterator.next()
                    keyIterator.remove()

                    if(!key.isValid) {
                        continue
                    }

                    if(key.isConnectable) {
                        mInnerChannel?.finishConnect();
                        mInnerChannel?.register(mSelector, SelectionKey.OP_WRITE)

                        DemoLog.d("HttpsClient", "server connected...")
                        break
                    }
                    else if(key.isWritable) {
                        val request =
                            "GET ${path} HTTP/1.0\r\n" + // 请求行
                            "Accept: */*\r\n" + // 请求头
                            "Host: ${host}\r\n" + // 请求头
                            "Connection: Close\r\n" + // 请求头
                            "\r\n" // 空行

                        writeBuffer.clear()
                        writeBuffer.put(request.toByteArray())
                        writeBuffer.flip()
                        mInnerChannel?.write(writeBuffer)

                        mInnerChannel?.register(mSelector, SelectionKey.OP_READ)

                        DemoLog.d("HttpsClient", "send data ....")
                    }
                    else if(key.isReadable){
                        readBuffer.clear()
                        val num = mInnerChannel?.read(readBuffer)?:0

                        DemoLog.d("HttpsClient *****", String(readBuffer.array(), 0, num))

                        stop()
                    }
                }
            }
        } catch (e: Exception) {
            DemoLog.e("HttpsClient","updServer catch an exception: %s", e)
        } finally {
            mInnerChannel?.close()
            mInnerChannel = null
            DemoLog.i("HttpsClient","udpServer thread exited.")
        }
    }

    fun stop(){
        mInnerChannel?.close()
        mInnerChannel = null
    }
}