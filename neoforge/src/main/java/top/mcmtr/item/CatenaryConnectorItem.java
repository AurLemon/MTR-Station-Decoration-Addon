package top.mcmtr.item;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import org.mtr.core.data.Position;
import org.mtr.core.data.TwoPositionsBase;
import top.mcmtr.block.OldNodeBlock;
import top.mcmtr.core.data.Catenary;
import top.mcmtr.core.data.CatenaryType;
import top.mcmtr.core.data.OffsetPosition;
import top.mcmtr.core.operation.MSDDeleteDataRequest;
import top.mcmtr.core.operation.MSDUpdateDataRequest;
import top.mcmtr.init.MSDNeoForgeRuntime;

public final class CatenaryConnectorItem extends Item {

	private static final String KEY_ROOT = "msd_catenary_tool";
	private static final String KEY_POS = "pos";

	private final boolean isConnector;
	private final CatenaryType catenaryType;

	public CatenaryConnectorItem(Properties properties, boolean isConnector, CatenaryType catenaryType) {
		super(properties.stacksTo(1));
		this.isConnector = isConnector;
		this.catenaryType = catenaryType;
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		if (!(context.getPlayer() instanceof ServerPlayer serverPlayer) || !(context.getLevel() instanceof ServerLevel serverLevel)) {
			return InteractionResult.SUCCESS;
		}

		final BlockPos clickedPos = context.getClickedPos();
		final BlockState clickedState = serverLevel.getBlockState(clickedPos);
		if (!(clickedState.getBlock() instanceof OldNodeBlock)) {
			return InteractionResult.FAIL;
		}

		final CompoundTag rootTag = serverPlayer.getPersistentData().getCompound(KEY_ROOT);
		if (!rootTag.contains(KEY_POS)) {
			rootTag.putLong(KEY_POS, clickedPos.asLong());
			serverPlayer.getPersistentData().put(KEY_ROOT, rootTag);
			return InteractionResult.SUCCESS;
		}

		final BlockPos firstPos = BlockPos.of(rootTag.getLong(KEY_POS));
		serverPlayer.getPersistentData().remove(KEY_ROOT);
		if (firstPos.equals(clickedPos)) {
			return InteractionResult.SUCCESS;
		}

		final BlockState firstState = serverLevel.getBlockState(firstPos);
		if (!(firstState.getBlock() instanceof OldNodeBlock)) {
			return InteractionResult.FAIL;
		}

		return isConnector ? connect(serverLevel, firstPos, clickedPos, firstState, clickedState, catenaryType) : remove(serverLevel, firstPos, clickedPos, firstState, clickedState);
	}

	public static InteractionResult connect(ServerLevel level, BlockPos firstPos, BlockPos secondPos, BlockState firstState, BlockState secondState, CatenaryType catenaryType) {
		final OldNodeBlock.OldNodeBlockEntity firstBlockEntity = getNodeBlockEntity(level, firstPos);
		final OldNodeBlock.OldNodeBlockEntity secondBlockEntity = getNodeBlockEntity(level, secondPos);
		if (firstBlockEntity == null || secondBlockEntity == null || catenaryType == CatenaryType.NONE || catenaryType == CatenaryType.RIGID_CATENARY) {
			return InteractionResult.FAIL;
		}

		final Position startPosition = toPosition(firstPos);
		final Position endPosition = toPosition(secondPos);
		final OffsetPosition startOffset = new OffsetPosition(firstBlockEntity.getOffsetX(), firstBlockEntity.getOffsetY(), firstBlockEntity.getOffsetZ());
		final OffsetPosition endOffset = new OffsetPosition(secondBlockEntity.getOffsetX(), secondBlockEntity.getOffsetY(), secondBlockEntity.getOffsetZ());
		if (!Catenary.verifyPosition(startPosition, endPosition, startOffset, endOffset)) {
			return InteractionResult.FAIL;
		}

		final Catenary catenary = new Catenary(startPosition, endPosition, startOffset, endOffset, catenaryType);
		final MSDNeoForgeRuntime runtime = MSDNeoForgeRuntime.getInstance();
		if (runtime == null || !runtime.sendUpdate(level, new MSDUpdateDataRequest(runtime.newClientData()).addCatenary(catenary))) {
			return InteractionResult.FAIL;
		}

		level.setBlock(firstPos, firstState.setValue(OldNodeBlock.IS_CONNECTED, true), 3);
		level.setBlock(secondPos, secondState.setValue(OldNodeBlock.IS_CONNECTED, true), 3);
		return InteractionResult.SUCCESS;
	}

	public static InteractionResult remove(ServerLevel level, BlockPos firstPos, BlockPos secondPos, BlockState firstState, BlockState secondState) {
		final String catenaryId = TwoPositionsBase.getHexId(toPosition(firstPos), toPosition(secondPos));
		final MSDNeoForgeRuntime runtime = MSDNeoForgeRuntime.getInstance();
		if (runtime == null || !runtime.sendDelete(level, new MSDDeleteDataRequest().addCatenaryId(catenaryId))) {
			return InteractionResult.FAIL;
		}

		level.setBlock(firstPos, firstState.setValue(OldNodeBlock.IS_CONNECTED, false), 3);
		level.setBlock(secondPos, secondState.setValue(OldNodeBlock.IS_CONNECTED, false), 3);
		return InteractionResult.SUCCESS;
	}

	private static OldNodeBlock.OldNodeBlockEntity getNodeBlockEntity(ServerLevel level, BlockPos pos) {
		return level.getBlockEntity(pos) instanceof OldNodeBlock.OldNodeBlockEntity oldNodeBlockEntity ? oldNodeBlockEntity : null;
	}

	private static Position toPosition(BlockPos pos) {
		return new Position(pos.getX(), pos.getY(), pos.getZ());
	}
}
