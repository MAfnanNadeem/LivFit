package life.mibo.hexa.models.register


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("firstname")
    var firstName: String?,
    @SerializedName("lastname")
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
    @SerializedName("country_code")
    var countryCode: String?,
    @SerializedName("phone")
    var phone: String?
)