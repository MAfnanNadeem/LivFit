package life.mibo.hexa.ui.rxl.model

data class ReflexModel(val id: Int) {

    constructor(
        id: Int,
        title: String,
        image: Int,
        typeText: String,
        userText: String,
        timeText: String,
        podsText: String,
        isLike: Boolean = false
    ) : this(id) {
        this.title = title
        this.image = image
        this.typeText = typeText
        this.userText = userText
        this.timeText = timeText
        this.podsText = podsText
        this.isLike = isLike
    }

    var title: String = ""
    var image: Int = 0
    var typeImage: Int = 0
    var userImage: Int = 0
    var timeImage: Int = 0
    var podsImage: Int = 0
    var typeText: String = ""
    var userText: String = ""
    var timeText: String = ""
    var podsText: String = ""
    var isLike = false


}