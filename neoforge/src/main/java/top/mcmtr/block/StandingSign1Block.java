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

public class StandingSign1Block extends AbstractStandingSignBlock {

	public StandingSign1Block(BlockBehaviour.Properties properties, Supplier<BlockEntityType<StandingSignBlockEntity>> blockEntityType) {
		super(properties, 1, blockEntityType);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return IBlock.getVoxelShapeByDirection(7.6, 4.5, -1, 8.4, 10.5, 17, state.getValue(FACING));
	}
}
