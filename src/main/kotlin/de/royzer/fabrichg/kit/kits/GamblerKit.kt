package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.commands.revive
import de.royzer.fabrichg.data.hgplayer.HGPlayerStatus
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.broadcast
import de.royzer.fabrichg.game.broadcastComponent
import de.royzer.fabrichg.kit.Kit
import de.royzer.fabrichg.kit.achievements.delegate.achievement
import de.royzer.fabrichg.kit.cooldown.activateCooldown
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.randomKit
import de.royzer.fabrichg.server
import de.royzer.fabrichg.util.WeightedCollection
import de.royzer.fabrichg.util.giveOrDropItem
import net.minecraft.core.Vec3i
import net.minecraft.core.component.DataComponents
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.Filterable
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.targeting.TargetingConditions
import net.minecraft.world.entity.animal.IronGolem
import net.minecraft.world.entity.animal.MushroomCow
import net.minecraft.world.entity.animal.Wolf
import net.minecraft.world.entity.animal.horse.Horse
import net.minecraft.world.entity.boss.wither.WitherBoss
import net.minecraft.world.entity.item.PrimedTnt
import net.minecraft.world.entity.monster.Creeper
import net.minecraft.world.entity.monster.hoglin.Hoglin
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.vehicle.Boat
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.WrittenBookContent
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.LecternBlock
import net.minecraft.world.level.block.entity.LecternBlockEntity
import net.silkmc.silk.core.entity.blockPos
import net.silkmc.silk.core.entity.modifyVelocity
import net.silkmc.silk.core.entity.pos
import net.silkmc.silk.core.entity.world
import net.silkmc.silk.core.item.itemStack
import net.silkmc.silk.core.math.vector.plus
import net.silkmc.silk.core.task.infiniteMcCoroutineTask
import net.silkmc.silk.core.task.mcCoroutineTask
import net.silkmc.silk.core.text.literal
import net.silkmc.silk.core.text.literalText
import net.silkmc.silk.core.text.sendText
import java.util.logging.Filter
import kotlin.math.pow
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

class GamingGolem(level: Level, val wolf: GamingGolemWolf) : IronGolem(EntityType.IRON_GOLEM, level) {
    init {
        attributes.getInstance(Attributes.ATTACK_DAMAGE)?.baseValue = 7.5 // 15 normal
        customName = literalText {
            text(wolf.owner.name.string) { color = TEXT_BLUE }
            text("'s Gaming Golem")
        }
    }

    override fun tick() {
        super.tick()

        teleportTo(wolf.x, wolf.y, wolf.z)
        setRot(wolf.xRot, wolf.yRot)

        if (!wolf.isAlive) {
            kill()
            remove(RemovalReason.KILLED)
        }
    }

    override fun canAttack(target: LivingEntity): Boolean {
        return wolf.canAttack(target)
    }

    override fun canAttack(livingentity: LivingEntity, condition: TargetingConditions): Boolean {
        return wolf.canAttack(livingentity, condition)
    }
}

class GamingGolemWolf(level: Level, val owner: ServerPlayer) : Wolf(EntityType.WOLF, level) {
    val golem = GamingGolem(level, this).also {
        level.addFreshEntity(it)
    }

    init {
        isInvisible = true

    }

    override fun tick() {
        isInvisible = true
        health = maxHealth
        golem.target = target

        if (golem.isDeadOrDying || golem.isRemoved) {
            kill()
            remove(RemovalReason.KILLED)
        }
        super.tick()
    }

    override fun canAttack(livingentity: LivingEntity, condition: TargetingConditions): Boolean {
        return livingentity != golem && livingentity != this && livingentity != owner
    }

    override fun canAttack(target: LivingEntity): Boolean {
        return target != golem && target != this && target != owner
    }

    override fun doHurtTarget(target: Entity): Boolean {
        return false
    }

    override fun canBeCollidedWith(): Boolean {
        return false
    }

    override fun hurt(source: DamageSource, amount: Float): Boolean {
        return false
    }


    override fun getAmbientSound(): SoundEvent? {
        return SoundEvents.IRON_GOLEM_ATTACK
    }

    override fun getDeathSound(): SoundEvent? {
        return SoundEvents.IRON_GOLEM_DEATH
    }

    override fun getHurtSound(damageSource: DamageSource): SoundEvent? {
        return SoundEvents.IRON_GOLEM_HURT
    }

