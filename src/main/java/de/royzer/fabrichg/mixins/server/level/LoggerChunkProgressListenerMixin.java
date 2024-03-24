package de.royzer.fabrichg.mixins.server.level;

import de.royzer.fabrichg.game.GamePhaseManager;
import net.minecraft.Util;
import net.minecraft.server.level.progress.LoggerChunkProgressListener;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LoggerChunkProgressListener.class)
class LoggerChunkProgressListenerMixin {

    // chat.openai.com
    @Unique
    private static String convertToMinecraftColor(int r, int g, int b) {
        char colorCode = getClosestMinecraftColor(r, g, b);

        return "§" + colorCode;
    }

    @Unique
    private static char getClosestMinecraftColor(int r, int g, int b) {
        char[] minecraftColors = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        int[][] minecraftColorValues = {
                {0, 0, 0},   // Black
                {0, 0, 170}, // Dark Blue
                {0, 170, 0}, // Dark Green
                {0, 170, 170}, // Dark Aqua
                {170, 0, 0}, // Dark Red
                {170, 0, 170}, // Dark Purple
                {255, 170, 0}, // Gold
                {170, 170, 170}, // Gray
                {85, 85, 85}, // Dark Gray
                {85, 85, 255}, // Blue
                {85, 255, 85}, // Green
                {85, 255, 255}, // Aqua
                {255, 85, 85}, // Red
                {255, 85, 255}, // Light Purple
                {255, 255, 85}, // Yellow
                {255, 255, 255} // White
        };

        // Calculate the closest Minecraft color based on Euclidean distance
        int minDistance = Integer.MAX_VALUE;
        int closestColorIndex = 0;
        for (int i = 0; i < minecraftColorValues.length; i++) {
            int[] colorValue = minecraftColorValues[i];
            int distance = (r - colorValue[0]) * (r - colorValue[0]) +
                    (g - colorValue[1]) * (g - colorValue[1]) +
                    (b - colorValue[2]) * (b - colorValue[2]);
            if (distance < minDistance) {
                minDistance = distance;
                closestColorIndex = i;
            }
        }

        return minecraftColors[closestColorIndex];
    }

    @Shadow
    public int getProgress() {
        return 0;
    }

    @Shadow private long nextTickTime;


    @Unique
    public float lerp(float start, float end, float progress) {
        return start + progress * (end - start);
    }


    @Inject(
            method = "onStatusChange",
            at = @At("TAIL")
    )
    public void changeMotd(ChunkPos chunkPosition, ChunkStatus newStatus, CallbackInfo ci) {
        int progress = this.getProgress();
        if (progress > 100 || Util.getMillis() <= this.nextTickTime) return;
        int r = (int) lerp(0, 255, ((float) 100-progress) / 100);
        int g = (int) lerp(0, 255, ((float) progress) / 100);
        int b = 0;
        GamePhaseManager.INSTANCE.getServer()
                .setMotd(GamePhaseManager.MOTD_STRING + "\nLoading, " + convertToMinecraftColor(r, g, b) + progress + "%");
    }
}