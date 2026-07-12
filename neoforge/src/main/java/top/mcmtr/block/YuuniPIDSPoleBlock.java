package top.mcmtr.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.mtr.block.BlockPIDSHorizontalBase;
import org.mtr.block.BlockPoleCheckBase;
import org.mtr.block.IBlock;

public class YuuniPIDSPoleBlock extends BlockPoleCheckBase {

	public YuuniPIDSPoleBlock(BlockBehaviour.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
		builder.add(BlockStateProperties.HORIZONTAL_FACING);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return IBlock.getVoxelShapeByDirection(7.75, 0, 10.5, 8.25, 16, 11, state.getValue(BlockStateProperties.HORIZONTAL_FACING));
	}

	@Override
	protected boolean isBlock(BlockBehaviour block) {
		return block instanceof BlockPIDSHorizontalBase || block instanceof YuuniPIDSPoleBlock;
	}

	@Override
	protected Component getTooltipBlockText() {
		return Component.translatable("block.msd.yamanote_pids");
	}
}
