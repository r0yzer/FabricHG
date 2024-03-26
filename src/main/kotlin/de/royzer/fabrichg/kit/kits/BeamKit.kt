package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.kit.cooldown.activateCooldown
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.property.property
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.boss.enderdragon.EndCrystal
import net.minecraft.world.item.Items
import net.minecraft.world.phys.Vec3
import net.silkmc.silk.core.entity.pos
import net.silkmc.silk.core.entity.world
import net.silkmc.silk.core.math.vector.times
import net.silkmc.silk.core.task.mcCoroutineTask
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds

val beamKit = kit("Beam") {
    kitSelectorItem = Items.END_CRYSTAL.defaultInstance
    description = "Beam your enemies"

    cooldown = 38.0

    val distance by property(15, "beam distance")

    kitItem {
        itemStack = kitSelectorItem

        onClick { hgPlayer, kit ->
            val player = hgPlayer.serverPlayer ?: return@onClick
            val crystal = EndCrystal(EntityType.END_CRYSTAL, player.world)
            crystal.invulnerableTime = Int.MAX_VALUE
            crystal.setShowBottom(false)
            crystal.setPos(player.eyePosition.add(0.0, 1.0, 0.0))
            var target = player.pos.add(player.lookDirection.normalize().times(distance))
            crystal.beamTarget = BlockPos(target.x.toInt(), target.y.toInt(), target.z.toInt())

            player.world.addFreshEntity(crystal)
            crystals.add(crystal)

            mcCoroutineTask(howOften = 20L * 5L) {
                target = player.pos.add(player.lookDirection.normalize().times(distance))
                crystal.teleportTo(player.eyePosition.x, player.eyePosition.y + 1.0, player.eyePosition.z)
                crystal.beamTarget = BlockPos(target.x.roundToInt(), target.y.roundToInt(), target.z.roundToInt())

                if ((it.round.toInt() % 10) == 0) {
                    PlayerList.alivePlayers.forEach { hgPlayer ->
                        val player1 = hgPlayer.serverPlayer ?: return@forEach
                        if (player1 == player) return@forEach
                        if (hgPlayer.isNeo) return@forEach
                        val vec3 = player.getViewVector(1.0f).normalize()
                        var vec32 = Vec3(player1.x - player.x, player1.eyeY - player.eyeY - 1, player1.z - player.z)
                        val d = vec32.length()
                        vec32 = vec32.normalize()
                        val e = vec3.dot(vec32)
                        if ((e > 1.0 - 0.015 / d) && player.hasLineOfSight(player1) && player.distanceTo(player1) < distance) {
                            player1.hurt(player.damageSources().playerAttack(player), 4.0f)
                        }
                    }
                }
            }
            mcCoroutineTask(delay = 5.seconds) {
                crystal.remove(Entity.RemovalReason.DISCARDED)
                crystals.remove(crystal)
            }

            hgPlayer.activateCooldown(kit)
        }

    }
}

val crystals = mutableListOf<EndCrystal>()