/*
 *  Created by Sumeet Kumar on 5/16/20 3:15 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/16/20 3:15 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.weather


import com.google.gson.annotations.SerializedName

data class OpenWeather(
    @SerializedName("base")
    var base: String?,
    @SerializedName("clouds")
    var clouds: Clouds?,
    @SerializedName("cod")
    var cod: Int?,
    @SerializedName("coord")
    var coord: Coord?,
    @SerializedName("dt")
    var dt: Int?,
    @SerializedName("id")
    var id: Int?,
    @SerializedName("main")
    var main: Main?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("sys")
    var sys: Sys?,
    @SerializedName("weather")
    var weather: List<Weather?>?,
    @SerializedName("wind")
    var wind: Wind?
) {
    data class Clouds(
        @SerializedName("all")
        var all: Int?
    )

    data class Coord(
        @SerializedName("lat")
        var lat: Double?,
        @SerializedName("lon")
        var lon: Double?
    )

    data class Main(
        @SerializedName("grnd_level")
        var grndLevel: Double?,
        @SerializedName("humidity")
        var humidity: Int?,
        @SerializedName("pressure")
        var pressure: Double?,
        @SerializedName("sea_level")
        var seaLevel: Double?,
        @SerializedName("temp")
        var temp: Double?,
        @SerializedName("temp_max")
        var tempMax: Double?,
        @SerializedName("temp_min")
        var tempMin: Double?
    )

    data class Sys(
        @SerializedName("country")
        var country: String?,
        @SerializedName("message")
        var message: Double?,
        @SerializedName("sunrise")
        var sunrise: Int?,
        @SerializedName("sunset")
        var sunset: Int?
    )

    data class Weather(
        @SerializedName("description")
        var description: String?,
        @SerializedName("icon")
        var icon: String?,
        @SerializedName("id")
        var id: Int?,
        @SerializedName("main")
        var main: String?
    )

    data class Wind(
        @SerializedName("deg")
        var deg: Int?,
        @SerializedName("speed")
        var speed: Double?
    )
}