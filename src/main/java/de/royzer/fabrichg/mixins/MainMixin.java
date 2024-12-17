package de.royzer.fabrichg.mixins;

import net.minecraft.server.Main;
import org.apache.commons.io.FileUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

@Mixin(Main.class)
public class MainMixin {
    @Inject(
            method = "main",
            at = @At("HEAD"),
            remap = false
    )
    private static void onStart(String[] args, CallbackInfo ci) throws IOException {
//        if (System.getProperty("os.name").contains("Windows") || Arrays.stream(args).toList().contains("saveworld")) {
//            return;
//        }

        var worldDir = new File("./world");
        if (!worldDir.exists()) return;
        Arrays.stream(worldDir.list()).filter(s -> !s.equalsIgnoreCase("datapacks")).filter(s -> !s.equalsIgnoreCase("dimensions")).forEach(s -> {
            File file = new File("./world/" + s);
            if (file.isDirectory()) {
                try {
                    FileUtils.deleteDirectory(file);
                } catch (IOException e) {
                }
            } else {
                file.delete();
            }
        });

    }
}
