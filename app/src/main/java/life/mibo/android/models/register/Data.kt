package life.mibo.android.models.register


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("firstName")
    var firstName: String?,
    @SerializedName("lastName")
    var lastName: String?,
    @SerializedName("password")
    var password: String?,
    @SerializedName("email")
    var email: String?,
    @SerializedName("gender")
    var gender: String?,
    @SerializedName("city")
    var city: String?,
    @SerializedName("country")
    var country: String?,
    @SerializedName("dob")
    var dOB: String?,
    @SerializedName("countryCode")
    var countryCode: String?,
    @SerializedName("phone")
    var phone: String?,
    @SerializedName("type")
    var type: String?,
    @SerializedName("key")
    var key: String?
)