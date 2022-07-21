package socket

import com.google.common.eventbus.Subscribe
import org.smartboot.socket.transport.AioSession


class StringLogPost(val logHandle:(v: String)->Unit) {

    @Subscribe
    fun listen(v: String){
        logHandle(v)
    }
}


///查看历史记录的请求
data class ViewHistoryModel(val session: AioSession)
class ViewHistoryModelEvent(var handle: (model: ViewHistoryModel)->Unit){
    @Subscribe
    fun listen(model: ViewHistoryModel){
        handle(model)
    }
}
