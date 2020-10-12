package life.mibo.fitbitsdk.service.api


interface NetworkConnectivityChecker {
    fun isConnected(): Boolean
}