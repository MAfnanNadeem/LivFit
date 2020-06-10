/*
 *  Created by Sumeet Kumar on 5/14/20 2:23 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/14/20 2:23 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.trainer


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Certifications(
    @SerializedName("certificate_no")
    var certificateNo: String?,
    @SerializedName("certification_attachment")
    var certificationAttachment: String?,
    @SerializedName("certification_body")
    var certificationBody: String?,
    @SerializedName("certification_date")
    var certificationDate: String?,
    @SerializedName("certification_name")
    var certificationName: String?,
    @SerializedName("certification_valid_until")
    var certificationValidUntil: String?,
    @SerializedName("certification_verification_url")
    var certificationVerificationUrl: String?,
    @SerializedName("id")
    var id: Int?,
    @SerializedName("trainer_user_id")
    var trainerUserId: Int?
)