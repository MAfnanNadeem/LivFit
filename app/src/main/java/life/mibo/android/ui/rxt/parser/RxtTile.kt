package life.mibo.android.ui.rxt.parser

import life.mibo.android.core.toIntOrZero

data class RxtTile(var uid: String, var tileId: Int) {
    fun from(tile: String) {
        val tiles = tile.split("-")
        if (tiles.isNotEmpty()) {
            uid = tiles[0]
        }
        if (tiles.size > 1) {
            tileId = tiles[1].toIntOrZero()
        }
    }
}
