package top.mcmtr.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.mtr.block.IBlock;

public class OldElectricPoleBlock extends Block {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty IS_LONG = BooleanProperty.create("is_long");

	private final boolean hasAnother;

	public OldElectricPoleBlock(BlockBehaviour.Properties properties, boolean hasAnother) {
		super(properties);
		this.hasAnother = hasAnother;
		registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(IS_LONG, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, IS_LONG);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		final Direction facing = context.getHorizontalDirection();
		if (!IBlock.isReplaceable(context, facing, 2)) {
			return null;
		}
		return defaultBlockState().setValue(FACING, facing).setValue(IS_LONG, false);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, net.minecraft.world.entity.LivingEntity placer, net.minecraft.world.item.ItemStack stack) {
		if (!level.isClientSide && hasAnother) {
			final Direction direction = state.getValue(FACING);
			level.setBlock(pos.relative(direction), defaultBlockState().setValue(FACING, direction.getOpposite()).setValue(IS_LONG, true), 3);
			level.setBlock(pos.relative(direction.getOpposite()), defaultBlockState().setValue(FACING, direction).setValue(IS_LONG, true), 3);
		}
	}

	@Override
	protected BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	protected BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return Shapes.block();
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}
}
