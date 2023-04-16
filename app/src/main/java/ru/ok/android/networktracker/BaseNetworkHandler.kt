package ru.ok.android.networktracker

import android.net.TrafficStats

class BaseNetworkHandler() {
    var lastUsageKb = 0L

    fun getCurrentDataUsageKB(): Long {
        val receivedBytes = TrafficStats.getUidRxBytes(android.os.Process.myUid())
        val sentBytes = TrafficStats.getUidTxBytes(android.os.Process.myUid())
        val deltaLastUsageKb = (receivedBytes/1024+sentBytes/1024) - lastUsageKb
        lastUsageKb = receivedBytes/1024+sentBytes/1024
        return deltaLastUsageKb
    }
}
