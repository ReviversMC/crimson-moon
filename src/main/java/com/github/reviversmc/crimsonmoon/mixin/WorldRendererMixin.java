package com.github.reviversmc.crimsonmoon.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;

import com.github.reviversmc.crimsonmoon.CrimsonMoon;
import com.mojang.blaze3d.systems.RenderSystem;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Environment(EnvType.CLIENT)
@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Unique
    private static final Identifier CRIMSON_MOON_PHASES = CrimsonMoon.id("textures/environment/crimson_moon_phases.png");

    @Unique
    private static final Identifier CRIMSON_RAIN = CrimsonMoon.id("textures/environment/crimson_rain.png");

    @Shadow @Final private static Identifier MOON_PHASES;

    @Shadow private ClientWorld world;

    @Shadow @Final private static Identifier RAIN;

    @Redirect(
            method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/math/Matrix4f;FLjava/lang/Runnable;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/util/Identifier;)V",
                    ordinal = 1
            )
    )
    private void crimsonmoon_redirectMoonTexture(int bindPosition, Identifier id) {
        if(CrimsonMoon.CRIMSON_MOON_COMPONENT.get(world).isCrimsonMoon() && CrimsonMoon.CONFIG.customMoonTexture) {
            RenderSystem.setShaderTexture(bindPosition, CRIMSON_MOON_PHASES);
        } else {
            RenderSystem.setShaderTexture(bindPosition, MOON_PHASES);
        }
    }

    @Redirect(
            method = "renderWeather",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/util/Identifier;)V",
                    ordinal = 0
            )
    )
    private void crimsonmoon_redirectRainTexture(int bindPosition, Identifier id) {
        if(CrimsonMoon.CRIMSON_MOON_COMPONENT.get(world).isCrimsonMoon() && CrimsonMoon.CONFIG.customRainTexture) {
            RenderSystem.setShaderTexture(bindPosition, CRIMSON_RAIN);
        } else {
            RenderSystem.setShaderTexture(bindPosition, RAIN);
        }
    }
}
