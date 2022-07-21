package socket

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONObject
import org.smartboot.socket.Protocol
import org.smartboot.socket.StateMachineEnum
import org.smartboot.socket.extension.plugins.HeartPlugin
import org.smartboot.socket.extension.processor.AbstractMessageProcessor
import org.smartboot.socket.transport.AioSession
import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit


fun AioSession.sendData(dataString: String) {
    val writeBuffer = this.writeBuffer()
    val heartBytes = dataString.toByteArray()
    writeBuffer.writeInt(heartBytes.size)
    writeBuffer.write(heartBytes)
    writeBuffer.flush()
}

class SocketServerProcessorImpl : AbstractMessageProcessor<String?>() {
    override fun process0(session: AioSession?, msg: String?) {
        println("msg: $msg")
        session?.let {
            msg?.let {

            }
        }
    }

    override fun stateEvent0(session: AioSession?, stateMachineEnum: StateMachineEnum?, throwable: Throwable?) {
    }
}

class StringProtocol : Protocol<String?> {

    override fun decode(readBuffer: ByteBuffer, session: AioSession?): String? {
        val remaining: Int = readBuffer.remaining()
        if (remaining < Integer.BYTES) {
            return null
        }
        readBuffer.mark()
        val length: Int = readBuffer.int
        if (length > readBuffer.remaining()) {
            readBuffer.reset()
            return null
        }
        val b = ByteArray(length)
        readBuffer.get(b)
        readBuffer.mark()
        return String(b)
    }
}


///心跳处理,每10秒发送一次心跳
class MyHertHandle : HeartPlugin<String>(1,10, TimeUnit.SECONDS) {
    override fun sendHeartRequest(session: AioSession?) {
        session?.let {
            session.sendData("heart_req")
        }
    }

    override fun isHeartMessage(session: AioSession?, msg: String?): Boolean {
        msg?.let {
            try {
                val parseObject = JSON.parseObject(it, JSONObject::class.java)
                val type = parseObject.getString("dataType")
                if (type == "hert") {
                    return true
                }
            } catch (e: Exception) {
                return false
            }
            return false
        }
        return false
    }

}