package top.mcmtr.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.mtr.block.IBlock;

public class SurveillanceCamerasBlock extends AbstractChangeModelBlock {

	public static final IntegerProperty TYPE = IntegerProperty.create("type", 0, 1);

	public SurveillanceCamerasBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return IBlock.getVoxelShapeByDirection(6.5, 6.5, 0, 9.5, 16, 13, state.getValue(FACING));
	}

	@Override
	protected Property<Integer> getTypeProperty() {
		return TYPE;
	}
}
