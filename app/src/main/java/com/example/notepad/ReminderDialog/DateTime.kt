package com.example.notepad.ReminderDialog

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class DateTime(
    var year : Int =  Calendar.getInstance().get(Calendar.YEAR),
    var month : Int = Calendar.getInstance().get(Calendar.MONTH),
    var day : Int = Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
    var hour : Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
    var minute : Int = Calendar.getInstance().get(Calendar.MINUTE),
    var isSet : Boolean = false
) : Parcelable