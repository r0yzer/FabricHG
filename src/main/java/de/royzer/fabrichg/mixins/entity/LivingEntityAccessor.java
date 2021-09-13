package de.royzer.fabrichg.mixins.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageRecord;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;


@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Accessor("attackingPlayer")
    PlayerEntity getAttackingPlayer();
}
