package dev.anvilcraft.pigsplus.data;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import top.theillusivec4.curios.api.CuriosDataProvider;

import java.util.concurrent.CompletableFuture;

public class ModCuriosProvider extends CuriosDataProvider {
    public ModCuriosProvider(
        PackOutput output,
        ExistingFileHelper fileHelper,
        CompletableFuture<HolderLookup.Provider> registries
    ) {
        super(AnvilCraftPigsPlus.MOD_ID, output, fileHelper, registries);
    }

    @Override
    public void generate(HolderLookup.Provider registries, ExistingFileHelper fileHelper) {
        this.createEntities("charm")
            .addPlayer()
            .addSlots("charm");
    }
}
