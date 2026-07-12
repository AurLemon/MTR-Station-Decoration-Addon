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
import org.mtr.core.tool.Angle;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import top.mcmtr.block.RigidCatenaryNodeBlock;
import top.mcmtr.core.data.RigidCatenary;
import top.mcmtr.core.operation.MSDDeleteDataRequest;
import top.mcmtr.core.operation.MSDUpdateDataRequest;
import top.mcmtr.init.MSDNeoForgeRuntime;

public final class RigidCatenaryConnectorItem extends Item {

	private static final String KEY_ROOT = "msd_rigid_catenary_tool";
	private static final String KEY_POS = "pos";

	private final boolean isConnector;

	public RigidCatenaryConnectorItem(Properties properties, boolean isConnector) {
		super(properties.stacksTo(1));
		this.isConnector = isConnector;
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		if (!(context.getPlayer() instanceof ServerPlayer serverPlayer) || !(context.getLevel() instanceof ServerLevel serverLevel)) {
			return InteractionResult.SUCCESS;
		}

		final BlockPos clickedPos = context.getClickedPos();
		final BlockState clickedState = serverLevel.getBlockState(clickedPos);
		if (!(clickedState.getBlock() instanceof RigidCatenaryNodeBlock)) {
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
		if (!(firstState.getBlock() instanceof RigidCatenaryNodeBlock)) {
			return InteractionResult.FAIL;
		}

		if (isConnector) {
			return connect(serverLevel, firstPos, clickedPos, firstState, clickedState);
		} else {
			return remove(serverLevel, firstPos, clickedPos, firstState, clickedState);
		}
	}

	public static InteractionResult connect(ServerLevel level, BlockPos firstPos, BlockPos secondPos, BlockState firstState, BlockState secondState) {
		final Position startPosition = toPosition(firstPos);
		final Position endPosition = toPosition(secondPos);
		if (!RigidCatenary.verifyPosition(startPosition, endPosition)) {
			return InteractionResult.FAIL;
		}

		final ObjectObjectImmutablePair<Angle, Angle> angles = getAngles(firstPos, RigidCatenaryNodeBlock.getAngle(firstState), secondPos, RigidCatenaryNodeBlock.getAngle(secondState));
		final RigidCatenary rigidCatenary = new RigidCatenary(startPosition, angles.left(), endPosition, angles.right(), RigidCatenary.Shape.QUADRATIC, 0);
		final MSDNeoForgeRuntime runtime = MSDNeoForgeRuntime.getInstance();
		if (runtime == null || !runtime.sendUpdate(level, new MSDUpdateDataRequest(runtime.newClientData()).addRigidCatenary(rigidCatenary))) {
			return InteractionResult.FAIL;
		}

		level.setBlock(firstPos, firstState.setValue(RigidCatenaryNodeBlock.IS_CONNECTED, true), 3);
		level.setBlock(secondPos, secondState.setValue(RigidCatenaryNodeBlock.IS_CONNECTED, true), 3);
		return InteractionResult.SUCCESS;
	}

	public static InteractionResult remove(ServerLevel level, BlockPos firstPos, BlockPos secondPos, BlockState firstState, BlockState secondState) {
		final String rigidCatenaryId = TwoPositionsBase.getHexId(toPosition(firstPos), toPosition(secondPos));
		final MSDNeoForgeRuntime runtime = MSDNeoForgeRuntime.getInstance();
		if (runtime == null || !runtime.sendDelete(level, new MSDDeleteDataRequest().addRigidCatenaryId(rigidCatenaryId))) {
			return InteractionResult.FAIL;
		}

		level.setBlock(firstPos, firstState.setValue(RigidCatenaryNodeBlock.IS_CONNECTED, false), 3);
		level.setBlock(secondPos, secondState.setValue(RigidCatenaryNodeBlock.IS_CONNECTED, false), 3);
		return InteractionResult.SUCCESS;
	}

	public static ObjectObjectImmutablePair<Angle, Angle> getAngles(BlockPos firstPos, float firstAngle, BlockPos secondPos, float secondAngle) {
		final float angleDifference = (float) Math.toDegrees(Math.atan2(secondPos.getZ() - firstPos.getZ(), secondPos.getX() - firstPos.getX()));
		return new ObjectObjectImmutablePair<>(
				Angle.fromAngle(firstAngle + (Angle.similarFacing(angleDifference, firstAngle) ? 0 : 180)),
				Angle.fromAngle(secondAngle + (Angle.similarFacing(angleDifference, secondAngle) ? 180 : 0))
		);
	}

	private static Position toPosition(BlockPos pos) {
		return new Position(pos.getX(), pos.getY(), pos.getZ());
	}
}
