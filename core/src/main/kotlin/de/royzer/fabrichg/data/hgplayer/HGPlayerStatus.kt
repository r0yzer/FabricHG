package de.royzer.fabrichg.data.hgplayer

enum class HGPlayerStatus {
    ALIVE,
    SPECTATOR,
    DISCONNECTED,
    GULAG;

    val statusColor
        get() = when (this) {
            ALIVE -> 0x00FF32
            SPECTATOR -> 0xE9E9E9
            DISCONNECTED -> 0xFF4CC0
            GULAG -> 0xF36E3E
        }
}