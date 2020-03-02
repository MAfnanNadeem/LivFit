/*
 *  Created by Sumeet Kumar on 1/22/20 11:24 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/12/20 5:35 PM
 *  Mibo Hexa - app
 */

/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.hexa.database


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import life.mibo.hexa.models.base.BaseModel

@Entity(tableName = "mibo_member")
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
    var province: String?,
    @SerializedName("token_type")
    var tokenType: String?
) : BaseModel {
    fun id(): String {
        return id.toString()
    }

    @PrimaryKey(autoGenerate = false)
    var pk: Int = 1

    companion object {
        fun from(member: life.mibo.hexa.models.login.Member): Member {

            return Member(
                member.accessToken,
                member.city,
                member.contact,
                member.country,
                member.dob,
                member.expiresIn,
                member.firstName,
                member.gender,
                member.email,
                member.id,
                "",
                member.lastName,
                member.numberVerify,
                "",
                member.tokenType
            );
        }
    }
}