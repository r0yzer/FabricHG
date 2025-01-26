import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.property.property
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import kotlin.random.Random

val viperKit = kit("Viper") {
    kitSelectorItem = ItemStack(Items.SPIDER_EYE)

    usableInInvincibility = false
    description = "Poison your enemies when hitting them"

    val maxInt by property(4, "max int (0 = always poison)")
    val poisonSeconds by property(3, "seconds poisoned")
    val poisonLevel by property(0, "poison level (0 = Poison I)")

    kitEvents {
        onHitEntity { _, _, entity ->

            val livingEntity = entity as? LivingEntity ?: return@onHitEntity


            if ((livingEntity as? ServerPlayer)?.hgPlayer?.isNeo == true) return@onHitEntity


            if (Random.nextInt(maxInt) == 0) {
                livingEntity.addEffect(MobEffectInstance(MobEffects.POISON, poisonSeconds * 20, poisonLevel))
            }
        }
    }
}