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

    private boolean cm_isCrimson = false;

    protected CrimsonMobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(
            method = "writeCustomDataToTag",
            at = @At("RETURN"))
    private void onWriteData(NbtCompound tag, CallbackInfo ci) {
        tag.putBoolean("IsCrimson", cm_isCrimson);
    }

    @Inject(
            method = "readCustomDataFromTag",
            at = @At("RETURN"))
    private void onReadData(NbtCompound tag, CallbackInfo ci) {
        cm_isCrimson = tag.getBoolean("IsCrimson");
    }

    @Override
    public boolean cm_isCrimson() {
        return cm_isCrimson;
    }

    @Override
    public void cm_setCrimson(boolean crimson) {
        cm_isCrimson = crimson;
    }

}
