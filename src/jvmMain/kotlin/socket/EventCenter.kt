package socket

import com.google.common.eventbus.EventBus

class EventCenter {
    private val eventBus  = EventBus()
    companion object {
        var instance = EventCenterHolder.instance
    }

    fun reg(obj: Any) {
        eventBus.register(obj)
    }

    fun post(data: Any) {
        eventBus.post(data)
    }
    private object EventCenterHolder{
        val instance = EventCenter()
    }
}