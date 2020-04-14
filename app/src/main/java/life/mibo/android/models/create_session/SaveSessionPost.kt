/*
 *  Created by Sumeet Kumar on 1/22/20 4:55 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/22/20 4:55 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.create_session


import life.mibo.android.models.base.BasePost


class SaveSessionPost(post: Session, auth: String) :
    BasePost<Session>(post, "SaveSessionReport", auth)