    override fun getSwimSound(): SoundEvent? {
        return SoundEvents.GENERIC_SWIM
    }

    override fun getSwimSplashSound(): SoundEvent? {
        return SoundEvents.GENERIC_SPLASH
    }

    override fun getSwimHighSpeedSplashSound(): SoundEvent? {
        return SoundEvents.GENERIC_SPLASH
    }

    override fun getFallSounds(): Fallsounds {
        return Fallsounds(SoundEvents.GENERIC_SMALL_FALL, SoundEvents.GENERIC_BIG_FALL)
    }
}


val gamblerKit = kit("Gambler") {
    kitSelectorItem = Items.OAK_BUTTON.defaultInstance

    cooldown = 30.0

    description = "Test your luck"

    val goodGamblerAchievement by achievement("lucky gambler") {
        level(200)
        level(1000)
        level(2500)
    }
    val diaGamblerAchievement by achievement("gamble diamonds") {
        level(30)
        level(100)
        level(700)
    }

    kitItem {
        itemStack = kitSelectorItem
        onClick { hgPlayer, kit ->
            val good = Random.nextBoolean()
            val loot = (if (good) goodGambler.get() else badGambler.get()) ?: return@onClick
            val serverPlayer = hgPlayer.serverPlayer ?: return@onClick

            if (loot.text == "You won a full diamond kit") {
                diaGamblerAchievement.awardLater(serverPlayer, 26)
            } else if (loot.text == "You won a diamond") {
                diaGamblerAchievement.awardLater(serverPlayer, 26)
            }
            loot.action.invoke(serverPlayer)
            serverPlayer.sendText {
                text(loot.text)
                color = if (good) 0x46FF38 else 0xFF2530
            }

            hgPlayer.activateCooldown(kit)
        }
    }
}


