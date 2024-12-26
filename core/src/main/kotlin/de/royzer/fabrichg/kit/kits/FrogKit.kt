package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.cooldown.activateCooldown
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.util.toVec3
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.Mth
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.projectile.FishingHook
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import net.silkmc.silk.core.entity.modifyVelocity
import net.silkmc.silk.core.item.setCustomName
import net.silkmc.silk.core.math.vector.minus
import net.silkmc.silk.core.math.vector.times
import net.silkmc.silk.core.task.mcCoroutineTask
import kotlin.time.Duration.Companion.seconds


private const val FROG_KEY = "frogBobberEntity"
class FrogLeash(world: Level, val player: ServerPlayer) : FishingHook(EntityType.FISHING_BOBBER, world) {

    init {
        this.addTag("frogBobber")
        owner = player
        player.hgPlayer.playerData[FROG_KEY] = this

        // wenn der in der luft despawnt weil zu lange, mindestens 10 sec aber wenn cooldown lürzer
        mcCoroutineTask(delay = Mth.clamp(frogKit.cooldown!!, 10.0, frogKit.cooldown!!).seconds) {
            player.hgPlayer.playerData.remove(FROG_KEY)
            this@FrogLeash.discard()
        }
    }
    override fun onHitEntity(result: EntityHitResult) {
        super.onHitEntity(result)
    }

    override fun onHitBlock(result: BlockHitResult) {
        this.discard()
        player.hgPlayer.playerData.remove(FROG_KEY)
        super.onHitBlock(result)
    }

}

val frogKit = kit("Frog") {
    kitSelectorItem = Items.KELP.defaultInstance

    description = "Pull your enemies"

    cooldown = 18.0

    kitItem {
        itemStack = kitSelectorItem.copy().also {
            it.setCustomName {
                text("Frog tounge") {
                    italic = false
                    bold = true
                    color = 0x00FF00
                }
            }
        }

        onClick { hgPlayer, kit ->
            val serverPlayer = hgPlayer.serverPlayer ?: return@onClick
            val bobber = hgPlayer.getPlayerData<FrogLeash>(FROG_KEY)
            if (bobber != null) {
                if (bobber.hookedIn == null) return@onClick
                val hooked = bobber.hookedIn ?: return@onClick
                hooked.modifyVelocity {
                    serverPlayer.blockPosition().minus(bobber.blockPosition()).toVec3().normalize().times(2)
                }
                hgPlayer.playerData.remove(FROG_KEY)
                bobber.discard()
                hgPlayer.activateCooldown(kit)
                return@onClick
            }

            serverPlayer.level().addFreshEntity(FrogLeash(serverPlayer.level(), serverPlayer).apply {
                this.deltaMovement = serverPlayer.lookAngle.normalize().times(2)
                this.setPos(serverPlayer.eyePosition)
            })

        }
    }

}