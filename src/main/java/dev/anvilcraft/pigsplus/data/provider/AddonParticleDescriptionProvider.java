package dev.anvilcraft.pigsplus.data.provider;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.init.AddonParticleTypes;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.data.ParticleDescriptionProvider;

public class AddonParticleDescriptionProvider extends ParticleDescriptionProvider {
    public AddonParticleDescriptionProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void addDescriptions() {
        spriteSet(AddonParticleTypes.EXP.get(), AnvilCraftPigsPlus.of("exp"));
    }
}