private val goodGambler = WeightedCollection<GamblerAction>().also { collection ->
    collection.add(GamblerAction("You won a full diamond kit") {
        it.giveOrDropItem(itemStack(Items.DIAMOND_SWORD, 1) {})
        it.giveOrDropItem(itemStack(Items.DIAMOND_HELMET, 1) {})
        it.giveOrDropItem(itemStack(Items.DIAMOND_CHESTPLATE, 1) {})
        it.giveOrDropItem(itemStack(Items.DIAMOND_LEGGINGS, 1) {})
        it.giveOrDropItem(itemStack(Items.DIAMOND_BOOTS, 1) {})
    }, 0.01)
    collection.add(GamblerAction("You won a gaming golem") {
        val golemWolf = GamingGolemWolf(it.level(), it)
        it.level().addFreshEntity(golemWolf)
        golemWolf.tame(it)
        golemWolf.setPos(it.pos)
        golemWolf.golem.setPos(it.pos)
    }, 0.1)
    collection.add(GamblerAction("You won a diamond") {
        it.giveOrDropItem(itemStack(Items.DIAMOND, 1) {})
    }, 0.075)
    collection.add(GamblerAction("You won a iron ingot") {
        it.giveOrDropItem(itemStack(Items.IRON_INGOT, 1) {})
    }, 0.2)
    collection.add(GamblerAction("You won a iron sword") {
        it.giveOrDropItem(itemStack(Items.IRON_SWORD, 1) {})
    }, 0.1)
    collection.add(GamblerAction("You won wood") {
        it.giveOrDropItem(itemStack(Items.OAK_PLANKS, 32) {})
    }, 0.8)
    collection.add(GamblerAction("You a full chain set") {
        it.giveOrDropItem(itemStack(Items.CHAINMAIL_HELMET, 1) {})
        it.giveOrDropItem(itemStack(Items.CHAINMAIL_CHESTPLATE, 1) {})
        it.giveOrDropItem(itemStack(Items.CHAINMAIL_LEGGINGS, 1) {})
        it.giveOrDropItem(itemStack(Items.CHAINMAIL_BOOTS, 1) {})
        it.giveOrDropItem(itemStack(Items.STONE_SWORD, 1) {})
    }, 0.20)
    collection.add(GamblerAction("You a full gold set") {
        it.giveOrDropItem(itemStack(Items.GOLDEN_HELMET, 1) {})
        it.giveOrDropItem(itemStack(Items.GOLDEN_CHESTPLATE, 1) {})
        it.giveOrDropItem(itemStack(Items.GOLDEN_LEGGINGS, 1) {})
        it.giveOrDropItem(itemStack(Items.GOLDEN_BOOTS, 1) {})
        it.giveOrDropItem(itemStack(Items.GOLDEN_SWORD, 1) {})
    }, 0.3)
    collection.add(GamblerAction("You a full leather set") {
        it.giveOrDropItem(itemStack(Items.LEATHER_HELMET, 1) {})
        it.giveOrDropItem(itemStack(Items.LEATHER_CHESTPLATE, 1) {})
        it.giveOrDropItem(itemStack(Items.LEATHER_LEGGINGS, 1) {})
        it.giveOrDropItem(itemStack(Items.LEATHER_BOOTS, 1) {})
        it.giveOrDropItem(itemStack(Items.WOODEN_SWORD, 1) {})
    }, 0.4)
    collection.add(GamblerAction("Coco farm") {
        it.giveOrDropItem(itemStack(Items.JUNGLE_LOG, 3) {})
        it.giveOrDropItem(itemStack(Items.COCOA_BEANS, 12) {})
    }, 0.2)
    collection.add(GamblerAction("Horse") {
        val pferd = Horse(EntityType.HORSE, it.level())
        pferd.isTamed = true
        it.level().addFreshEntity(pferd)
        it.giveOrDropItem(Items.SADDLE.defaultInstance)
        pferd.setPos(it.pos)
    }, 0.2)
    collection.add(GamblerAction("You won strength") {
        it.addEffect(MobEffectInstance(MobEffects.DAMAGE_BOOST, 20 * 15))
    }, 0.4)
    collection.add(GamblerAction("You won jump boost") {
        it.addEffect(MobEffectInstance(MobEffects.JUMP, 20 * 15, 3))
    }, 0.5)
    collection.add(GamblerAction("You won resistance") {
        it.addEffect(MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 15, 0))
    }, 0.5)
    collection.add(GamblerAction("You won a Beer") {
        beerPotions.values.random().also { beer ->
            it.giveOrDropItem(beer.copy())
        }
    }, 0.45)
    collection.add(GamblerAction("Extra heart") {
        val max = it.attributes.getBaseValue(Attributes.MAX_HEALTH)
        it.attributes.getInstance(Attributes.MAX_HEALTH)?.baseValue = max + 2.0
    }, 0.1)
    collection.add(GamblerAction("You won some recraft") {
        val amount = 2f.pow(Random.nextInt(1, 5)).toInt()
        it.giveOrDropItem(itemStack(Items.BOWL, amount) {})
        it.giveOrDropItem(itemStack(Items.RED_MUSHROOM, amount) {})
        it.giveOrDropItem(itemStack(Items.BROWN_MUSHROOM, amount) {})
    }, 0.5)
    collection.add(GamblerAction("You won a friend") {
        val wolf = Wolf(EntityType.WOLF, it.level())
        wolf.tame(it)
        it.level().addFreshEntity(wolf)
        wolf.setPos(it.pos)
    }, 0.2)
    collection.add(GamblerAction("Random kill") {
        val killed = PlayerList.alivePlayers.random()
        val serverPlayer = killed.serverPlayer ?: return@GamblerAction
        broadcastComponent(literalText {
            text("${killed.name} was randomkilled by a gambler")
            color = 0xFFFF55
        })
        serverPlayer.hurt(serverPlayer.damageSources().playerAttack(serverPlayer), 1000f)
    }, 0.0025)
    collection.add(GamblerAction("OP") {
        server.playerList.op(it.gameProfile)
        broadcast("tmm")
    }, 0.000001)
    collection.add(GamblerAction("Helm") {
        it.inventory.armor[3] = Items.IRON_HELMET.defaultInstance
    }, 0.1)
