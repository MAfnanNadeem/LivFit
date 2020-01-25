/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.hexa.models.login


import life.mibo.hexa.models.base.BasePost

class LoginUser(data: Login, requestType: String?) :
    BasePost<Login>(data, requestType, "") {
    constructor(username: String, password: String) : this(Login(username, password), "LoginUser")
}