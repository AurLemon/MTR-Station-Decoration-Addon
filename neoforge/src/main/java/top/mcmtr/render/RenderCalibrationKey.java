package top.mcmtr.render;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.state.BlockState;

public final class RenderCalibrationKey {

	private RenderCalibrationKey() {
	}

	public static String fromBlockState(BlockState blockState) {
		return BuiltInRegistries.BLOCK.getKey(blockState.getBlock()).getPath();
	}
}
