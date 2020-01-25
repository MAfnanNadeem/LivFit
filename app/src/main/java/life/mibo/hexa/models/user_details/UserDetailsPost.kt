/*
 *  Created by Sumeet Kumar on 1/9/20 2:17 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/9/20 2:17 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.models.user_details


import life.mibo.hexa.models.base.BasePost

class UserDetailsPost(userId: String?, token: String?) :
    BasePost<DataX?>(DataX(userId), "UserDetails", token) {
    //constructor(userId: String, token: String?) : this(DataX(userId), token)
}