package top.mcmtr.block;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;

public abstract class AbstractChangeModelBlock extends Block {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

	protected AbstractChangeModelBlock(BlockBehaviour.Properties properties) {
		super(properties);
		registerDefaultState(defaultState(stateDefinition.any()));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, getTypeProperty());
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultState(defaultBlockState()).setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	protected BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	protected BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	private BlockState defaultState(BlockState state) {
		return state.setValue(FACING, Direction.NORTH).setValue(getTypeProperty(), defaultTypeValue());
	}

	protected int defaultTypeValue() {
		return 0;
	}

	protected abstract Property<Integer> getTypeProperty();
}
