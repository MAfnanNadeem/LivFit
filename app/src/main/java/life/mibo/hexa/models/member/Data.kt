package life.mibo.hexa.models.member


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("access_token")
    var accessToken: String?,
    @SerializedName("address1")
    var address1: String?,
    @SerializedName("address2")
    var address2: Any?,
    @SerializedName("age")
    var age: String?,
    @SerializedName("city")
    var city: String?,
    @SerializedName("contact")
    var contact: String?,
    @SerializedName("country")
    var country: String?,
    @SerializedName("dob")
    var dob: String?,
    @SerializedName("expires_in")
    var expiresIn: Int?,
    @SerializedName("firstName")
    var firstName: String?,
    @SerializedName("gender")
    var gender: String?,
    @SerializedName("id")
    var id: Int?,
    @SerializedName("identificationNumber")
    var identificationNumber: Any?,
    @SerializedName("imageThumbnail")
    var imageThumbnail: Any?,
    @SerializedName("lastName")
    var lastName: String?,
    @SerializedName("latitude")
    var latitude: String?,
    @SerializedName("longitude")
    var longitude: String?,
    @SerializedName("medicalHistory")
    var medicalHistory: MedicalHistory?,
    @SerializedName("number_verify")
    var numberVerify: Int?,
    @SerializedName("primaryContactEmail")
    var primaryContactEmail: String?,
    @SerializedName("primaryContactName")
    var primaryContactName: String?,
    @SerializedName("primaryContactRelation")
    var primaryContactRelation: String?,
    @SerializedName("primaryPhone")
    var primaryPhone: String?,
    @SerializedName("province")
    var province: Any?,
    @SerializedName("secondaryContactEmail")
    var secondaryContactEmail: Any?,
    @SerializedName("secondaryContactName")
    var secondaryContactName: Any?,
    @SerializedName("secondaryContactRelation")
    var secondaryContactRelation: Any?,
    @SerializedName("secondaryPhone")
    var secondaryPhone: String?,
    @SerializedName("token_type")
    var tokenType: String?,
    @SerializedName("trainingGoals")
    var trainingGoals: TrainingGoals?,
    @SerializedName("zip")
    var zip: String?
)