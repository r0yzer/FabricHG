package de.royzer.fabrichg.mixins;

import de.royzer.fabrichg.MainKt;
import net.minecraft.server.Main;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

@Mixin(Main.class)
public class MainMixin {
    @Shadow @Final private static Logger LOGGER;

    @Inject(
            method = "main",
            at = @At("HEAD"),
            remap = false
    )
    private static void onStart(String[] args, CallbackInfo ci) throws IOException {
        boolean ffa = Arrays.stream(args).toList().contains("ffa");

//        if (System.getProperty("os.name").contains("Windows") || Arrays.stream(args).toList().contains("saveworld")) {
//            return;
//        }
        MainKt.setFFA(ffa);

        if (ffa) {
            File ffaFolder = new File("./ffa-world");
            File worldFolder = new File("./world");

            if (!ffaFolder.exists()) {
                LOGGER.warn("NO FFA WORLD");
                return;
            }

            FileUtils.delete(worldFolder);
            FileUtils.copyDirectory(ffaFolder, worldFolder);
        } else {
            deleteWorldData();
        }
    }

    @Unique
    private static void deleteWorldData() {
        Arrays.stream(Objects.requireNonNull(new File("./world").list())).filter(s -> !s.equalsIgnoreCase("datapacks")).forEach(s -> {
            File file = new File("./world/" + s);
            if (file.isDirectory()) {
                try {
                    FileUtils.deleteDirectory(file);
                } catch (IOException _) {
                }
            } else {
                file.delete();
            }
        });
    }
}
