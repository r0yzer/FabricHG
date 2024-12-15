package de.royzer.fabrichg.bots

import de.royzer.fabrichg.bots.goals.*
import de.royzer.fabrichg.bots.player.FakeServerPlayer
import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.feast.Feast
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.gulag.GulagManager
import kotlinx.coroutines.delay
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.game.ClientboundDamageEventPacket
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.InteractionHand
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.navigation.PathNavigation
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.monster.Zombie
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import net.silkmc.silk.core.entity.pos
import net.silkmc.silk.core.entity.world
import net.silkmc.silk.core.item.itemStack
import net.silkmc.silk.core.task.mcCoroutineTask
import net.silkmc.silk.core.text.literal
import java.time.Instant
import kotlin.time.Duration.Companion.milliseconds


val botCopys = arrayListOf<ServerPlayer>()

class HGBot(
    world: Level,
    val hgName: String,
    target: ServerPlayer,
    private val range: Double = 1.5,
    val serverPlayer: FakeServerPlayer
) : Zombie(world) {

    init {
        serverPlayer.hgBot = this
        serverPlayer.boundingBox = AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        botCopys.add(serverPlayer)
        this.isInvisible = true
        customName = hgName.literal
        isCustomNameVisible = true
        setTarget(target)
        setItemSlot(EquipmentSlot.MAINHAND, sword.defaultInstance)
        attributes.getInstance(Attributes.FOLLOW_RANGE)?.baseValue = 100.0
        attributes.getInstance(Attributes.MAX_HEALTH)?.baseValue = 20.0
        health = 20.0F
        attributes.getInstance(Attributes.MOVEMENT_SPEED)?.baseValue = 0.3
        attributes.getInstance(Attributes.ATTACK_SPEED)?.baseValue = 10.0
        attributes.getInstance(Attributes.ATTACK_DAMAGE)?.baseValue = 2.5
        attributes.getInstance(Attributes.KNOCKBACK_RESISTANCE)?.baseValue =
            0.5
        sendPlayerInfoUpdatePacket()
    }


    override fun canBeAffected(effectInstance: MobEffectInstance): Boolean {
        return true
    }

    override fun isInvisibleTo(player: Player): Boolean = true

    override fun isInvisible(): Boolean = true

    override fun knockback(strength: Double, x: Double, z: Double) {
        super.knockback(strength, x, z)
    }

    override fun setItemSlot(slot: EquipmentSlot, stack: ItemStack) {
        serverPlayer.setItemSlot(slot, stack)
    }

    private var soups = 50
    var lastAttackedByEntity: Entity? = null

    private var kills = 0
    val armorSlots = EquipmentSlot.entries.filter { it.isArmor }

    private val sword: Item
        get() {
            return if (Feast.started) {
                if (kills == 0) Items.IRON_SWORD
                else Items.DIAMOND_SWORD
            } else {
                if (kills > 2) Items.IRON_SWORD
                else Items.STONE_SWORD
            }
        }

    val air: ItemStack get() = Items.AIR.defaultInstance

    var tracking = false

    val armor: List<ItemStack>
        get() {
            return if (Feast.started) {
                if (kills == 0) listOf(air, Items.IRON_CHESTPLATE.defaultInstance, air, air)
                if (kills == 2) listOf(Items.IRON_HELMET.defaultInstance, air, Items.IRON_LEGGINGS.defaultInstance, air)
                if (kills == 3) listOf(
                    Items.IRON_HELMET.defaultInstance, Items.IRON_CHESTPLATE.defaultInstance, air, air
                )
                if (kills == 4) listOf(
                    Items.DIAMOND_HELMET.defaultInstance, air, Items.DIAMOND_LEGGINGS.defaultInstance, air
                )
                else listOf(air, Items.DIAMOND_CHESTPLATE.defaultInstance, Items.DIAMOND_LEGGINGS.defaultInstance, air)
            } else {
                if (kills > 2) listOf(Items.IRON_CHESTPLATE.defaultInstance)
                else listOf()
            }
        }

    fun kill(player: HGPlayer) {
        soups += player.kills * 2
        kills += 1
    }

    override fun createNavigation(level: Level): PathNavigation {
        return HGBotPathNavigation(this, level)
    }

    fun jump() {
        serverPlayer.jumpFromGround()
        super.jumpFromGround()
    }

    override fun registerGoals() {
        goalSelector.addGoal(2, RandomLookAroundIfNoTargetGoal(this))
        this.addBehaviourGoals()
    }

    override fun addBehaviourGoals() {
        goalSelector.addGoal(1, HGBotAttackGoal(this, 1.0, true))
        goalSelector.addGoal(4, MoveThroughVillageIfNoTargetGoal(
            this, 1.25, false, 4
        ) { this.canBreakDoors() })
        goalSelector.addGoal(3, WaterAvoidingRandomStrollIfNoTargetGoal(this, 1.25, 0.5f))
        goalSelector.addGoal(2, HGBotWalkTowardsFeastIfNoTargetGoal(this))
    }

    override fun tick() {
        if (!isAlive) return

        serverPlayer.moveTo(x, y, z, yRot, xRot)
        serverPlayer.xCloak
        serverPlayer.health = health
        serverPlayer.yHeadRot = yHeadRot
        serverPlayer.yBodyRot = yBodyRot

        if (!tracking && mainHandItem.item != sword && mainHandItem.item != Items.MUSHROOM_STEW) {
            setItemSlot(EquipmentSlot.MAINHAND, sword.defaultInstance)
        }

        if (tracking && mainHandItem.item != Items.MUSHROOM_STEW) {
            setItemSlot(EquipmentSlot.MAINHAND, Items.COMPASS.defaultInstance)
        }

        val targetAlive = target is ServerPlayer && (target as ServerPlayer).hgPlayer.isAlive
        val recalcTargetByHitTime = (tickCount - lastHurtByMobTimestamp.coerceAtLeast(lastHurtByPlayerTime)) > 20 * 10

        if (targetAlive || recalcTargetByHitTime) {
            target = null
        }

        if (GamePhaseManager.currentPhaseType == PhaseType.INGAME && target == null) {
            val distance = if (shouldWalkToFeast()) 45.0 else 250.0
            target = world.getNearestPlayer(this.x,
                this.y,
                this.z,
                distance,
                EntitySelector.NO_CREATIVE_OR_SPECTATOR.and {
                    if (it == this.serverPlayer) false
                    else if (it is FakeServerPlayer && (!GulagManager.isFighting(it))) false
                    else if (GulagManager.isFighting(this)) true
                    else !botCopys.contains(it)
                }
            )
        } else if (GamePhaseManager.currentPhaseType != PhaseType.INGAME) {
            target = null
        }

        super.tick()
    }

    override fun animateHurt(yaw: Float) {
        super.animateHurt(yaw)
    }

    override fun swing(hand: InteractionHand, updateSelf: Boolean) {
        serverPlayer.swing(hand, updateSelf)
        super.swing(hand, updateSelf)
    }

    override fun isWithinMeleeAttackRange(entity: LivingEntity): Boolean {
        return this.attackBoundingBox.intersects(entity.boundingBox.inflate(range))
    }

    private suspend fun soup() {
        soups--
        setItemSlot(EquipmentSlot.MAINHAND, itemStack(Items.MUSHROOM_STEW) {})
        delay(85.milliseconds)
        setItemSlot(EquipmentSlot.MAINHAND, itemStack(Items.BOWL) {})
        world.addFreshEntity(ItemEntity(EntityType.ITEM, level()).apply {
            setPos(this@HGBot.pos)
            item = ItemStack(Items.BOWL, 1)
        })
        health += 3.5f
        setItemSlot(EquipmentSlot.MAINHAND, itemStack(Items.AIR) {})
        delay(75.milliseconds)
        setItemSlot(EquipmentSlot.MAINHAND, sword.defaultInstance)
    }

    override fun hurt(damageSource: DamageSource, f: Float): Boolean {
        println("hgbot hurting: $f")
        val result = super.hurt(damageSource, f)
        if(result) server?.playerList?.broadcastAll(ClientboundDamageEventPacket(serverPlayer, damageSource))
        lastAttackedByEntity = damageSource.entity
        if(health<=0.0f) {
            serverPlayer.health = serverPlayer.health
            serverPlayer.hurtFromHGBot(damageSource, f)
            die(damageSource)
            return result
        }
            mcCoroutineTask(true) {
                while (health < 17 && soups > 0) {
                    val soupDelay = if (soups % 8 == 0) 1000 else 125
                    delay(soupDelay.milliseconds)
                    soup()
                }
            }

        return result
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

    override fun dropFromLootTable(damageSource: DamageSource, hitByPlayer: Boolean) {
    }

    override fun dropCustomDeathLoot(serverLevel: ServerLevel, damageSource: DamageSource, hitByPlayer: Boolean) {
        if (!hitByPlayer) return;
        repeat(soups) {
            spawnAtLocation(Items.MUSHROOM_STEW.defaultInstance)
        }
        spawnAtLocation(sword.defaultInstance)
    }

    override fun die(damageSource: DamageSource) {
        println("hgbot die")
        super.die(damageSource)
        remove(RemovalReason.KILLED)
        serverPlayer.justDie(damageSource)
    }

    override fun remove(reason: RemovalReason) {
        println("remove: $reason")

        //super.remove(reason)
    }

    override fun kill() {
        println("hgbot kill")
        super.kill()
    }

    override fun shouldDespawnInPeaceful(): Boolean {
        return false
    }

    override fun checkDespawn() {
    }

    fun shouldWalkToFeast(): Boolean {
        if (target != null) return false
        if (!Feast.started) return false
        if ((Instant.now().toEpochMilli() - (Feast.feastTimestamp?.toEpochMilli()
                ?: Long.MAX_VALUE)) > 1000 * 60
        ) return false

        return true
    }

    fun hasLineOfSight(position: Vec3): Boolean {
        val vec3 = this.pos
        return if (position.distanceTo(vec3) > 128.0) {
            false
        } else {
            level().clip(
                ClipContext(
                    vec3, position, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this
                )
            ).type == HitResult.Type.MISS
        }
    }

    private fun sendPlayerInfoUpdatePacket() {
        server?.playerList?.broadcastAll(
            ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(
                listOf(
                    // fakePlayer
                )
            )
        )
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        super.addAdditionalSaveData(compound)
        compound.putBoolean("hgbot", true)
    }

}

