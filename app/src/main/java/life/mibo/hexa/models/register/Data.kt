package life.mibo.hexa.models.register


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("FirstName")
    var firstName: String?,
    @SerializedName("LastName")
    var lastName: String?,
    @SerializedName("Password")
    var password: String?,
    @SerializedName("Email")
    var email: String?,
    @SerializedName("Gender")
    var gender: String?,
    @SerializedName("City")
    var city: String?,
    @SerializedName("Country")
    var country: String?,
    @SerializedName("DOB")
    var dOB: String?,
    @SerializedName("Phone")
    var phone: String?
)