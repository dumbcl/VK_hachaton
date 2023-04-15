package ru.ok.android.networktracker

import android.hardware.TriggerEvent

enum class TrackerEvent {
    CHANGE_BATTERY, CHANGE_REQUEST
}

object Payload {
    val data = 0
}

//<reifed T: TrackerEvent<T>>
typealias SubscriberCallback = (payload: Payload) -> Unit

object Subscriber {
    var eventTitle: TrackerEvent = TrackerEvent.CHANGE_BATTERY
    var callback: SubscriberCallback = {print("hello")}
}


class Tracker {

    init {
        (mb) =>{
            triggerEvents(TriggerEvent.CHANGE)
        }
    }

    val subscribers = ArrayList<Subscriber>()
    fun subscribe(eventTitle: TrackerEvent, callback: SubscriberCallback) {
        val subscriber: Subscriber =  Subscriber
        subscriber.eventTitle = eventTitle
        subscriber.callback = callback
        subscribers.add(subscriber)
    }

    fun unsubscribe(callback: SubscriberCallback) {
        for (subscriber in subscribers)
            if (subscriber.callback == callback) subscribers.remove(subscriber)
    }

    private fun triggerEvents(eventTitle: TrackerEvent, payload: Payload) {
        for (subscriber in subscribers)
            if (subscriber.eventTitle == eventTitle) triggerEvent(eventTitle, payload, subscriber.callback)
    }

    private fun triggerEvent(eventTitle: TrackerEvent, payload: Payload, callback: SubscriberCallback){
        when(eventTitle){
            TrackerEvent.CHANGE_BATTERY -> print(0)
            TrackerEvent.CHANGE_REQUEST -> print(1)
            else -> print(payload)
        }
        callback(payload)
    }
}