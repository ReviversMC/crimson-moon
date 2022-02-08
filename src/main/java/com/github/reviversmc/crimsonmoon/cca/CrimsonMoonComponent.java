package com.github.reviversmc.crimsonmoon.cca;

import com.github.reviversmc.crimsonmoon.CrimsonMoon;
import com.github.reviversmc.crimsonmoon.CrimsonMoonClient;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.World;

public class CrimsonMoonComponent implements AutoSyncedComponent {

    private static final String CRIMSON_MOON_KEY = "CrimsonMoon";
    private final World world;
    private boolean isCrimsonMoon = false;

    public CrimsonMoonComponent(World world) {
        this.world = world;
    }

    public boolean isCrimsonMoon() {
        return isCrimsonMoon;
    }

    public void setCrimsonMoon(boolean toggle) {
        this.isCrimsonMoon = toggle;
        CrimsonMoon.CRIMSON_MOON_COMPONENT.sync(world);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.isCrimsonMoon = tag.getBoolean(CRIMSON_MOON_KEY);
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putBoolean(CRIMSON_MOON_KEY, isCrimsonMoon);
    }

    @Override
    public void applySyncPacket(PacketByteBuf buf) {
        NbtCompound tag = buf.readNbt();

        if (tag != null) {
            readFromNbt(tag);

            if(isCrimsonMoon) {
                CrimsonMoonClient.triggerBanner();
            }
        }
    }
}
