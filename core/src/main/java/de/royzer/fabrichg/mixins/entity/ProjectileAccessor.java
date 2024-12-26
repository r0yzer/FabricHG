package de.royzer.fabrichg.mixins.entity;

import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Projectile.class)
public interface ProjectileAccessor {
    @Invoker("onHitEntity")
    void onHitEntityInvoker(EntityHitResult entityHitResult);
}
