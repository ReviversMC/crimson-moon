package draylar.crimsonmoon.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.entity.mob.MobEntity;

@Mixin(MobEntity.class)
public class MobEntityMixin {

//    @Redirect(
//            method = "initEquipment",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/LocalDifficulty;getClampedLocalDifficulty()F")
//    )
//    private float crimsonmoon_redirect(LocalDifficulty localDifficulty) {
//        return 100;
//    }
}
