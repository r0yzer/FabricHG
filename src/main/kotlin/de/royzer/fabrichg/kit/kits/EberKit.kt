package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.achievements.KitAchievement
import de.royzer.fabrichg.kit.achievements.delegate.achievement
import de.royzer.fabrichg.kit.cooldown.activateCooldown
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.property.property
import de.royzer.fabrichg.util.toHighestPos
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.targeting.TargetingConditions
import net.minecraft.world.entity.monster.hoglin.Hoglin
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.pathfinder.Path
import net.minecraft.world.phys.Vec3
import net.silkmc.silk.core.entity.modifyVelocity
import net.silkmc.silk.core.entity.pos
import net.silkmc.silk.core.entity.world
import net.silkmc.silk.core.task.mcCoroutineTask
import net.silkmc.silk.core.text.literal
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

class Eber(
    level: Level,
    val owner: HGPlayer,
    val eberAliveTime: Double,
    val eberVelocityBoost: Double,
    val eberSpeed: Double,
    val launchPlayersAchievement: KitAchievement
): Hoglin(EntityType.HOGLIN, level) {
    init {
        setImmuneToZombification(true)

        removeFreeWill()
        //goalSelector.addGoal(1, MeleeAttackGoal(this, 1.0, true))

        val ebersBefore = (owner.getPlayerData<MutableList<Eber>>(eberKey) ?: mutableListOf())
        ebersBefore.add(this)
        owner.playerData[eberKey] = ebersBefore

        mcCoroutineTask(delay = eberAliveTime.seconds) {
            remove(RemovalReason.DISCARDED)

            val ebersAfterDeath = (owner.getPlayerData<MutableList<Eber>>(eberKey) ?: mutableListOf())
            ebersAfterDeath.remove(this@Eber)
            owner.playerData[eberKey] = ebersAfterDeath
        }
        customName = "${owner.name}'s Eber".literal
    }

    var eberTarget: LivingEntity? = null

    private var path: Path? = null
    private var ticksUntilPathRecalculation = 0

    override fun tick() {
        target = eberTarget
        owner.serverPlayer?.takeIf { isAlive && it.isAlive }?.startRiding(this)

        if (path == null || ticksUntilPathRecalculation <= 0) {
            val pos = pos.add(owner.serverPlayer!!.forward.multiply(2.25, 2.25, 2.25))
            val bpos = BlockPos(pos.x.toInt(), pos.y.toInt(), pos.z.toInt()).toHighestPos()
            path = navigation.createPath(bpos, 0)
        }
        path?.let { navigation.moveTo(it, eberSpeed) }
        ticksUntilPathRecalculation--


        world.getNearbyEntities(
            LivingEntity::class.java,
            TargetingConditions.DEFAULT,
            this,
            hitbox.inflate(1.5, 1.25, 1.5)
        ).forEach {
            if (it == owner.serverPlayer || it.hgPlayer?.isNeo == true) return@forEach
            doHurtTarget(it)
        }
        super.tick()
    }

    override fun doHurtTarget(target: Entity): Boolean {
        if (target is ServerPlayer) {
            owner.serverPlayer?.let { launchPlayersAchievement.awardLater(it) }
        }

        val result = super.doHurtTarget(target)
        target.modifyVelocity(Vec3(0.0, Random.nextDouble(eberVelocityBoost-0.15, eberVelocityBoost+0.15), 0.0))
        return result
    }

}

const val eberKey = "eberlist"

val eberKit = kit("Eber") {
    kitSelectorItem = Items.NETHERITE_INGOT.defaultInstance

    cooldown = 35.0

    description = "Fight with the help of an eber"

    val eberAliveTime by property(7.5, "ebers time alive")
    val eberVelocityBoost by property(1.15, "eber launch velocity")
    val eberSpeed by property(1.5, "eber speed")

    val summonEbersAchievement by achievement("summon ebers") {
        level(5)
        level(50)
        level(1000)
    }

    val eberLaunchPlayersAchievement by achievement("launch players with ebers") {
        level(15)
        level(100)
        level(2500)
    }

    kitItem {
        itemStack = kitSelectorItem.copy()

        onClick { hgPlayer, kit ->
            val world = hgPlayer.serverPlayer?.world ?: return@onClick

            val eber = Eber(world, hgPlayer, eberAliveTime, eberVelocityBoost, eberSpeed, eberLaunchPlayersAchievement).also {
                it.setPos(hgPlayer.serverPlayer!!.position())
            }
            hgPlayer.serverPlayer!!.startRiding(eber)
            world.addFreshEntity(eber)

            summonEbersAchievement.awardLater(hgPlayer.serverPlayer ?: return@onClick)

            hgPlayer.activateCooldown(kit)
        }
    }

    kitEvents {
        onHitEntity(ignoreCooldown = true) { hgPlayer, kit, entity ->
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