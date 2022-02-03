package draylar.crimsonmoon;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import draylar.crimsonmoon.cca.CrimsonMoonComponent;
import draylar.crimsonmoon.cca.ProgressComponent;
import draylar.crimsonmoon.config.CrimsonMoonConfig;
import draylar.crimsonmoon.registry.CrimsonEventHandlers;
import draylar.omegaconfig.OmegaConfig;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.biome.SpawnSettings.SpawnEntry;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import java.util.Random;

// General Notes:
//    world.getTimeOfDay() is a GENERAL total-tick counter. If you add 100,000 to the time, this method will return a large number.
//    world.getTime() is world.getGameTime() in mojmap. It doesn't seem to be related to day time or the time command. guessing it's like a world age counter
public class CrimsonMoon implements ModInitializer, WorldComponentInitializer {

    public static final ComponentKey<CrimsonMoonComponent> CRIMSON_MOON_COMPONENT = ComponentRegistryV3.INSTANCE.getOrCreate(id("crimsonmoon"), CrimsonMoonComponent.class);
    public static final ComponentKey<ProgressComponent> PROGRESS = ComponentRegistryV3.INSTANCE.getOrCreate(id("progress"), ProgressComponent.class);
    public static final CrimsonMoonConfig CONFIG = OmegaConfig.register(CrimsonMoonConfig.class);

    @Override
    public void onInitialize() {
        CrimsonEventHandlers.register();
    }

    public static SpawnSettings.SpawnEntry pickRandomSpawnEntry(ChunkGenerator chunkGenerator, SpawnGroup spawnGroup, Random random, BlockPos pos, StructureAccessor accessor, Biome biome) {
        while (true) {
            SpawnEntry mob = chunkGenerator.getEntitySpawnList(biome, accessor, spawnGroup, pos).getOrEmpty(random).get();
            if (!mob.type.equals(EntityType.SLIME)) {
                return mob;
            }
        }
    }

    public static Identifier id(String name) {
        return new Identifier("crimsonmoon", name);
    }

    public static long getTrueDayTime(World world) {
        return world.getTimeOfDay() % 24000;
    }

    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
        registry.register(CRIMSON_MOON_COMPONENT, CrimsonMoonComponent::new);
        registry.register(PROGRESS, world -> new ProgressComponent());
    }
}
