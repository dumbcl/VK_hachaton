package ru.ok.android.networktracker

import android.content.Context
import android.hardware.TriggerEvent
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.Date
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit
import ru.ok.android.networktracker.EmailHandler
import ru.ok.android.networktracker.Email

enum class TrackerEvent(val value: String) {
    //CHANGE_BATTERY("CHANGE_BATTERY"),
    CHANGE_NETWORK("CHANGE_NETWORK"),
    EMAIL("EMAIL")
}

enum class NetworkType(val value: String) {
    BASIC("BASIC"),
    DOWNLOAD_MANAGER("CHANGE_NETWORK"),
    CURL("CURL"),
    WEB_VIEW("CHANGE_NETWORK"),
}

typealias SubscriberCallback = (payload: Payload) -> Unit

object Subscriber {
    var activityName: String = ""
    var eventTitle: TrackerEvent = TrackerEvent.CHANGE_NETWORK
    var callback: SubscriberCallback = {print("hello")}
}

sealed class Payload {
    data class Network(var dataUsage: Long, var activityName: String) : Payload()
    data class EmailP(var email: Email) : Payload()
}

var baseNetworkHandler: BaseNetworkHandler = BaseNetworkHandler()

class Tracker(val context: Context, networkType: NetworkType, period: Long, var maxLogsNumber: Int = 10, var emailOpts: Email? = null) {

    val executor = ScheduledThreadPoolExecutor(3)
    val initialDelay = 0L
    init {
        executeNetwork(period)
    }
    fun executeNetwork(period: Long){
        executor.scheduleAtFixedRate({
            val subs = subscribers.size.toString()
            //Log.d("TrafficUsageJobService", "yehu $subs");
            var dataUsage = baseNetworkHandler.getCurrentDataUsageKB()
            if (dataUsage > 0L){
                for (subscriber in subscribers) {
                    if (subscriber.eventTitle == TrackerEvent.CHANGE_NETWORK) {
                        Log.d("TrafficUsageJobService", "yehu1 $subs");
                        var payloadNetwork: Payload.Network =
                            Payload.Network(dataUsage, subscriber.activityName)
                        triggerEvent(
                            TrackerEvent.CHANGE_NETWORK,
                            payloadNetwork,
                            subscriber.callback
                        )
                    }
                    if (subscriber.eventTitle == TrackerEvent.EMAIL){
                        Log.d("TrafficUsageJobService", "yehu2 $subs");
                        if (emailOpts != null && checkLogsCount()) {
                            var payloadEmail: Payload.EmailP = Payload.EmailP(emailOpts!!)
                            triggerEvent(TrackerEvent.EMAIL, payloadEmail, subscriber.callback)
                        }
                    }
                }
            }
        }, initialDelay, period, TimeUnit.MILLISECONDS)
    }

    var subscribers = ArrayList<Subscriber>()
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

    fun pullLogs(): File {
        val file = File(context.filesDir, DB_NAME)
        if (file.exists()) return file
        else{
            FileOutputStream(file).use {
                val bytes = "".toByteArray()
                it.write(bytes)
            }
            return file
        }
    }
    fun cleanLogs() {
        val file: File = pullLogs()
        FileOutputStream(file).use {
            val bytes = ("").toByteArray()
            it.write(bytes)
        }
    }

    fun countLogs() : Int {
        val file = pullLogs()
        val lines = FileInputStream(file).use {
            String(it.readBytes())
        }.split("\n")
        return lines.size-1
    }

    fun checkLogsCount() : Boolean {
        return (countLogs() > maxLogsNumber)
    }

//    fun isThisMonthCleaned(): Boolean {
//        val file: File = pullLogs()
//        val data = FileInputStream(file).use {
//            String(it.readBytes())
//        }
//        return data.split("\n")[0].split(" ")[0].substring(5, 7) == ZonedDateTime().now().toString().substring(5, 7)
//    }
    fun changeNetwork(payload:Payload.Network) {
        val date = Date()
        val formattedDate = SimpleDateFormat("dd.MM.yyyy").format(date)
        val formattedTime = SimpleDateFormat("HH.mm.ss").format(date)
        val activityName: String? = payload?.activityName
        val size_kb: String = payload?.dataUsage.toString()
        //print("\n$formattedDate $formattedTime $activityName $size_kb")
        //Log.d("TrafficUsageJobService", "\n$formattedDate $formattedTime $activityName $size_kb")
        val file = pullLogs()
        val data = FileInputStream(file).use {
            String(it.readBytes())
        }
        FileOutputStream(file).use {
            val bytes = ("$data\n$formattedDate $formattedTime $activityName $size_kb").toByteArray()
            it.write(bytes)
        }
    }

    private fun triggerEvents(eventTitle: TrackerEvent, payload: Payload) {
        subscribers.forEach{s-> if(s.eventTitle==eventTitle) triggerEvent(eventTitle, payload, s.callback)}
    }

    private fun triggerEvent(eventTitle: TrackerEvent, payload: Payload, callback: SubscriberCallback){
        when(payload){
            is Payload.Network -> {
                changeNetwork(payload)
            }
            is Payload.EmailP -> {
                val emailHandler: EmailHandler = EmailHandler()
                payload.email.attachment = pullLogs()
                emailHandler.sendEmail(payload.email)
                cleanLogs()
                Log.d("TrafficUsageJobService", "ura")
            }
            else -> print("$eventTitle, $payload")
        }
        callback(payload)
    }
    fun stop(){
        executor.shutdown()
    }
    fun clean(){
        stop()
        subscribers.removeAll(subscribers)
    }
}
