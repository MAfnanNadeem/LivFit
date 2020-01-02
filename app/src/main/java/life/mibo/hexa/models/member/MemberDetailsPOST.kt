package life.mibo.hexa.models.member


import com.google.gson.annotations.SerializedName
import life.mibo.hexa.models.base.PostModel

data class MemberDetailsPOST(
    @SerializedName("token")
    var token: String?, var datax: MemberId
) : PostModel(datax) {

}