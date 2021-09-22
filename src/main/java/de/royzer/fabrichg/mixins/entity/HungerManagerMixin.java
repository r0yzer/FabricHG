package de.royzer.fabrichg.mixins.entity;

import de.royzer.fabrichg.game.GamePhaseManager;
import de.royzer.fabrichg.game.phase.PhaseType;
import net.minecraft.entity.player.HungerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HungerManager.class)
public class HungerManagerMixin {
    @Inject(
            method = "setFoodLevel",
            at = @At("HEAD"),
            cancellable = true
    )
    public void setHunger(int foodLevel, CallbackInfo ci) {
        if (GamePhaseManager.INSTANCE.getCurrentPhaseType().equals(PhaseType.LOBBY)) {
            ci.cancel();
        }
    }
}
