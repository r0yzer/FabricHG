package de.royzer.fabrichg.mixins.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageRecord;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;


@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Accessor("attackingPlayer")
    PlayerEntity getAttackingPlayer();

    @Invoker("tryUseTotem")
    boolean invokeTryUseTotem(DamageSource source);
}
