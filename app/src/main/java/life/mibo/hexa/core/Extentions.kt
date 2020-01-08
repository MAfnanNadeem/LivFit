package life.mibo.hexa.core

import android.graphics.Color
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

fun Color.PRIMARY():Int {
    return Color.parseColor("#09B189")
}

class Extentions {
}