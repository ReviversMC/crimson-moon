package draylar.crimsonmoon.cca;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import net.minecraft.nbt.NbtCompound;

public class ProgressComponent implements ComponentV3 {

    private static final String VISITED_NETHER_KEY = "VisitedNether";
    private boolean hasVisitedNether = false;

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.hasVisitedNether = tag.getBoolean(VISITED_NETHER_KEY);
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putBoolean(VISITED_NETHER_KEY, hasVisitedNether);
    }

    public boolean hasVisitedNether() {
        return hasVisitedNether;
    }

    public void setHasVisitedNether(boolean hasVisitedNether) {
        this.hasVisitedNether = hasVisitedNether;
    }
}
