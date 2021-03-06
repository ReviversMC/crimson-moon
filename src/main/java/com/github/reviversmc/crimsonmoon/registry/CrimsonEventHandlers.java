package com.github.reviversmc.crimsonmoon.registry;

import com.github.reviversmc.crimsonmoon.CrimsonMoon;
import com.github.reviversmc.crimsonmoon.api.Crimson;
import com.github.reviversmc.crimsonmoon.api.CrimsonMobHelper;
import com.github.reviversmc.crimsonmoon.api.CrimsonMoonEvents;
import com.github.reviversmc.crimsonmoon.util.WorldUtils;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.SpawnSettings;

public class CrimsonEventHandlers {

    public static final int KILL_TIME_START = 23031;
    public static final int KILL_TIME_END = 23031 + 100;

    public static void register() {
        // Crimson Moons can only spawn if the config chance passes.
        CrimsonMoonEvents.BEFORE_START.register(world -> {
            if (world.random.nextInt(CrimsonMoon.CONFIG.averageNightsBetweenCrimsonMoons) == 0) {
                return ActionResult.PASS;
            }

            return ActionResult.FAIL;
        });

        registerTickHandler();
    }

    private static void registerTickHandler() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            server.getWorlds().forEach(world -> {

                // Only tick Crimson Moon logic in worlds where the event is active.
                // Additionally, there is a spawn delay to lower TPS impact.
                //    This defaults to 1 second in the config file.
                if (CrimsonMoon.CRIMSON_MOON_COMPONENT.get(world).isCrimsonMoon() && world.random.nextInt(CrimsonMoon.CONFIG.spawnDelaySeconds * 20) == 0) {

                    // Ensure mobs can spawn at this time.
                    // 13188 is the general time most mobs can spawn (12969 in rainy weather).
                    if (CrimsonMoon.getTrueDayTime(world) >= 13188) {

                        // Calculate a list of loaded chunks by looking at the position of every player
                        // in the world, and then spawn mobs in each chunk.
                        WorldUtils.getLoadedChunks(world).forEach(chunk -> {
                            ChunkPos pos = chunk.getPos();

                            // Before spawning mobs inside a chunk, ensure it has room for more mobs.
                            if (world.getEntitiesByClass(HostileEntity.class, new Box(pos.getStartPos(), pos.getStartPos().add(16, 256, 16)), e -> true).size() < CrimsonMoon.CONFIG.maxMobCountPerChunk) {
                                // Calculate a position to spawn our new mob at.
                                // Mobs always spawn on the surface at this point.
                                // TODO: do we want mobs to spawn underground?
                                int randomX = world.random.nextInt(16);
                                int randomZ = world.random.nextInt(16);
                                ChunkPos chunkPos = chunk.getPos();
                                int y = world.getTopY(Heightmap.Type.MOTION_BLOCKING, chunkPos.getStartX() + randomX, chunkPos.getStartZ() + randomZ);
                                BlockPos spawnPos = new BlockPos(chunkPos.getStartX() + randomX, y, chunkPos.getStartZ() + randomZ);

                                // Pick a random monster to spawn based on the spawn conditions of the area.
                                SpawnSettings.SpawnEntry spawnEntry = CrimsonMoon.pickRandomSpawnEntry(
                                        world.getChunkManager().getChunkGenerator(),
                                        SpawnGroup.MONSTER,
                                        world.getRandom(),
                                        spawnPos,
                                        world.getStructureAccessor(),
                                        world.getBiome(spawnPos)
                                );

                                if(spawnEntry != null) {
                                    Entity entity = spawnEntry.type.create(world);

                                    if(entity != null) {
                                        // Ensure this entity can spawn at the given position
                                        // TODO: light levels?
                                        if(entity instanceof HostileEntity) {
                                            if(!HostileEntity.canSpawnInDark((EntityType<? extends HostileEntity>) spawnEntry.type, world, SpawnReason.EVENT, spawnPos, world.random)) {
                                                return;
                                            }
                                        }

                                        if(entity instanceof MobEntity) {
                                            ((Crimson) entity).crimsonmoon_setCrimson(true);
                                            ((MobEntity) entity).initialize(world, world.getLocalDifficulty(spawnPos), SpawnReason.EVENT, null, null);
                                            CrimsonMobHelper.initialize(world.getServer(), (MobEntity) entity);

                                            // --- The following code is left over from the 1.16 version.
                                            //     It doesn't seem to be necessary now?                  ---
                                            // // refresh target goal distance
                                            // // because it is calculated during init and this is too late
                                            // GoalSelector targetSelector = ((MobEntityAccessor) entity).getTargetSelector();
                                            // Set<PrioritizedGoal> goals = targetSelector.getGoals();
                                            // for (PrioritizedGoal goal : goals) {
                                            //     if(goal.getGoal() instanceof ActiveTargetGoal) {
                                            //         // haha yes
                                            //         FollowTargetGoalAccessor followTargetGoal = (FollowTargetGoalAccessor) ((ActiveTargetGoal) goal.getGoal());
                                            //         TargetPredicate targetPredicate = followTargetGoal.getTargetPredicate();
                                            //         Predicate<LivingEntity> predicate = ((TargetPredicateAccessor) targetPredicate).getPredicate();
                                            //         followTargetGoal.setTargetPredicate(new TargetPredicate().setBaseMaxDistance(((MobEntity) entity).getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE)).setPredicate(predicate));
                                            //     }
                                            // }
                                        }

                                        entity.setPos(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
                                        entity.updateTrackedPosition(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
                                        entity.updatePosition(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
                                        world.spawnEntity(entity);
                                    }
                                }
                            }
                        });
                    }
                } else if (CrimsonMoon.getTrueDayTime(world) >= KILL_TIME_START && CrimsonMoon.getTrueDayTime(world) <= KILL_TIME_END) {
                    world.iterateEntities().forEach(entity -> {
                        if (entity instanceof MobEntity
                                && ((Crimson) entity).crimsonmoon_isCrimson())
                        entity.kill();
                    });
                }
            });
        });
    }

    private CrimsonEventHandlers() {
        // NO-OP
    }
}
