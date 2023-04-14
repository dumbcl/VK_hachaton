package ru.ok.android.itmohack2023.retrofit.dto

import com.google.gson.annotations.SerializedName

class CatFact {

    @SerializedName("fact")
    var fact: String = ""

    @SerializedName("length")
    var length: Int = 0
}