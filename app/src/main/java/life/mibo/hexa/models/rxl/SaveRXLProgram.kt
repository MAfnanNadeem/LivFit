/*
 *  Created by Sumeet Kumar on 2/11/20 9:15 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/11/20 9:15 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.models.rxl


import life.mibo.hexa.models.base.BasePost

class SaveRXLProgram(data: Data, token: String?, requestType: String = "SaveRXLProgram") :
    BasePost<Data>(data, requestType, token)