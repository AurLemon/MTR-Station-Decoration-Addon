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

public class Yamanote4PIDSBlock extends BlockPIDSHorizontalBase {

	private static final int MAX_ARRIVALS = 3;
	private final Supplier<BlockEntityType<Yamanote4PIDSBlockEntity>> blockEntityType;

	public Yamanote4PIDSBlock(BlockBehaviour.Properties properties, Supplier<BlockEntityType<Yamanote4PIDSBlockEntity>> blockEntityType) {
		super(properties, MAX_ARRIVALS);
		this.blockEntityType = blockEntityType;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		final VoxelShape shape1 = IBlock.getVoxelShapeByDirection(7, 8, 0, 9, 16, 17, state.getValue(BlockStateProperties.HORIZONTAL_FACING));
		final VoxelShape shape2 = IBlock.getVoxelShapeByDirection(7.5, 8, 17, 8.5, 16, 18, state.getValue(BlockStateProperties.HORIZONTAL_FACING));
		return Shapes.or(shape1, shape2);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new Yamanote4PIDSBlockEntity(blockEntityType.get(), pos, state);
	}

	public static class Yamanote4PIDSBlockEntity extends BlockEntityHorizontalBase {

		public Yamanote4PIDSBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
			super(MAX_ARRIVALS, type, pos, state);
		}

		@Override
		public boolean showArrivalNumber() {
			return true;
		}
	}
}
