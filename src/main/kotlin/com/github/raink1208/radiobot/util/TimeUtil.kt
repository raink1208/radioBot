package com.github.raink1208.radiobot.util

class TimeUtil {
    companion object {
        fun toMs(minute: Int, second: Int): Long = (minute * 60 + second) * 1000L

        fun toMs(hour: Int, minute: Int, second: Int): Long = (hour * 60 + minute * 60 + second) * 1000L

        fun toMs(day: Int, hour: Int, minute: Int, second: Int): Long = (day * 24 + hour * 60 + minute * 60 + second) * 1000L
    }
}