package com.example.notepad.ReminderDialog

import java.util.*

data class DateTime(
    var year : Int =  Calendar.getInstance().get(Calendar.YEAR),
    var month : Int = Calendar.getInstance().get(Calendar.MONTH),
    var day : Int = Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
    var hour : Int = Calendar.getInstance().get(Calendar.HOUR),
    var minute : Int = Calendar.getInstance().get(Calendar.MINUTE),
    var isSet : Boolean = false
)