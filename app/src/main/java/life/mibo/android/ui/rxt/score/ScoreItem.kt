package life.mibo.android.ui.rxt.score

import java.io.Serializable

class ScoreItem(var id: Int, var name: String, var hits: String, var missed: String, var color: Int, var total: Int, var totalTimeSec: Int = 0) : Serializable {
}