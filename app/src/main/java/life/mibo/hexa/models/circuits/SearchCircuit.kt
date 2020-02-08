/*
 *  Created by Sumeet Kumar on 2/4/20 12:10 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/4/20 12:10 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.models.circuits


import life.mibo.hexa.models.base.BasePost

class SearchCircuit(data: Data, token: String?) : BasePost<Data>(data, "SearchCircuit", token)