/*
 *  Created by Sumeet Kumar on 1/22/20 4:55 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/22/20 4:55 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.models.create_session


import life.mibo.hexa.models.base.BasePost


data class SaveSessionPost(
    var post: Session, var type: String = "SaveSessionReport", var auth: String
) : BasePost<Session>(post, type, auth)