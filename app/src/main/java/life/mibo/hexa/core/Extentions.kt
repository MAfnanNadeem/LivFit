package life.mibo.hexa.core

import com.google.gson.Gson
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import java.io.Serializable

@Throws(JsonIOException::class)
fun Serializable.toJson(): String {
    return Gson().toJson(this)
}

@Throws(JsonSyntaxException::class)
fun <T> String.to(type: Class<T>): T where T : Serializable? {
    return Gson().fromJson(this, type)
}

class Extentions {
}