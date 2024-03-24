package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.kit.cooldown.activateCooldown
import de.royzer.fabrichg.kit.kit
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal
import net.minecraft.world.entity.monster.hoglin.Hoglin
import net.minecraft.world.entity.monster.hoglin.HoglinBase
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.silkmc.silk.core.entity.modifyVelocity
import net.silkmc.silk.core.entity.world
import net.silkmc.silk.core.task.mcCoroutineTask
import net.silkmc.silk.core.text.literal
import kotlin.time.Duration.Companion.seconds

class Eber(level: Level, val owner: HGPlayer) : Hoglin(EntityType.HOGLIN, level), HoglinBase {
    init {
        setImmuneToZombification(true)

        removeFreeWill()
        goalSelector.addGoal(1, MeleeAttackGoal(this, 1.0, true))

        val ebersBefore = (owner.getPlayerData<MutableList<Eber>>(eberKey) ?: mutableListOf())
        ebersBefore.add(this)
        owner.playerData[eberKey] = ebersBefore

        mcCoroutineTask(delay = 7.5.seconds) {
            kill()

            val ebersAfterDeath = (owner.getPlayerData<MutableList<Eber>>(eberKey) ?: mutableListOf())
            ebersAfterDeath.remove(this@Eber)
            owner.playerData[eberKey] = ebersAfterDeath
        }
        customName = "${owner.name}'s Eber".literal
    }

    var eberTarget: LivingEntity? = null

    override fun tick() {
        target = eberTarget
        super.tick()
    }

    override fun doHurtTarget(target: Entity): Boolean {
        val result = super.doHurtTarget(target)
        target.modifyVelocity(Vec3(0.0, 1.0, 0.0))
        return result
    }
}

const val eberKey = "eberlist"

val eberKit = kit("Eber") {

    kitSelectorItem = Items.NETHERITE_INGOT.defaultInstance

    cooldown = 35.0

    kitItem {
        itemStack = kitSelectorItem.copy()

        onClick { hgPlayer, kit ->
            val world = hgPlayer.serverPlayer?.world ?: return@onClick

            val eber = Eber(world, hgPlayer).also {
                it.setPos(hgPlayer.serverPlayer!!.position())
            }
            world.addFreshEntity(eber)

            hgPlayer.activateCooldown(kit)
        }
    }

    // TODO auch ohne cooldown
    kitEvents {
        onHitEntity { hgPlayer, kit, entity ->
            if (entity !is LivingEntity) return@onHitEntity

            val eber = hgPlayer.getPlayerData<List<Eber>>(eberKey)
            if (eber.isNullOrEmpty()) return@onHitEntity

            eber.forEach {
                if (entity == it) return@forEach
                it.eberTarget = entity
            }
        }
    }
}