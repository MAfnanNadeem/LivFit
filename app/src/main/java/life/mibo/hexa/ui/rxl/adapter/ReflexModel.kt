package life.mibo.hexa.ui.rxl.adapter

import androidx.navigation.Navigator
import java.io.Serializable

data class ReflexModel(val id: Int) : Serializable {

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

    var extras: Navigator.Extras? = null

    var title: String = ""
    var image: Int = 0
    var typeText: String = ""
    var userText: String = ""
    var timeText: String = ""
    var podsText: String = ""
    var isLike = false


}