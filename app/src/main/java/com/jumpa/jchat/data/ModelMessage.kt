package com.jumpa.jchat.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ModelMessage(
    val message: String? = null,
    val senderId: String? = null,
    val timestamp: Long? = null,

) : Parcelable

