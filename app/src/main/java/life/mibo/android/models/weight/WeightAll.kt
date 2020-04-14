/*
 *  Created by Sumeet Kumar on 1/9/20 2:13 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/9/20 2:13 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.weight


import life.mibo.android.models.base.BasePost

class WeightAll(userId: String, token: String?): BasePost<DataX?>(DataX(userId), "AllWeight", token)
{
//    constructor(userId: String, token: String?) : this(
//        "Client1213", DataX(userId), "192.168.195.122", "AllWeight",
//        "2019-12-10T04:49:11.6570000", token, "1.0.0.0"
//    )
}