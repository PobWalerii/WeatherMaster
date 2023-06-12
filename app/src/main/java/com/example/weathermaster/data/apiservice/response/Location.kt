package com.example.weathermaster.data.apiservice.response
import com.google.gson.annotations.SerializedName


class Location : ArrayList<LocationItem>()

class LocationItem(
    @SerializedName("country")
    val country: String,
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("local_names")
    val localNames: LocalNames,
    @SerializedName("lon")
    val lon: Double,
    @SerializedName("name")
    val name: String,
    @SerializedName("state")
    val state: String
)

data class LocalNames(
    val af: String,
    val al: String,
    val ar: String,
    val az: String,
    val be: String,
    val br: String,
    val ca: String,
    val cs: String,
    val cu: String,
    val da: String,
    val de: String,
    val el: String,
    val en: String,
    val eo: String,
    val es: String,
    val et: String,
    val eu: String,
    val fa: String,
    val fi: String,
    val fr: String,
    val gd: String,
    val gl: String,
    val he: String,
    val hr: String,
    val hu: String,
    val hy: String,
    val id: String,
    val `is`: String,
    val `it`: String,
    val ja: String,
    val kr: String,
    val ka: String,
    val ko: String,
    val ku: String,
    val la: String,
    val lt: String,
    val lv: String,
    val mk: String,
    val ml: String,
    val ms: String,
    val nl: String,
    val nn: String,
    val no: String,
    val oc: String,
    val pl: String,
    val pt: String,
    val ro: String,
    val ru: String,
    val sl: String,
    val sp: String,
    val sk: String,
    val sr: String,
    val sv: String,
    val th: String,
    val tr: String,
    val uk: String,
    val vi: String,
    val yi: String,
    val zh: String,
    val zu: String,
)
