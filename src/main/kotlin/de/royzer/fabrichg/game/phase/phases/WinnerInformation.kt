package de.royzer.fabrichg.game.phase.phases

import de.royzer.fabrichg.data.hgplayer.HGPlayerData
import de.royzer.fabrichg.data.hgplayer.hgPlayerData
import net.minecraft.server.network.ServerPlayerEntity
import java.util.*

class WinnerInformation(
    val name: String,
    val uuid: UUID,
    val hgPlayerData: HGPlayerData
) {
    constructor(serverPlayerEntity: ServerPlayerEntity) : this(serverPlayerEntity.name.string, serverPlayerEntity.uuid, serverPlayerEntity.hgPlayerData)
}