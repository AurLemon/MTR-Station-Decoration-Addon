package top.mcmtr.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.mtr.block.IBlock;

public class HallSeatBlock extends AbstractChangeModelBlock {

	public static final IntegerProperty TYPE = IntegerProperty.create("type", 0, 2);

	public HallSeatBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		final VoxelShape shape1 = IBlock.getVoxelShapeByDirection(0, 6, 0, 16, 8, 16, state.getValue(FACING));
		final VoxelShape shape2 = IBlock.getVoxelShapeByDirection(0, 8, 0, 16, 16, 5, state.getValue(FACING));
		return Shapes.or(shape1, shape2);
	}

	@Override
	protected Property<Integer> getTypeProperty() {
		return TYPE;
	}
}
