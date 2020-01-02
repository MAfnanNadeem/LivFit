package life.mibo.hexa.models.base

abstract class BaseModel {

//    constructor() {
//        create()
//    }

    open fun create(): PostModel {
        return PostModel.create(this)
    }
}