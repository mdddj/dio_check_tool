package socket

import com.google.common.eventbus.Subscribe
import model.DioRequest
import org.smartboot.socket.transport.AioSession


class StringLogPost(val logHandle:(v: String)->Unit) {
    @Subscribe
    fun listen(v: String){
        logHandle(v)
    }
}

class DioRequestPost(val logHandle:(v: DioRequest)->Unit) {

    @Subscribe
    fun listen(v: DioRequest){
        logHandle(v)
    }
}

