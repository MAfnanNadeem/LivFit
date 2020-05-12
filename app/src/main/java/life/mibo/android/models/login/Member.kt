/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.android.models.login


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BaseModel

data class Member(
    @SerializedName("access_token")
    var accessToken: String?,
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
    @SerializedName("email")
    var email: String?,
    @SerializedName("id")
    var id: Int?,
    @SerializedName("imageThumbnail")
    var imageThumbnail: String?,
    @SerializedName("lastName")
    var lastName: String?,
    @SerializedName("number_verify")
    var numberVerify: Int?,
    @SerializedName("province")
    var province: Any?,
    @SerializedName("token_type")
    var tokenType: String?,
    @SerializedName("profileImg")
    var profileImg: String?,
    @SerializedName("type")
    var type: String?
): BaseModel
{
    fun id() : String{
        return ""+id
    }

    fun isMember(): Boolean {
        return "$type".equals("member", false)
    }
}