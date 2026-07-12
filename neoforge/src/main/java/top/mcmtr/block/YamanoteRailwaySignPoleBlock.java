package top.mcmtr.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.mtr.block.BlockPoleCheckBase;
import org.mtr.block.IBlock;

public class YamanoteRailwaySignPoleBlock extends BlockPoleCheckBase {

	public static final IntegerProperty TYPE = IntegerProperty.create("type", 0, 3);

	public YamanoteRailwaySignPoleBlock(BlockBehaviour.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH).setValue(TYPE, 0));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
		builder.add(BlockStateProperties.HORIZONTAL_FACING, TYPE);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return switch (state.getValue(TYPE)) {
			case 0 -> IBlock.getVoxelShapeByDirection(14, 0, 7.5, 15, 16, 8.5, state.getValue(BlockStateProperties.HORIZONTAL_FACING));
			case 1 -> IBlock.getVoxelShapeByDirection(10, 0, 7.5, 11, 16, 8.5, state.getValue(BlockStateProperties.HORIZONTAL_FACING));
			case 2 -> IBlock.getVoxelShapeByDirection(6, 0, 7.5, 7, 16, 8.5, state.getValue(BlockStateProperties.HORIZONTAL_FACING));
			case 3 -> IBlock.getVoxelShapeByDirection(2, 0, 7.5, 3, 16, 8.5, state.getValue(BlockStateProperties.HORIZONTAL_FACING));
			default -> super.getShape(state, level, pos, context);
		};
	}

	@Override
	protected boolean isBlock(BlockBehaviour block) {
		return block instanceof YamanoteRailwaySignPoleBlock;
	}

	@Override
	protected Component getTooltipBlockText() {
		return Component.translatable("block.msd.yamanote_railway_sign");
	}
}
