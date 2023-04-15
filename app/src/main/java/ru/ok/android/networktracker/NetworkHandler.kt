package ru.ok.android.networktracker


import android.net.TrafficStats
import android.os.Handler
import android.os.Process

object TrafficObserver {

    private object handler : Handler()

    private var interval = 100L

    private object runnable : Runnable {
        override fun run() {
            subscribers.forEach {
                it.update(getTraffic())
            }
            handler.postDelayed(runnable, interval)
        }
    }

    fun updateInterval(interval: Long) {
        TrafficObserver.interval = interval
    }

    fun startObserve() {
        subscribers.forEach {
            it.update(getTraffic())
        }
        handler.postDelayed(runnable, interval)
    }

    fun stopObserve() {
        handler.removeCallbacks(runnable)
    }

    fun getInitialTraffic(): Long = getTraffic()

    private fun getTraffic() = TrafficStats.getUidTxBytes(Process.myUid()) + TrafficStats.getUidRxBytes(Process.myUid())

}

class NetworkHandler {

}