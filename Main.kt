package org.example

import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.json.simple.parser.ParseException
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL

val ipStackApiUrl = "https://api.ipstack.com/"
val ipStackApiKey = "ENTER YOUR IPSTACK API KEY HERE"

val weatherMapApiUrl = "https://api.openweathermap.org/data/2.5/weather"
val weatherMapApiKey = "ENTER YOUR OPENWEATHERMAP API KEY HERE"

var country:String? = null

fun main() {
    fetchIPApi()
    println("The temperature in $country is " + fetchWeatherApi() + "°")
    givePrompt()
}

fun givePrompt() {
    println("Would you like to continue the program?")
    val readLine = readLine()
    if (readLine == "yes" || readLine == "Yes") {
        println("Pick a country!")
        val readLine = readLine()
        country = readLine
        println("The temperature in $country is " + fetchWeatherApi() + "°")
        givePrompt()
    } else {
        println("Ending program!")
    }
}

fun getIPAddress(): String? {
    val whatismyip = URL("http://checkip.amazonaws.com")
    val `in` = BufferedReader(
        InputStreamReader(
            whatismyip.openStream()
        )
    )

    val ip = `in`.readLine()
    return ip
}

fun fetchIPApi() {
    try {
        val url : URL = URI.create(ipStackApiUrl + "${getIPAddress()}" + "?access_key=" + ipStackApiKey).toURL()
        val connection : HttpURLConnection = url.openConnection() as HttpURLConnection

        connection.requestMethod = "GET"

        val responseCode: Int = connection.responseCode
        println("Response Code: $responseCode")

        if (responseCode == HttpURLConnection.HTTP_OK) {
            val reader : BufferedReader = BufferedReader(InputStreamReader(connection.inputStream))
            var line: String?
            val response = StringBuilder()

            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }

            reader.close()
            country = parseIPInfo(response.toString())
            println("Response: $response")
        } else {
            println("Error")
        }

        connection.disconnect()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun parseIPInfo(jsonData: String?): String {
    try {
        val parser: JSONParser = JSONParser()
        val jsonObject: JSONObject = parser.parse(jsonData) as JSONObject

        val countryName = jsonObject.get("country_name") as String

        return countryName
    } catch (e: ParseException) {
        e.printStackTrace()
        return ""
    }
}

fun fetchWeatherApi(): String {
    try {
        val url : URL = URI.create(weatherMapApiUrl + "?q=" + country + "&appid=" + weatherMapApiKey).toURL()
        val connection : HttpURLConnection = url.openConnection() as HttpURLConnection

        connection.requestMethod = "GET"

        val responseCode: Int = connection.responseCode
        println("Response Code: $responseCode")

        if (responseCode == HttpURLConnection.HTTP_OK) {
            val reader : BufferedReader = BufferedReader(InputStreamReader(connection.inputStream))
            var line: String?
            val response = StringBuilder()

            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }

            reader.close()
            return parseWeatherInfo(response.toString())
            println("Response: $response")
        } else {
            println("Error")
            return ""
        }

        connection.disconnect()
    } catch (e: Exception) {
        e.printStackTrace()
        return ""
    }
}

fun parseWeatherInfo(jsonData: String?): String {
    try {
        val parser: JSONParser = JSONParser()
        val jsonObject: JSONObject = parser.parse(jsonData) as JSONObject

        val main = jsonObject["main"] as JSONObject
        val temperature = (main["temp"] as Number).toDouble() - 273.15

        return temperature.toString()
    } catch (e: ParseException) {
        e.printStackTrace()
        return ""
    }
}