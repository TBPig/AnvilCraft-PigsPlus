package dev.anvilcraft.pigsplus.block.entity.megastructure;

import dev.dubhe.anvilcraft.block.entity.CelestialForgingAnvilBlockEntity;

public class StarReformerHandler extends ReformerHandler {
    public static final String NAME = "star_reformer";

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void serverTick(CelestialForgingAnvilBlockEntity be) {
        if (!be.isAmplifierPresent()) return;
        super.serverTick(be);
    }
}
