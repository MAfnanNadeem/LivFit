/*
 *  Created by Sumeet Kumar on 7/16/20 4:01 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 7/16/20 4:01 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.profile

import java.io.Serializable

data class UpdateMember(
        var fName: String,
        var lName: String,
        var dob: String,
        var city: String,
        var countryIso: String,
        var gender: String
    ) : Serializable