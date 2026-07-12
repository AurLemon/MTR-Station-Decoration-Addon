package top.mcmtr.block;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.mtr.block.IBlock;

public class StandingSignBlock extends AbstractStandingSignBlock {

	public StandingSignBlock(BlockBehaviour.Properties properties, Supplier<BlockEntityType<StandingSignBlockEntity>> blockEntityType) {
		super(properties, 3, blockEntityType);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return IBlock.getVoxelShapeByDirection(6.9, 1, 0, 9.1, 16, 11, state.getValue(FACING));
	}
}
