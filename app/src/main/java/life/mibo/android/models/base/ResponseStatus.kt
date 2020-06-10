/*
 *  Created by Sumeet Kumar on 5/21/20 11:11 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/12/20 9:53 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.base


class ResponseStatus(response: Any?) : BaseResponse<Any?>(response) {

    fun isDataList(): Boolean {
        if (data == null)
            return false
        if (data is List<*>) {
            return true
        }
        if (data is Collection<*>) {
            return true
        }
        return false
    }

    fun isString(): Boolean {
        if (data is String) {
            return true
        }
        return false
    }
}
