package com.harismexis.apod.model

data class Apod(
    val date: String?, // "2009-04-20"
    val explanation: String?, // "When does Mars act like a liquid?  Although liquids freeze and evaporate quickly into the thin atmosphere of Mars, persistent winds may make large sand dunes appear to flow and even drip like a liquid.  Visible on the above image right are two flat top mesas in southern Mars, where the season is changing from Spring to Summer.  A light dome topped hill is also visible on the far left of the image.  As winds blow from right to left, flowing sand on and around the hills leaves picturesque streaks.  The dark arc-shaped droplets of fine sand are called barchans, and are the interplanetary cousins of similar Earth-based sand forms. Barchans can move intact downwind and can even appear to pass through each other. Over the past few weeks, winds on southern Mars have been kicking up dust and are being watched to see if they escalate into another of Mars' famous planet-scale sand storms.    digg_url = 'http://apod.nasa.gov/apod/ap090420.html'; digg_skin = 'compact';"
    val hdurl: String?, // "https://apod.nasa.gov/apod/image/0904/sandmars_mro_big.jpg"
    val mediaType: String?, // "image"
    val serviceVersion: String?, // "v1"
    val title: String?, // "Flowing Barchan Sand Dunes on Mars"
    val url: String? // "https://apod.nasa.gov/apod/image/0904/sandmars_mro.jpg"
)

fun String?.extractYouTubeVideoId(): String? {
    if (this == null) return null
    if (!this.contains("youtube")) return null
    val embed = "/embed/"
    val startIndex: Int = this.lastIndexOf(embed) + embed.length
    if (startIndex <= 0) return null
    val endIndex: Int = this.indexOf("?")
    if (endIndex <= 0) return null
    return this.substring(startIndex, endIndex)
}

fun Apod?.isImage(): Boolean? {
    return this?.url?.startsWith("https://apod.nasa.gov/apod/image/") == true
}

fun Apod?.isVideo(): Boolean? {
    return this?.url?.startsWith("https://www.youtube.com/embed/") == true
}
