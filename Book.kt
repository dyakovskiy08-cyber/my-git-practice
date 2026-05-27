package com.example.myapplication

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Book(
    val id: Long = 0,
    val title: String,
    val author: String,
    val description: String = "",
    var isRead: Boolean = false,
    val imageUri: String? = null // Ссылка на фото
) : Parcelable
