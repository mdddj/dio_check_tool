package socket

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONObject
import logInstance
import model.DioRequest
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

    private val log = logInstance<SocketServerProcessorImpl>()
    override fun process0(session: AioSession?, msg: String?) {

        session?.let {
            msg?.let {
                val jsonObject = JSON.parseObject(msg)
                val type = jsonObject.getString("type")
                val jsonString = jsonObject.getString("jsonString")
                when(type){

                    //api请求
                    "request" -> {
                        val req = JSON.parseObject(jsonString,DioRequest::class.java)
                        EventCenter.instance.post(req)
                    }
                    else -> {
                        log.warn("未处理的消息:${jsonString}")
                    }
                }
            }
        }
    }


    override fun stateEvent0(session: AioSession?, stateMachineEnum: StateMachineEnum?, throwable: Throwable?) {
        when (stateMachineEnum) {
            StateMachineEnum.NEW_SESSION -> {
                log.info("新的连接:${session?.localAddress}  id:${session?.sessionID}")
            }
            StateMachineEnum.INPUT_SHUTDOWN -> {
                log.warn("通道已被关闭")
            }
            StateMachineEnum.PROCESS_EXCEPTION -> {
                log.error("处理异常:", throwable)
            }
            StateMachineEnum.DECODE_EXCEPTION -> {
                log.error("解析数据异常:", throwable)
            }
            StateMachineEnum.INPUT_EXCEPTION -> {
                log.error("读取异常", throwable)
            }
            StateMachineEnum.OUTPUT_EXCEPTION -> {
                log.error("写数据异常", throwable)
            }
            StateMachineEnum.SESSION_CLOSING -> {
                log.warn("${session?.sessionID}正在关闭中...")
            }
            StateMachineEnum.SESSION_CLOSED -> {
                log.warn("连接关闭")
            }
            StateMachineEnum.REJECT_ACCEPT -> {
                log.warn("拒绝接收该连接")
            }
            StateMachineEnum.ACCEPT_EXCEPTION -> {
                log.error("服务端接受连接异常", throwable)
            }

            null -> {
                log.error("NULL >>>")
            }
            else -> {}
        }
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
class MyHertHandle : HeartPlugin<String>(1, 10, TimeUnit.SECONDS) {
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