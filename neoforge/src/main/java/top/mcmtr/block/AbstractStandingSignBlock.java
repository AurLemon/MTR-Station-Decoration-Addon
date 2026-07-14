package top.mcmtr.block;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.mtr.block.IBlock;
import org.mtr.registry.RegistryServer;
import top.mcmtr.packet.MSDPacketOpenCustomScreen;

public abstract class AbstractStandingSignBlock extends Block implements EntityBlock {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final IntegerProperty TYPE = IntegerProperty.create("type", 0, 5);

	private final Supplier<BlockEntityType<StandingSignBlockEntity>> blockEntityType;
	private final int maxMessages;

	protected AbstractStandingSignBlock(
			BlockBehaviour.Properties properties,
			int maxMessages,
			Supplier<BlockEntityType<StandingSignBlockEntity>> blockEntityType) {
		super(properties);
		this.blockEntityType = blockEntityType;
		this.maxMessages = maxMessages;
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(TYPE, 0));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, TYPE);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getCounterClockWise()).setValue(TYPE, 0);
	}

	@Override
	protected BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	protected BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new StandingSignBlockEntity(maxMessages, blockEntityType.get(), pos, state);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		return openScreenWithBrush(level, pos, player);
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
		final InteractionResult result = openScreenWithBrush(level, pos, player);
		return result.consumesAction() ? ItemInteractionResult.SUCCESS : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Override
	protected abstract VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context);

	private InteractionResult openScreenWithBrush(Level level, BlockPos pos, Player player) {
		return IBlock.checkHoldingBrush(level, player, () -> {
			if (player instanceof ServerPlayer serverPlayer) {
				RegistryServer.sendPacketToClient(serverPlayer, new MSDPacketOpenCustomScreen(pos, maxMessages));
			}
		});
	}

	public static class StandingSignBlockEntity extends BlockEntity {

		private static final String KEY_MESSAGE = "msd_custom_message";

		private final String[] messages;

		public StandingSignBlockEntity(int maxMessages, BlockEntityType<?> type, BlockPos pos, BlockState state) {
			super(type, pos, state);
			this.messages = new String[maxMessages];
		}

		public void setMessages(String[] messages) {
			System.arraycopy(messages, 0, this.messages, 0, Math.min(messages.length, this.messages.length));
			setChanged();
			if (level != null) {
				level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
			}
		}

		public String getMessage(int index) {
			if (index >= 0 && index < messages.length) {
				return messages[index] == null ? "" : messages[index];
			}
			return "";
		}

		@Override
		public CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider registries) {
			return saveWithoutMetadata(registries);
		}

		@Override
		public ClientboundBlockEntityDataPacket getUpdatePacket() {
			return ClientboundBlockEntityDataPacket.create(this);
		}

		@Override
		protected void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
			super.loadAdditional(tag, registries);
			for (int i = 0; i < messages.length; i++) {
				messages[i] = tag.getString(KEY_MESSAGE + i);
			}
		}

		@Override
		protected void saveAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
			super.saveAdditional(tag, registries);
			for (int i = 0; i < messages.length; i++) {
				tag.putString(KEY_MESSAGE + i, messages[i] == null ? "" : messages[i]);
			}
		}
	}
}
