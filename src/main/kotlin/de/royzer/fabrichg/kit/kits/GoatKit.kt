package de.royzer.fabrichg.kit.kits

import com.mojang.serialization.Dynamic
import de.royzer.fabrichg.kit.cooldown.activateCooldown
import de.royzer.fabrichg.kit.kit
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.Brain
import net.minecraft.world.entity.animal.goat.Goat
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.schedule.Activity
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.silkmc.silk.core.entity.pos
import net.silkmc.silk.core.entity.world
import net.silkmc.silk.core.math.vector.times
import net.silkmc.silk.core.task.mcCoroutineTask
import kotlin.time.Duration.Companion.seconds

val goatKit = kit("Goat") {
    kitSelectorItem = Items.GOAT_HORN.defaultInstance

    description = "Ronaldo"

    cooldown = 0.250

    kitItem {
        itemStack = kitSelectorItem
        onClickAtEntity { hgPlayer, kit, entity, interactionHand ->
            val serverPlayer = hgPlayer.serverPlayer ?: return@onClickAtEntity
            hgPlayer.activateCooldown(kit)
            val world = serverPlayer.world
            val goat = GoatKitGoat(world)
            world.addFreshEntity(goat)
            val angle = entity.lookAngle.also {
                it.subtract(0.0, it.y, 0.0)
            }
            val behind = entity.pos.subtract(angle.normalize().times(6)).add(0.0, 1.0, 0.0)
            goat.setPos(behind)
            goat.target = serverPlayer//entity as? LivingEntity
            goat.attack(serverPlayer)

            mcCoroutineTask(delay = 7.5.seconds) {
                goat.remove(Entity.RemovalReason.DISCARDED)
            }
        }
    }
}

class GoatKitGoat(level: Level) : Goat(EntityType.GOAT, level) {
    init {
        isScreamingGoat = true
    }

    fun attack(entity: LivingEntity?) {
        (this.brain).setActiveActivityIfPossible(Activity.RAM)
    }

    override fun makeBrain(dynamic: Dynamic<*>): Brain<*> {
        return super.makeBrain(dynamic)
    }

    override fun mobInteract(player: Player, hand: InteractionHand): InteractionResult {
        return InteractionResult.PASS
    }
}