package com.jumpa.jchat.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class ModelUser(
    val name: String? = null,
    val email: String? = null,
    val uid: String? = null,
    val gender: String? = null,
) : Parcelable