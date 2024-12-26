package de.royzer.fabrichg.mixins.server.network;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(
            method = "getServerModName",
            at = @At("RETURN"),
            remap = false,
            cancellable = true
    )
    public void serverBrandName(CallbackInfoReturnable<String> cir) {
        cir.setReturnValue("brain");
    }

    @Inject(
            method = "getMaxPlayers",
            at = @At("HEAD"),
            cancellable = true
    )
    public void getMaxPlayers(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(187);
    }


}