//    collection.add(GamblerAction("5 Sekunden fly") {
//        it.abilities.mayfly = true
//        it.connection.send(ClientboundPlayerAbilitiesPacket(it.abilities))
//        mcCoroutineTask(delay = 5.seconds) { _ ->
//            if (it != null) {
//                it.abilities.mayfly = false
//                it.connection.send(ClientboundPlayerAbilitiesPacket(it.abilities))
//            }
//        }
//    }, 0.1)
    collection.add(GamblerAction("End crystal") {
        it.giveOrDropItem(itemStack(Items.END_CRYSTAL, 1) {})
        it.giveOrDropItem(itemStack(Items.OBSIDIAN, 1) {})
    }, 0.05)
    collection.add(GamblerAction("Bow") {
        it.giveOrDropItem(itemStack(Items.BOW, 1) {})
        it.giveOrDropItem(itemStack(Items.ARROW, 5) {})
    }, 0.25)
    collection.add(GamblerAction("TNT") {
        it.giveOrDropItem(itemStack(Items.TNT, 1) {})
        it.giveOrDropItem(itemStack(Items.FLINT_AND_STEEL, 1) {})
    }, 0.2)
    collection.add(GamblerAction("Double bed") {
        it.giveOrDropItem(itemStack(Items.RED_BED, 1) {})
        it.giveOrDropItem(itemStack(Items.RED_BED, 1) {})
    }, 0.2)
    collection.add(GamblerAction("Totem") {
        it.giveOrDropItem(itemStack(Items.TOTEM_OF_UNDYING, 1) {})
    }, 0.01)
    collection.add(GamblerAction("Enderpearl") {
        it.giveOrDropItem(itemStack(Items.ENDER_PEARL, 1) {})
    }, 0.1)
    collection.add(GamblerAction("Wind charge") {
        it.giveOrDropItem(itemStack(Items.WIND_CHARGE, 3) {})
    }, 0.03)
    collection.add(GamblerAction("Boat") {
        val boat = Boat(EntityType.BOAT, it.level())
        it.level().addFreshEntity(boat)
        boat.setPos(it.pos)
    }, 0.2)
    collection.add(GamblerAction("Mooshroom") {
        val mooshroom = MushroomCow(EntityType.MOOSHROOM, it.level())
        it.level().addFreshEntity(mooshroom)
        mooshroom.setPos(it.pos)
    }, 0.075)
    collection.add(GamblerAction("Enchanter") {
        it.giveOrDropItem(itemStack(Items.ENCHANTING_TABLE, 1) {})
        it.giveOrDropItem(itemStack(Items.LAPIS_LAZULI, 16) {})
        it.giveOrDropItem(itemStack(Items.EXPERIENCE_BOTTLE, 16) {})
    }, 0.02)
}


