package top.mcmtr.mixin;

import net.minecraft.core.BlockPos;
import org.mtr.screen.RailwaySignScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RailwaySignScreen.class)
public interface RailwaySignScreenAccessor {

	@Accessor(value = "signPos", remap = false)
	BlockPos msd$getSignPos();
}
