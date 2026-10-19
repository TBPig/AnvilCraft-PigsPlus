package dev.anvilcraft.pigsplus.init;

import dev.anvilcraft.pigsplus.block.entity.BeamHeatConverterBlockEntity;
import dev.dubhe.anvilcraft.api.heat.HeatRecorder;
import dev.dubhe.anvilcraft.api.heat.HeatTier;
import dev.dubhe.anvilcraft.api.heat.HeatTierLine;
import dev.dubhe.anvilcraft.api.heat.HeaterInfo;

public class AddonHeaterInfos {
    public static final HeaterInfo<BeamHeatConverterBlockEntity> BEAM_HEAT_CONVERTER = HeatRecorder.registerProducerInfo(
        HeaterInfo.blockEntity(
            AddonBlockEntities.BEAM_HEAT_CONVERTER,
            BeamHeatConverterBlockEntity::getHeatingPoses,
            HeatTierLine.always(HeatTier.INCANDESCENT, 60)
        )
    );

    public static void register() {
    }
}
