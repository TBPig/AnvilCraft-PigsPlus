package dev.anvilcraft.pigsplus.block;

import dev.anvilcraft.pigsplus.init.AddonInteractionMaps;
import dev.dubhe.anvilcraft.block.Layered4LevelCauldronBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class VoidAcidCauldronBlock extends Layered4LevelCauldronBlock {
    public VoidAcidCauldronBlock(Properties properties) {
        super(properties, AddonInteractionMaps.VOID_ACID);
    }

    @Override
    public ItemInteractionResult useItemOn(
        ItemStack stack,
        BlockState state,
        Level level,
        BlockPos pos,
        Player player,
        InteractionHand hand,
        BlockHitResult hitResult
    ) {
        CauldronInteraction interaction = this.interactions.map().get(stack.getItem());
        if (interaction == null) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        return interaction.interact(state, level, pos, player, hand, stack);
    }
}
