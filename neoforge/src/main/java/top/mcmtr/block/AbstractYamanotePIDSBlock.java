package top.mcmtr.block;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.mtr.block.BlockPIDSHorizontalBase;
import org.mtr.block.IBlock;

public abstract class AbstractYamanotePIDSBlock extends BlockPIDSHorizontalBase {

	private static final int MAX_ARRIVALS = 3;
	private final double shapeEnd;
	private final double capStart;
	private final double capEnd;
	private final Supplier<BlockEntityType<YamanotePIDSBlockEntity>> blockEntityType;

	protected AbstractYamanotePIDSBlock(
			BlockBehaviour.Properties properties,
			double shapeEnd,
			double capStart,
			double capEnd,
			Supplier<BlockEntityType<YamanotePIDSBlockEntity>> blockEntityType) {
		super(properties, MAX_ARRIVALS);
		this.shapeEnd = shapeEnd;
		this.capStart = capStart;
		this.capEnd = capEnd;
		this.blockEntityType = blockEntityType;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		final VoxelShape shape1 = IBlock.getVoxelShapeByDirection(7, 8, 0, 9, 16, shapeEnd, state.getValue(BlockStateProperties.HORIZONTAL_FACING));
		final VoxelShape shape2 = IBlock.getVoxelShapeByDirection(7.5, 8, capStart, 8.5, 16, capEnd, state.getValue(BlockStateProperties.HORIZONTAL_FACING));
		return Shapes.or(shape1, shape2);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new YamanotePIDSBlockEntity(blockEntityType.get(), pos, state);
	}

	public static class YamanotePIDSBlockEntity extends BlockEntityHorizontalBase {

		public YamanotePIDSBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
			super(MAX_ARRIVALS, type, pos, state);
		}

		@Override
		public boolean showArrivalNumber() {
			return true;
		}
	}
}
