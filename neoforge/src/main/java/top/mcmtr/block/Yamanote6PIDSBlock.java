package top.mcmtr.block;

import java.util.function.Supplier;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class Yamanote6PIDSBlock extends AbstractYamanotePIDSBlock {

	public Yamanote6PIDSBlock(BlockBehaviour.Properties properties, Supplier<BlockEntityType<YamanotePIDSBlockEntity>> blockEntityType) {
		super(properties, 25, 25, 26, blockEntityType);
	}
}
