package draylar.crimsonmoon;

import com.mojang.blaze3d.systems.RenderSystem;

import ladysnake.satin.api.event.ShaderEffectRenderCallback;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ShaderEffectManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class CrimsonMoonClient implements ClientModInitializer {

    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final int END_TIME = 23031;
    private static final int FADE_START = END_TIME - 200;
    public static final Identifier BANNER_TEXTURE = CrimsonMoon.id("textures/gui/banner.png");
    public static final ManagedShaderEffect CRIMSON_SHADER = ShaderEffectManager.getInstance().manage(new Identifier("crimsonmoon", "shaders/post/crimson.json"));
    public static boolean banner = false;
    public static int bannerTicks = 0;

    @Override
    public void onInitializeClient() {
        ShaderEffectRenderCallback.EVENT.register((v) -> {
            if (mc.world != null) {
                if (CrimsonMoon.CRIMSON_MOON_COMPONENT.get(mc.world).isCrimsonMoon()) {
                    double glowIntensity = Math.max(0.0, Math.min(1.0, CrimsonMoon.CONFIG.glowIntensity)); // [0, 1]
                    double remaining = 1 - glowIntensity;
                    long timeOfDay = CrimsonMoon.getTrueDayTime(mc.world);

                    if (banner) {
                        glowIntensity = glowIntensity + remaining * (1 - (bannerTicks / 200f));
                    } else if (timeOfDay >= FADE_START && timeOfDay <= END_TIME) {
                        glowIntensity = glowIntensity + remaining * ((timeOfDay - FADE_START) / 200f);
                    }

                    CRIMSON_SHADER.setUniformValue("ColorModulate", 1.0f, (float) glowIntensity, (float) glowIntensity, 1.0f);
                    CRIMSON_SHADER.render(v);
                }
            }
        });

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            // Tick dropdown banner
            if (banner) {
                bannerTicks++;

                if (bannerTicks >= 200) {
                    bannerTicks = 0;
                    banner = false;
                }
            }
        });

        HudRenderCallback.EVENT.register((matrices, delta) -> {
            if (banner) {
                matrices.push();
                float lerpedTicks = MathHelper.lerp(delta, bannerTicks - 1, bannerTicks);

                matrices.translate(0, -100, 0);

                if (bannerTicks < 20) {
                    matrices.translate(0, 100 * (-Math.cos(Math.PI * (lerpedTicks / 20f)) + 1) / 2, 0); // 0 -> 1
                } else if (bannerTicks > 180) {
                    matrices.translate(0, 100 - 100 * ((lerpedTicks - 180) / 20f), 0); // 1 -> 0
                } else {
                    matrices.translate(0, 100, 0);
                }

                float width = mc.getWindow().getScaledWidth() / 2f;

                // TODO: Can the next line be deleted in 1.17+?
                mc.getTextureManager().bindTexture(BANNER_TEXTURE);
                RenderSystem.setShaderTexture(0, BANNER_TEXTURE);

                matrices.push();
                matrices.scale(2, 2, 2);
                matrices.translate(width / 2 - 78 / 2, 1, 0);
                DrawableHelper.drawTexture(matrices, 0, 0, 0, 0, 78, 20, 78, 20);
                matrices.pop();

                MutableText crimson_moon = new LiteralText("Crimson Moon").setStyle(Style.EMPTY.withBold(true));
                MutableText text = new LiteralText("The grounds tremble...").setStyle(Style.EMPTY.withBold(true));
                int titleWidth = mc.textRenderer.getWidth(crimson_moon);
                int hintWidth = mc.textRenderer.getWidth(text);
                mc.textRenderer.draw(matrices, crimson_moon, width - titleWidth / 2f, 10, 0x5F0713);
                mc.textRenderer.draw(matrices, text, width - hintWidth / 2f, 20, 0x75160c);

                matrices.pop();
            } else {
                bannerTicks = 0;
            }
        });
    }

    public static void triggerBanner() {
        mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_TOAST_IN, 0.0F, 5.0F));
        mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 0.0F, 1.0F));
        mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ENTITY_ZOMBIE_AMBIENT, 0.5F, 0.75F));
        banner = true;
        bannerTicks = 0;
    }
}
