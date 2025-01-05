package de.royzer.fabrichg.mixins.commans;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Optional;
import java.util.stream.Stream;

@Mixin(Commands.class)
public class CommandsMixin {
    @Unique
    private CommandDispatcher<CommandSourceStack> createFakeDispatcher() {
        return new CommandDispatcher<>();
    }

    @Unique
    private CommandBuildContext createFakeBuildContext() {
        return new CommandBuildContext() {
            @Override
            public @NotNull Stream<ResourceKey<? extends Registry<?>>> listRegistries() {
                return Stream.empty();
            }

            @Override
            public <T> @NotNull Optional<HolderLookup.RegistryLookup<T>> lookup(ResourceKey<? extends Registry<? extends T>> registryKey) {
                return Optional.empty();
            }
        };
    }

    @ModifyArgs(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/commands/TeamCommand;register(Lcom/mojang/brigadier/CommandDispatcher;Lnet/minecraft/commands/CommandBuildContext;)V"))
    public void disableTeamCommand(Args args) {
        args.set(0, createFakeDispatcher());
        args.set(1, createFakeBuildContext());
    }

}
