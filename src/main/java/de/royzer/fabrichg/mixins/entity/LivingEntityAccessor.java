package de.royzer.fabrichg.mixins.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;


@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Accessor("lastHurtByPlayer")
    Player getAttackingPlayer();

    @Invoker("checkTotemDeathProtection")
    boolean invokeTryUseTotem(DamageSource source);
}
