package top.mcmtr.block;

import java.util.function.Supplier;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class Yamanote5PIDSBlock extends AbstractYamanotePIDSBlock {

	public Yamanote5PIDSBlock(BlockBehaviour.Properties properties, Supplier<BlockEntityType<YamanotePIDSBlockEntity>> blockEntityType) {
		super(properties, 21, 21, 22, blockEntityType);
	}
}
