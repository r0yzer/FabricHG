package de.royzer.fabrichg.data.hgplayer

enum class HGPlayerStatus {
    ALIVE,
    SPECTATOR,
    DISCONNECTED;

    val statusColor
        get() = when (this) {
            ALIVE -> 0x00FF32
            SPECTATOR -> 0xE9E9E9
            DISCONNECTED -> 0xFF4CC0
        }
}