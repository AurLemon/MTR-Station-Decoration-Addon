package top.mcmtr.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.mtr.libraries.it.unimi.dsi.fastutil.longs.LongAVLTreeSet;
import org.mtr.packet.PacketUpdateRailwaySignConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.mcmtr.block.YamanoteRailwaySignBlock;

@Mixin(PacketUpdateRailwaySignConfig.class)
public abstract class PacketUpdateRailwaySignConfigMixin {

	@Shadow(remap = false)
	@Final
	private BlockPos blockPos;

	@Shadow(remap = false)
	@Final
	private LongAVLTreeSet[] selectedIds;

	@Shadow(remap = false)
	@Final
	private String[] signIds;

	@Inject(method = "setData", at = @At("HEAD"), cancellable = true, remap = false)
	private void handleMsdYamanote(Level level, CallbackInfo ci) {
		if (level == null) {
			return;
		}
		if (level.getBlockEntity(blockPos) instanceof YamanoteRailwaySignBlock.YamanoteRailwaySignBlockEntity signBlockEntity) {
			signBlockEntity.setData(selectedIds, signIds);
			ci.cancel();
		}
	}
}
