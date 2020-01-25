package life.mibo.hexa.models.member


import life.mibo.hexa.models.base.BaseModel
import life.mibo.hexa.models.base.BasePost

class MemberDetailsPOST(token: String?, data: MemberId
) : BasePost<MemberId>(data, "", token) {

}