package life.mibo.hardware.rxt


data class RxtTile(var uid: String, var tileId: Int) {
    fun from(tile: String) {
        val tiles = tile.split("-")
        if (tiles.isNotEmpty()) {
            uid = tiles[0]
        }
        if (tiles.size > 1) {
            tileId = tiles[1].toIntOrNull() ?: 0
        }
    }
}
