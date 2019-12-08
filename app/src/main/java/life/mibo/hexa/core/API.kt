package life.mibo.hexa.core

class API {

    private constructor() {

    }


    val request: API by lazy { API() }

    fun getApi(): ApiService? {

        return null
    }


    interface ApiService {

    }
}