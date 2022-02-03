package draylar.crimsonmoon.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import draylar.crimsonmoon.api.Crimson;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

@Mixin(MobEntity.class)
public abstract class CrimsonMobEntityMixin extends LivingEntity implements Crimson {

    private boolean crimsonmoon_isCrimson = false;

    protected CrimsonMobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(
            method = "writeCustomDataToNbt",
            at = @At("RETURN"))
    private void onWriteData(NbtCompound tag, CallbackInfo ci) {
        tag.putBoolean("IsCrimson", crimsonmoon_isCrimson);
    }

    @Inject(
            method = "readCustomDataFromNbt",
            at = @At("RETURN"))
    private void onReadData(NbtCompound tag, CallbackInfo ci) {
        crimsonmoon_isCrimson = tag.getBoolean("IsCrimson");
    }

    @Override
    public boolean crimsonmoon_isCrimson() {
        return crimsonmoon_isCrimson;
    }

    @Override
    public void crimsonmoon_setCrimson(boolean crimson) {
        crimsonmoon_isCrimson = crimson;
    }

}
