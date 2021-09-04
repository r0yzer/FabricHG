package de.royzer.fabrichg.mixins;

import net.minecraft.server.Main;
import org.apache.commons.io.FileUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.IOException;

@Mixin(Main.class)
public class MainMixin {
    @Inject(
            method = "main",
            at = @At("HEAD")
    )
    private static void onStart(String[] args, CallbackInfo ci) throws IOException {
        FileUtils.deleteDirectory(new File("world"));
    }
}
