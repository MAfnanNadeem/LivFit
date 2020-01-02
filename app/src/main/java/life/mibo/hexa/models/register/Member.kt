/*
 * $Created by $Sumeet $Kumar 2019.
 */

package life.mibo.hexa.models.register


import com.google.gson.annotations.SerializedName

data class Member(
    @SerializedName("user_id")
    var userId: String?
)