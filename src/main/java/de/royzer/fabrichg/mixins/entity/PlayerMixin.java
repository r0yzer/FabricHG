package de.royzer.fabrichg.mixins.entity;

import de.royzer.fabrichg.kit.events.kit.invoker.OnSneakKt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

    @Shadow public abstract float getAttackStrengthScale(float adjustTicks);

    @Shadow protected abstract float getEnchantedDamage(Entity entity, float damage, DamageSource damageSource);

    @Unique
    private Entity brianBusting;

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Redirect(
            method = "updatePlayerPose",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;setPose(Lnet/minecraft/world/entity/Pose;)V")
    )
    void abc(Player instance, Pose pose) {
        if (instance.getPose() != Pose.CROUCHING && pose == Pose.CROUCHING) {
            OnSneakKt.onSneak(instance, pose);
        }
        instance.setPose(pose);
    }

    @Inject(
            method = "attack",
            at = @At("HEAD")
    )
    private void helm(Entity target, CallbackInfo ci) {
        this.brianBusting = target; // Store the target entity
    }

    @ModifyArgs(
            method = "attack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z")
    )
    public void brainBusting(Args args) {
        // ASKLFDJLFDJLFDjjefdooooooodsfgahjkvhhhhh werD OKSALftr ioASD JSJ agfio FSADF

        float f1 = this.isAutoSpinAttack() ? this.autoSpinAttackDmg : (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        DamageSource damageSource = this.damageSources().playerAttack((Player) (Object) this);
        float g1 = this.getEnchantedDamage(brianBusting, f1, damageSource) - f1;
        float h1 = this.getAttackStrengthScale(0.5F);
        float f2 = f1 * (0.2F + h1 * h1 * 0.8F);
        float g2 = g1 * h1;

        float givenDamage = args.get(1);

        boolean isCrit = (f2 + g2) != givenDamage;

        if (isCrit) {
            float newDamage = givenDamage * 0.85f;
            args.set(1, newDamage);
        }
    }
}
