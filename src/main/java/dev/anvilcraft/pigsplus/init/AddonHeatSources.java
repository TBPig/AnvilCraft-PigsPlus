package dev.anvilcraft.pigsplus.init;

import dev.anvilcraft.pigsplus.block.BeamHeatConverterBlock;
import dev.anvilcraft.pigsplus.block.entity.BeamHeatConverterBlockEntity;
import dev.dubhe.anvilcraft.api.heat.collector.HeatCollectorManager;
import dev.dubhe.anvilcraft.api.heat.collector.HeatSourceEntry;

public class AddonHeatSources {
    public static void register() {
        HeatCollectorManager.registerEntry(
            HeatSourceEntry.predicateAlways(
                BeamHeatConverterBlockEntity.HEAT_OUTPUT,
                state -> state.is(AddonBlocks.BEAM_HEAT_CONVERTER.get())
                    && state.getValue(BeamHeatConverterBlock.WORKING)
            )
        );
    }
}
