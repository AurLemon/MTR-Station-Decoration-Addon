package top.mcmtr.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.mtr.block.IBlock;
import org.mtr.core.data.Position;
import org.mtr.registry.RegistryServer;
import top.mcmtr.core.data.RigidCatenary;
import top.mcmtr.core.operation.MSDDeleteDataRequest;
import top.mcmtr.init.MSDNeoForgeRuntime;
import top.mcmtr.packet.MSDPacketOpenRigidCatenaryShapeScreen;
import top.mcmtr.registry.MSDBlockEntities;

public class RigidCatenaryNodeBlock extends Block implements EntityBlock {

	public static final BooleanProperty FACING = BooleanProperty.create("facing");
	public static final BooleanProperty IS_22_5 = BooleanProperty.create("is_22_5");
	public static final BooleanProperty IS_45 = BooleanProperty.create("is_45");
	public static final BooleanProperty IS_CONNECTED = BooleanProperty.create("is_connected");

	public RigidCatenaryNodeBlock(BlockBehaviour.Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState()
				.setValue(FACING, false)
				.setValue(IS_22_5, false)
				.setValue(IS_45, false)
				.setValue(IS_CONNECTED, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, IS_22_5, IS_45, IS_CONNECTED);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		final int quadrant = Mth.floor((context.getRotation() + 22.5F) / 45.0F) & 7;
		return defaultBlockState()
				.setValue(FACING, quadrant >= 4)
				.setValue(IS_45, quadrant % 4 >= 2)
				.setValue(IS_22_5, quadrant % 2 >= 1)
				.setValue(IS_CONNECTED, false);
	}

	public static float getAngle(BlockState state) {
		return (state.getValue(FACING) ? 0 : 90) + (state.getValue(IS_22_5) ? 22.5F : 0) + (state.getValue(IS_45) ? 45 : 0);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new RigidCatenaryNodeBlockEntity(MSDBlockEntities.RIGID_CATENARY_NODE.get(), pos, state);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		return openShapeScreen(level, pos, player);
	}

	@Override
	protected ItemInteractionResult useItemOn(
			ItemStack stack,
			BlockState state,
			Level level,
			BlockPos pos,
			Player player,
			InteractionHand hand,
			BlockHitResult hitResult) {
		final InteractionResult result = openShapeScreen(level, pos, player);
		return result.consumesAction() ? ItemInteractionResult.SUCCESS : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Override
	public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, net.minecraft.world.entity.player.Player player) {
		if (!level.isClientSide) {
			if (level instanceof ServerLevel serverLevel) {
				final MSDNeoForgeRuntime runtime = MSDNeoForgeRuntime.getInstance();
				if (runtime != null) {
					runtime.sendDelete(serverLevel, new MSDDeleteDataRequest().addCatenaryNodePosition(new Position(pos.getX(), pos.getY(), pos.getZ())));
				}
			}
			level.setBlock(pos, state.setValue(IS_CONNECTED, false), 3);
		}
		return super.playerWillDestroy(level, pos, state, player);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return Shapes.block();
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}

	private InteractionResult openShapeScreen(Level level, BlockPos pos, Player player) {
		return IBlock.checkHoldingBrush(level, player, () -> {
			if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
				return;
			}

			final MSDNeoForgeRuntime runtime = MSDNeoForgeRuntime.getInstance();
			if (runtime == null) {
				return;
			}

			final RigidCatenary rigidCatenary = runtime.getRigidCatenary(serverLevel, pos);
			if (rigidCatenary != null) {
				RegistryServer.sendPacketToClient(serverPlayer, new MSDPacketOpenRigidCatenaryShapeScreen(rigidCatenary));
			}
		});
	}

	public static class RigidCatenaryNodeBlockEntity extends BlockEntity {

		private static final String KEY_OFFSET_POSITION = "msd_offset_position_";
		private double offsetX;
		private double offsetY;
		private double offsetZ;

		public RigidCatenaryNodeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
			super(type, pos, state);
		}

		@Override
		protected void saveAdditional(net.minecraft.nbt.CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
			super.saveAdditional(tag, registries);
			tag.putDouble(KEY_OFFSET_POSITION + "x", offsetX);
			tag.putDouble(KEY_OFFSET_POSITION + "y", offsetY);
			tag.putDouble(KEY_OFFSET_POSITION + "z", offsetZ);
		}

		@Override
		public net.minecraft.nbt.CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider registries) {
			return saveWithoutMetadata(registries);
		}

		@Override
		public ClientboundBlockEntityDataPacket getUpdatePacket() {
			return ClientboundBlockEntityDataPacket.create(this);
		}

		@Override
		protected void loadAdditional(net.minecraft.nbt.CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
			super.loadAdditional(tag, registries);
			offsetX = tag.getDouble(KEY_OFFSET_POSITION + "x");
			offsetY = tag.getDouble(KEY_OFFSET_POSITION + "y");
			offsetZ = tag.getDouble(KEY_OFFSET_POSITION + "z");
		}
	}
}