private val badGambler = WeightedCollection<GamblerAction>().also { collection ->
    collection.add(GamblerAction("You won dirt...") {
        it.giveOrDropItem(itemStack(Items.DIRT, 16) {})
    }, 0.4)
    collection.add(GamblerAction("You won poision...") {
        it.addEffect(MobEffectInstance(MobEffects.POISON, 20 * 10))
    }, 0.4)
    collection.add(GamblerAction("You won nausea...") {
        it.addEffect(MobEffectInstance(MobEffects.CONFUSION, 20 * 10))
    }, 0.4)
    collection.add(GamblerAction("You won levitation") {
        it.addEffect(MobEffectInstance(MobEffects.LEVITATION, 20 * 10))
    }, 0.4)
    collection.add(GamblerAction("You won some wheat") {
        it.giveOrDropItem(itemStack(Items.WHEAT, 10 + Random.nextInt(-10, 10)) {})
    }, 0.4)
    collection.add(GamblerAction("Hoe") {
        it.giveOrDropItem(itemStack(Items.NETHERITE_HOE, 1) {})
    }, 0.075)
    collection.add(GamblerAction("Eggs") {
        it.giveOrDropItem(itemStack(Items.EGG, 16) {})
    }, 0.2)
    collection.add(GamblerAction("You may want to look above you...") {
        it.world.setBlockAndUpdate(it.blockPos.subtract(Vec3i(0, -15, 0)), Blocks.ANVIL.defaultBlockState())
    }, 0.075)
    collection.add(GamblerAction("Instant death") {
        it.kill()
    }, 0.005)
    collection.add(GamblerAction("Kit change") {
        val index = it.hgPlayer.kits.indexOfFirst { kit -> kit.name == "Gambler" } // indexOf(gamblerKit) rekursive problem
        if (index < 0) {
            if (it.hgPlayer.getPlayerData<Kit>(BANDIT_KIT_KEY)?.name == "Gambler") {
                it.hgPlayer.playerData.remove(BANDIT_KIT_KEY)
            }
            return@GamblerAction
        }
        val kit = randomKit()
        it.hgPlayer.kits[index] = kit
        kit.onEnable?.invoke(it.hgPlayer, kit, it)
        it.hgPlayer.giveKitItems(kit)
    }, 0.02)
    collection.add(GamblerAction("Coords leak") {
        broadcastComponent(
            literalText {
                text("${it.name.string}´s coordinates are: ") { color = TEXT_GRAY }
                text("${it.pos.x.toInt()} ${it.pos.y.toInt()} ${it.pos.z.toInt()}") {
                    color = TEXT_BLUE
                    bold = true
                }
            }
        )
    }, 0.15)
    collection.add(GamblerAction("Nothing") {}, 0.2)
    collection.add(GamblerAction("Time to read") {
        var blockState = Blocks.LECTERN.defaultBlockState()
        it.world.setBlockAndUpdate(it.blockPos, blockState)
        blockState = it.world.getBlockState(it.blockPos) ?: error("brain=!")

        val book = itemStack(Items.WRITTEN_BOOK) {
            set(
                DataComponents.WRITTEN_BOOK_CONTENT, WrittenBookContent(
                    Filterable.passThrough("Literatur"),
                    it.name.string,
                    0,
                    listOf(Filterable.passThrough(busterMessages.random().literal)),
                    true
                )
            )
        }

        LecternBlock.tryPlaceBook(it, it.world, it.blockPos, blockState, book)
    }, 0.15)
    collection.add(GamblerAction("Inventory clear") {
        it.inventory.clearContent()
        it.hgPlayer.giveKitItems()
    }, 0.004)
    collection.add(GamblerAction("Halbes") {
        it.health = 1f
    }, 0.04)
    collection.add(GamblerAction("Eber") {
        val eber = Hoglin(EntityType.HOGLIN, it.level())
        it.level().addFreshEntity(eber)
        eber.setPos(it.pos)
    }, 0.06)
    collection.add(GamblerAction("Wither") {
        val wither = WitherBoss(EntityType.WITHER, it.level())
        it.level().addFreshEntity(wither)
        wither.setPos(it.pos)
    }, 0.000327)
    collection.add(GamblerAction("MLG") {
        val before = it.inventory.getSelected()
        val slot = it.inventory.selected
        it.inventory.setItem(slot, Items.WATER_BUCKET.defaultInstance)
        it.teleportTo(it.x, it.y + 30, it.z)
        mcCoroutineTask(delay = 4.seconds) { _ ->
            it.inventory.setItem(slot, before)
        }
    }, 0.05)
    collection.add(GamblerAction("Random tp") {
        val random = PlayerList.alivePlayers.random()
        val serverPlayer = random.serverPlayer ?: run {
            it.sendText {
                text("You were lucky. The player you wanted to teleport to is disconnected.")
                color = TEXT_GRAY
            }
            return@GamblerAction
        }
        it.teleportTo(serverPlayer.x, serverPlayer.y, serverPlayer.z)
    }, 0.07)
    collection.add(GamblerAction("Random swap") {
        val random = PlayerList.alivePlayers.random()
        val serverPlayer = random.serverPlayer ?: run {
            it.sendText {
                text("You were lucky. The player you wanted to swap with is disconnected.")
                color = TEXT_GRAY
            }
            return@GamblerAction
        }
        serverPlayer.sendText {
            text("You were randomly swapped with a gambler")
            color = TEXT_GRAY
        }
        val pos = it.pos.add(0.0, 0.0, 0.0) // eigentlich muss man nicht kopieren
        it.teleportTo(serverPlayer.x, serverPlayer.y, serverPlayer.z)
        serverPlayer.teleportTo(pos.x, pos.y, pos.z)
    }, 0.01)
    collection.add(GamblerAction("Creeper") {
        val creeper = Creeper(EntityType.CREEPER, it.level())
        it.level().addFreshEntity(creeper)
        creeper.setPos(it.pos)
    }, 0.075)
    collection.add(GamblerAction("Pumpkin head") {
        it.inventory.armor[3] = Items.CARVED_PUMPKIN.defaultInstance
    }, 0.175)
    collection.add(GamblerAction("Dragon head") {
        it.inventory.armor[3] = Items.DRAGON_HEAD.defaultInstance
    }, 0.075)
    collection.add(GamblerAction("You won kelp..") {
        it.giveOrDropItem(itemStack(Items.DRIED_KELP, 16) {})
    }, 0.4)
    collection.add(GamblerAction("Lava") {
        it.world.setBlockAndUpdate(it.blockPos, Blocks.LAVA.defaultBlockState())
    }, 0.15)
    collection.add(GamblerAction("Cobweb") {
        it.world.setBlockAndUpdate(it.blockPos, Blocks.COBWEB.defaultBlockState())
    }, 0.2)
    collection.add(GamblerAction("Random boost") {
        it.modifyVelocity(Random.nextFloat() * 2, Random.nextFloat() / 2, Random.nextFloat() * 2)
    }, 0.05)
    collection.add(GamblerAction("Slime") {
        it.giveOrDropItem(itemStack(Items.SLIME_BLOCK, 1) {})
    }, 0.25)
    collection.add(GamblerAction("TNT") {
        val tnt = PrimedTnt(EntityType.TNT, it.level())
        it.level().addFreshEntity(tnt)
        tnt.setPos(it.pos)
    }, 0.1)
    collection.add(GamblerAction("Random spectator revive") {
        val player =
            server.playerList.players.filter { it.hgPlayer.status == HGPlayerStatus.SPECTATOR }.randomOrNull() ?: run {
                it.sendSystemMessage(literalText("There is no spectator online..") { color = TEXT_GRAY })
                return@GamblerAction
            }
        player.hgPlayer.revive(gambler = true)
    }, 0.01)
}


