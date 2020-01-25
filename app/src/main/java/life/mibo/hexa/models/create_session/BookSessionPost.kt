/*
 *  Created by Sumeet Kumar on 1/22/20 4:47 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/22/20 4:47 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.models.create_session


import life.mibo.hexa.models.base.BasePost

class BookSessionPost(post: Data, auth: String) :
    BasePost<Data?>(post, "BookAndStartConsumerSession", auth)