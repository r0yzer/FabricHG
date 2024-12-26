package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.kit.cooldown.activateCooldown
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.property.property
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.Arrow
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.phys.HitResult
import net.silkmc.silk.core.entity.modifyVelocity
import net.silkmc.silk.core.entity.pos
import net.silkmc.silk.core.math.vector.minus
import java.util.*

private val grapplerArrows = hashMapOf<Arrow, UUID>()

val grapplerKit = kit("Grappler") {
    kitSelectorItem = Items.CROSSBOW.defaultInstance

    description = "Launch yourself"

    cooldown = 3.0

    val launchVelocity by property(4.0, "Launch velocity")
    val fightlaunchVelocity by property(1.5, "Fight launch velocity")

    kitItem {
        itemStack = kitSelectorItem
        onClick { hgPlayer, kit ->
            val serverPlayer = hgPlayer.serverPlayer ?: return@onClick

            if (grapplerArrows.values.contains(hgPlayer.uuid)) return@onClick

            val level = serverPlayer.level()

            level.addFreshEntity(GrapplerArrow(EntityType.ARROW, level, launchVelocity, fightlaunchVelocity).also {
                it.deltaMovement = serverPlayer.lookAngle.scale(4.5)
                it.setPos(serverPlayer.eyePosition)
                grapplerArrows[it] = serverPlayer.uuid
            })

            hgPlayer.activateCooldown(kit)
        }
    }

}

class GrapplerArrow(entityType: EntityType<Arrow>, val level: Level, val launchVel: Double, val fightLaunchVel: Double) : Arrow(entityType, level) {
    override fun onHit(result: HitResult) {
        val type = result.type

        if (type == HitResult.Type.MISS) return
        this.remove(RemovalReason.KILLED)

        val uuid = grapplerArrows[this] ?: return
        val hgPlayer = PlayerList.getPlayer(uuid) ?: return
        val serverPlayer = hgPlayer.serverPlayer ?: return

        val diff = this.pos.minus(serverPlayer.pos).normalize().scale(if (hgPlayer.inFight) fightLaunchVel else launchVel)

        serverPlayer.modifyVelocity(diff.x, (diff.y * 0.8) + 0.2, diff.z)

        grapplerArrows.remove(this)
    }

    override fun isPickable(): Boolean {
        return false
    }

    override fun isCritArrow(): Boolean {
        return true
    }

    override fun isAttackable(): Boolean {
        return false
    }
    override fun tryPickup(player: Player): Boolean {
        return false
    }

    override fun doPostHurtEffects(target: LivingEntity) {
    }

    override fun doKnockback(entity: LivingEntity, damageSource: DamageSource) {
    }
}