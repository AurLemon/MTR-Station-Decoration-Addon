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

public class YuuniPIDSBlock extends BlockPIDSHorizontalBase {

	private final int maxArrivals;
	private final Supplier<BlockEntityType<YuuniPIDSBlockEntity>> blockEntityType;

	public YuuniPIDSBlock(BlockBehaviour.Properties properties, int maxArrivals, Supplier<BlockEntityType<YuuniPIDSBlockEntity>> blockEntityType) {
		super(properties, maxArrivals);
		this.maxArrivals = maxArrivals;
		this.blockEntityType = blockEntityType;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		final VoxelShape shape1;
		final VoxelShape shape2;
		if (maxArrivals == 1) {
			shape1 = IBlock.getVoxelShapeByDirection(5.75, 4.95, 0, 10.25, 9.6, 13.7, state.getValue(BlockStateProperties.HORIZONTAL_FACING));
			shape2 = IBlock.getVoxelShapeByDirection(7.75, 9.6, 8.5, 8.25, 13, 9, state.getValue(BlockStateProperties.HORIZONTAL_FACING));
		} else {
			shape1 = IBlock.getVoxelShapeByDirection(5.75, 0.3, 0, 10.25, 11.6, 15.7, state.getValue(BlockStateProperties.HORIZONTAL_FACING));
			shape2 = IBlock.getVoxelShapeByDirection(7.75, 11.6, 10.5, 8.25, 16, 11, state.getValue(BlockStateProperties.HORIZONTAL_FACING));
		}
		return Shapes.or(shape1, shape2);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new YuuniPIDSBlockEntity(maxArrivals, blockEntityType.get(), pos, state);
	}

	public static class YuuniPIDSBlockEntity extends BlockEntityHorizontalBase {

		public YuuniPIDSBlockEntity(int maxArrivals, BlockEntityType<?> type, BlockPos pos, BlockState state) {
			super(maxArrivals, type, pos, state);
		}

		@Override
		public boolean showArrivalNumber() {
			return true;
		}

		@Override
		public int textColorArrived() {
			return 65280;
		}
	}
}
