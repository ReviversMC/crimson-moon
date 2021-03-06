package com.github.reviversmc.crimsonmoon.mixin;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Invoker(value = "getEyeHeight")
    float crimsonmoon_getEyeHeight(EntityPose pose, EntityDimensions dimensions);
}
