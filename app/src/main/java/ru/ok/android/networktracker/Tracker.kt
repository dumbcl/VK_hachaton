package ru.ok.android.networktracker

import android.hardware.TriggerEvent
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.time.ZonedDateTime

enum class TrackerEvent(val value: String) {
    CHANGE_BATTERY("CHANGE_BATTERY"),
    CHANGE_REQUEST("CHANGE_REQUEST")
}

typealias SubscriberCallback = (payload: Any) -> Unit

typealias Payload = Any

object Subscriber {
    var activityName: String = ""
    var eventTitle: TrackerEvent = TrackerEvent.CHANGE_BATTERY
    var callback: SubscriberCallback = {print("hello")}
}


class Tracker {

    init {
        NetworkTracker.getInstance()
    }

    val subscribers = ArrayList<Subscriber>()
    val DB_NAME = "logs.txt"
    fun subscribe(activityName: String, eventTitle: TrackerEvent, callback: SubscriberCallback) {
        val subscriber: Subscriber =  Subscriber
        subscriber.activityName = activityName
        subscriber.eventTitle = eventTitle
        subscriber.callback = callback
        subscribers.add(subscriber)
    }

    fun unsubscribe(callback: SubscriberCallback) {
        for (subscriber in subscribers)
            if (subscriber.callback == callback) subscribers.remove(subscriber)
    }

    private fun triggerEvents(eventTitle: TrackerEvent, payload: Payload) {
        subscribers.forEach{s-> if(s.eventTitle==eventTitle) triggerEvent(eventTitle, payload, s.callback)}
    }

    fun pullLogs(): File {
        return File("", DB_NAME)
    }
    fun cleanLogs() {
        val file: File = pullLogs()
        FileOutputStream(file).use {
            val bytes = ("").toByteArray()
            it.write(bytes)
        }
    }
    fun isThisMonthCleaned(): Boolean {
        val file: File = pullLogs()
        val data = FileInputStream(file).use {
            String(it.readBytes())
        }
        return data.split("\n")[0].split(" ")[0].substring(5, 7) == ZonedDateTime().now().toString().substring(5, 7)
    }
    private fun triggerEvent(eventTitle: TrackerEvent, payload: Payload, callback: SubscriberCallback){
        when(eventTitle){
            TrackerEvent.CHANGE_REQUEST -> {
                val timeNow: String = ZonedDateTime().now().toString()
                val dateCalendar: String = timeNow.substring(0, 10).replace("-", ".")
                val dateClock: String = timeNow.substring(11, 19)
                val activityName: String = payload?.activityName
                val size_kb: String = payload?.size_kb.toString()

                val file = pullLogs()
                val data = FileInputStream(file).use {
                    String(it.readBytes())
                }
                FileOutputStream(file).use {
                    val bytes = (data + "\n$dateCalendar $dateClock $activityName $size_kb").toByteArray()
                    it.write(bytes)
                }
            }
            else -> print("$eventTitle, $payload")
        }
        callback(payload)
    }
}
