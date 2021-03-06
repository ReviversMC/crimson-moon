package com.github.reviversmc.crimsonmoon.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import com.github.reviversmc.crimsonmoon.CrimsonMoon;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    @Inject(
            method = "moveToWorld",
            at = @At("HEAD"))
    private void crimsonmoon_onChangeWorld(ServerWorld destination, CallbackInfoReturnable<Entity> cir) {
        // Player is traveling to the Nether
        if(destination.getRegistryKey().equals(World.NETHER)) {
            CrimsonMoon.PROGRESS.get(destination.getServer().getWorld(World.OVERWORLD)).setHasVisitedNether(true);
        }
    }
}
