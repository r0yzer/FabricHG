package de.royzer.fabrichg.game

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity

interface FabricHGRuntime {
    var isIngameOrInvincibility: Boolean
    var isIngame: Boolean
    var kitAmount: Int
    var maxPlayers: Int
    var anchor2Anvils: Boolean
    var isBlockPlacingForbidden: Boolean

    fun isInFight(player: HGPlayer): Boolean
    fun sendPlayerStatus(player: ServerPlayer)
    fun canDamage(source: DamageSource, entity: Entity): Boolean
    fun isNotStarted(): Boolean
}

lateinit var fabricHGRuntime: FabricHGRuntime