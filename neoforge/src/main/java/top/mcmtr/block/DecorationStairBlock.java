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

public class DecorationStairBlock extends AbstractChangeModelBlock {

	public static final IntegerProperty TYPE = IntegerProperty.create("type", 0, 1);

	public DecorationStairBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		if (state.getValue(getTypeProperty()) == 0) {
			final VoxelShape shape1 = IBlock.getVoxelShapeByDirection(0, 0, 0, 16, 4, 16, state.getValue(FACING));
			final VoxelShape shape2 = IBlock.getVoxelShapeByDirection(0, 4, 0, 16, 8, 12, state.getValue(FACING));
			final VoxelShape shape3 = IBlock.getVoxelShapeByDirection(0, 8, 0, 16, 12, 8, state.getValue(FACING));
			final VoxelShape shape4 = IBlock.getVoxelShapeByDirection(0, 12, 0, 16, 16, 4, state.getValue(FACING));
			return Shapes.or(shape1, shape2, shape3, shape4);
		} else {
			final VoxelShape shape1 = IBlock.getVoxelShapeByDirection(0, 8, 0, 16, 16, 16, state.getValue(FACING));
			final VoxelShape shape2 = IBlock.getVoxelShapeByDirection(0, 0, 0, 16, 8, 8, state.getValue(FACING));
			return Shapes.or(shape1, shape2);
		}
	}

	@Override
	protected Property<Integer> getTypeProperty() {
		return TYPE;
	}
}
