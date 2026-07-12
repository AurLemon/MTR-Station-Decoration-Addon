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

public class RailingStairBlock extends AbstractChangeModelBlock {

	public static final IntegerProperty TYPE = IntegerProperty.create("type", 0, 5);

	private final boolean mirror;
	private final int defaultType;

	public RailingStairBlock(BlockBehaviour.Properties properties, boolean mirror, int defaultType) {
		super(properties);
		this.mirror = mirror;
		this.defaultType = defaultType;
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		final int index = state.getValue(TYPE);
		if (index == 4) {
			return mirror
					? IBlock.getVoxelShapeByDirection(13, 0, 0, 16, 24, 3, state.getValue(FACING))
					: IBlock.getVoxelShapeByDirection(0, 0, 0, 3, 24, 3, state.getValue(FACING));
		}
		if (index == 5) {
			final VoxelShape shape1;
			final VoxelShape shape2;
			if (mirror) {
				shape1 = IBlock.getVoxelShapeByDirection(1, 0, 1, 16, 24, 3, state.getValue(FACING));
				shape2 = IBlock.getVoxelShapeByDirection(1, 0, 1, 3, 24, 16, state.getValue(FACING));
			} else {
				shape1 = IBlock.getVoxelShapeByDirection(0, 0, 1, 15, 24, 3, state.getValue(FACING));
				shape2 = IBlock.getVoxelShapeByDirection(13, 0, 1, 15, 24, 16, state.getValue(FACING));
			}
			return Shapes.or(shape1, shape2);
		}
		return IBlock.getVoxelShapeByDirection(0, 0, 1, 16, 24, 3, state.getValue(FACING));
	}

	@Override
	protected int defaultTypeValue() {
		return defaultType;
	}

	@Override
	protected Property<Integer> getTypeProperty() {
		return TYPE;
	}
}