private data class GamblerAction(
    val text: String,
    val action: ((ServerPlayer) -> Unit),
)

private val busterMessages = listOf<String>(
    "Man müsste ein video machen in dem man sich übers gendern beschwer und alle linken die drunter kommentieren in einen mixer werfen die masse härten lassen und ein boxautomat draus machen",
    "Nie wieder scheisshaus irschenberg ich bin am scheissen klobrille macht 360",
    "meine eier stecken gerade unter einem lkw fest",
    "Ich mach eine tankstelle auf und aus dem super zapfhahn kommt diesel",
    "Hab mal eine kamera aufgestellt und gesehen dass ich nachts beim schlafwandeln jedem nachbar von mir sage er solle wo anders waren, er mache meine tür heiß",
    "Kennt ihr das wenn ihr zeit messen müsst und abmesst wie oft nizi19s tür heiß gemacht wird",
    "https://www.yallashoot.video/video/germany-vs-netherlands-live-stream-26-3-2024/",
    "Ich lade seit 3 jahren bastighg videos mit dubiosen titeln auf sämtlichen porno seiten hoch und verdiene mir ein gutes nebeneinkommen",
    "was ist wenn wir alle spermien in irgendwelchen eiern sind und die so krass sind dass die pcs in ihren eiern haben",
    "es gibt nix schlimmeeres als wenn man warm duscht oder so und dann nachdem man das wasser ausgestellt hat so ein kalter tropfen auf deinen schwanz fällt",
    "Ich bin gerade so auf achse vorhin noch tille eingeschmissen anders müde kann nicht einschlafen danke frau merkel und ich geh noch behindert wegen Reservierung fixkt die GRÜNEN",
    "ich habe mich bei homag eingeschlichen und die nächste maschine heisst homag hoden",
    "ich leide seit 3 jahren an akkuter gynokonose",
    "Ich fahre mit dem auto nach münchen damit die polizei systematisch jede kleinfamilie für die nächsten 3 stunden durchsuchr",
    "ich arbeite bei gehirn25 und bin damit ruehl24s grösster konkurent",
    "Ich werde von 2 fliegen belästigt @bluefireoly cancel die mal du bist doch links",
    "ich habe mich beim bestellen flerschrieben kann mir jemand helfen eine 120m lange musikbox in den kofferraum zu stecken",
    "ich besitze ein 50ccm roller und es schiesst mein arschloch zum mars",
    "wenn für jedes kilo grammm wichse ein kind geboren wird aber frauen nicht mehr funktionieren gibt es mehr oder weniger kinder als davor",
    "es ist offiziell hanybal lässt sich die eier vermöbeln bevor er anfängt zu rappen",
    "hätte bayreuther nicht eine scheissrate von 25% wäre das eines der besten biere jeden 4. kasten kann man nicht trinken",
    "ich hab jahre lang vorm schlafen mein bett zu ner couch geklappt aber gestern habe ich gemerkt dass man das andersru machen sollte",
    "Falls ihr während ich dusche im nebenzimmer seit und komische geräudche hört, ich wichse nicht ich ficke einfach die wand",
    "wenn bluefireoly ein 0kmh schild sieht weiss er sich nicht zu helfen als sich ein neues auto zu kaufen",
)