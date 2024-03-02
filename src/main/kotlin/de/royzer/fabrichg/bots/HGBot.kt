package de.royzer.fabrichg.bots

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import kotlinx.coroutines.delay
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.monster.Monster
import net.minecraft.world.entity.monster.Zombie
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.silkmc.silk.core.entity.pos
import net.silkmc.silk.core.entity.world
import net.silkmc.silk.core.item.itemStack
import net.silkmc.silk.core.task.mcCoroutineTask
import net.silkmc.silk.core.text.literal
import kotlin.time.Duration.Companion.milliseconds

class HGBot(
    world: Level,
    name: String,
    target: ServerPlayer,
    range: Int = 10,
): Zombie(world) {

    init {
        customName = name.literal
        isCustomNameVisible = true
        setTarget(target)

        setItemSlot(EquipmentSlot.MAINHAND, itemStack(Items.STONE_SWORD){})
        setItemSlot(EquipmentSlot.HEAD, itemStack(Items.PLAYER_HEAD){})
        attributes.getInstance(Attributes.FOLLOW_RANGE)?.baseValue = 100.0
        attributes.getInstance(Attributes.MAX_HEALTH)?.baseValue = 200.0
        health = 200.0F
        attributes.getInstance(Attributes.MOVEMENT_SPEED)?.baseValue = 0.4
        attributes.getInstance(Attributes.ATTACK_SPEED)?.baseValue = 10.0
        attributes.getInstance(Attributes.ATTACK_DAMAGE)?.baseValue = 4.0
    }



    override fun tick() {
        super.tick()
        if (target == null || ((target as? ServerPlayer)?.hgPlayer?.isAlive) == true) {
            target = world.getNearestPlayer(this, 20.0)
        }
    }

    override fun hurt(damageSource: DamageSource, f: Float): Boolean {
        if (f > 2) return super.hurt(damageSource, f)
        mcCoroutineTask(true, delay = 100.milliseconds) {
            setItemSlot(EquipmentSlot.MAINHAND, itemStack(Items.MUSHROOM_STEW){})
            delay(20.milliseconds)
            setItemSlot(EquipmentSlot.MAINHAND, itemStack(Items.BOWL){})
            delay(100.milliseconds)
            setItemSlot(EquipmentSlot.MAINHAND, itemStack(Items.STONE_SWORD){})
        }
        return super.hurt(damageSource, f)
    }


    override fun canCollideWith(entity: Entity): Boolean {
        return false
    }

    override fun doHurtTarget(entity: Entity): Boolean {
        return super.doHurtTarget(entity)
    }

    override fun convertsInWater(): Boolean {
        return false
    }

    override fun canBeCollidedWith(): Boolean {
        return false
    }

    override fun getAmbientSound(): SoundEvent? {
        return null
    }

    override fun getStepSound(): SoundEvent {
        return blockStateOn.soundType.stepSound
    }

    override fun getDeathSound(): SoundEvent? {
        return SoundEvents.PLAYER_DEATH
    }

    override fun getHurtSound(damageSource: DamageSource): SoundEvent? {
        return SoundEvents.PLAYER_HURT
    }

    override fun getSwimSound(): SoundEvent {
        return SoundEvents.PLAYER_SWIM
    }

    override fun getSwimSplashSound(): SoundEvent {
        return SoundEvents.PLAYER_SPLASH
    }

    override fun getSwimHighSpeedSplashSound(): SoundEvent {
        return SoundEvents.PLAYER_SPLASH_HIGH_SPEED
    }


    override fun getFallSounds(): Fallsounds {
        return Fallsounds(SoundEvents.PLAYER_SMALL_FALL, SoundEvents.PLAYER_SMALL_FALL)
    }
    override fun isSunSensitive(): Boolean {
        return false
    }

    override fun die(damageSource: DamageSource) {
        world.addFreshEntity(ItemEntity(EntityType.ITEM, level()).apply {
            setPos(this@HGBot.pos)
            item = ItemStack(Items.MUSHROOM_STEW, 5)
        })
        super.die(damageSource)
    }
}