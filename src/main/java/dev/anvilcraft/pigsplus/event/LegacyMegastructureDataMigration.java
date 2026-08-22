package dev.anvilcraft.pigsplus.event;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkDataEvent;

@EventBusSubscriber(modid = AnvilCraftPigsPlus.MOD_ID)
// TODO: 不再支持包含 pigsplusActiveMegastructure 的旧存档后删除此迁移监听器。
public class LegacyMegastructureDataMigration {
    private static final String LEGACY_ACTIVE_MEGASTRUCTURE = "pigsplusActiveMegastructure";
    private static final String OFFICIAL_LEGACY_ACTIVE_MEGASTRUCTURE = "activeMegastructureName";

    @SubscribeEvent
    public static void onChunkLoad(ChunkDataEvent.Load event) {
        ListTag blockEntities = event.getData().getList("block_entities", Tag.TAG_COMPOUND);
        for (Tag entry : blockEntities) {
            if (!(entry instanceof CompoundTag blockEntityTag)
                || blockEntityTag.contains("activeMegastructureId")
                || blockEntityTag.contains(OFFICIAL_LEGACY_ACTIVE_MEGASTRUCTURE)) {
                continue;
            }

            String legacyName = blockEntityTag.getString(LEGACY_ACTIVE_MEGASTRUCTURE);
            if (!legacyName.isEmpty()) {
                blockEntityTag.putString(OFFICIAL_LEGACY_ACTIVE_MEGASTRUCTURE, legacyName);
            }
            blockEntityTag.remove(LEGACY_ACTIVE_MEGASTRUCTURE);
        }
    }
}
