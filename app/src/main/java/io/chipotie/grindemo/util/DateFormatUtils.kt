package io.chipotie.grindemo.util

import java.text.SimpleDateFormat
import java.util.*

class DateFormatUtils{

    companion object {

        fun formateDate(date: Date): String{
            val simpleDateFormat = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.US)
            return simpleDateFormat.format(date)
        }
    }

}