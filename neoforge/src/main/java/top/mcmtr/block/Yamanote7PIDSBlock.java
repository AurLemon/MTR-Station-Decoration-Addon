package top.mcmtr.block;

import java.util.function.Supplier;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class Yamanote7PIDSBlock extends AbstractYamanotePIDSBlock {

	public Yamanote7PIDSBlock(BlockBehaviour.Properties properties, Supplier<BlockEntityType<YamanotePIDSBlockEntity>> blockEntityType) {
		super(properties, 29, 29, 30, blockEntityType);
	}
}
