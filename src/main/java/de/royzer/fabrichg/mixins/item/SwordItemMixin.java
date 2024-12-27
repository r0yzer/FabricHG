package de.royzer.fabrichg.mixins.item;

import kotlin.random.Random;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SwordItem.class)
public abstract class SwordItemMixin extends TieredItem {
    public SwordItemMixin(Tier tier, Properties properties) {
        super(tier, properties);
    }

    @Inject(method = "postHurtEnemy", at = @At("HEAD"), cancellable = true)
    public void breakReduction(ItemStack stack, LivingEntity target, LivingEntity attacker, CallbackInfo ci) {
        double breakReduction = 0.5;

        if (Random.Default.nextDouble() <= breakReduction) {
            ci.cancel();
        }
    }

    @Inject(method = "createAttributes", at = @At("HEAD"), cancellable = true)
    private static void modifyAttributes(Tier tier, int attackDamage, float attackSpeed, CallbackInfoReturnable<ItemAttributeModifiers> cir) {
        cir.setReturnValue(ItemAttributeModifiers.builder().add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, (double)(-6), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, (double)attackSpeed, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).build());
        cir.cancel();
    }
}
