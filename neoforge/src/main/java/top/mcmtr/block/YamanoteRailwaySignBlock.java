package top.mcmtr.block;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.mtr.block.IBlock;
import org.mtr.libraries.it.unimi.dsi.fastutil.longs.LongAVLTreeSet;
import org.mtr.registry.RegistryServer;
import top.mcmtr.packet.MSDPacketOpenBlockEntityScreen;
import top.mcmtr.registry.MSDBlocks;

public class YamanoteRailwaySignBlock extends Block implements EntityBlock {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final float SMALL_SIGN_PERCENTAGE = 0.75F;

	private static final String KEY_SELECTED_IDS = "yamanote_selected_ids";
	private static final String KEY_SIGN_LENGTH = "yamanote_sign_length";
	private static final ThreadLocal<Boolean> CHAIN_PLACEMENT_IN_PROGRESS = ThreadLocal.withInitial(() -> false);

	private final int length;
	private final boolean isOdd;
	private final Supplier<BlockEntityType<YamanoteRailwaySignBlockEntity>> blockEntityType;

	public YamanoteRailwaySignBlock(
			BlockBehaviour.Properties properties,
			int length,
			boolean isOdd,
			Supplier<BlockEntityType<YamanoteRailwaySignBlockEntity>> blockEntityType) {
		super(properties);
		this.length = length;
		this.isOdd = isOdd;
		this.blockEntityType = blockEntityType;
		registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		final Direction facing = context.getHorizontalDirection();
		return IBlock.isReplaceable(context, facing.getClockWise(), getMiddleLength() + 2)
				? defaultBlockState().setValue(FACING, facing)
				: null;
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
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (!level.isClientSide) {
			placeSignChain(level, pos, state);
		}
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		super.onPlace(state, level, pos, oldState, movedByPiston);
		if (!level.isClientSide && !CHAIN_PLACEMENT_IN_PROGRESS.get() && !oldState.is(state.getBlock()) && shouldCreateChain(level, pos, state)) {
			placeSignChain(level, pos, state);
		}
	}

	@Override
	protected BlockState updateShape(
			BlockState state,
			Direction direction,
			BlockState neighborState,
			LevelAccessor level,
			BlockPos pos,
			BlockPos neighborPos) {
		final Direction facing = state.getValue(FACING);
		final boolean isNext = direction == facing.getClockWise()
				|| state.is(MSDBlocks.YAMANOTE_RAILWAY_SIGN_MIDDLE.get()) && direction == facing.getCounterClockWise();
		return isNext && !(neighborState.getBlock() instanceof YamanoteRailwaySignBlock)
				? net.minecraft.world.level.block.Blocks.AIR.defaultBlockState()
				: state;
	}

	@Override
	public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		final Direction facing = state.getValue(FACING);
		final BlockPos checkPos = findEndWithDirection(level, pos, facing, true);
		if (checkPos != null && !checkPos.equals(pos)) {
			level.destroyBlock(checkPos, !player.isCreative(), player);
		}
		return super.playerWillDestroy(level, pos, state, player);
	}

	@Override
	protected RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		if (this == MSDBlocks.YAMANOTE_RAILWAY_SIGN_MIDDLE.get()) {
			return null;
		}
		return new YamanoteRailwaySignBlockEntity(length, isOdd, blockEntityType.get(), pos, state);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		final Direction facing = state.getValue(FACING);
		if (state.is(MSDBlocks.YAMANOTE_RAILWAY_SIGN_MIDDLE.get())) {
			return IBlock.getVoxelShapeByDirection(0, 0, 7, 16, 12, 9, facing);
		}
		final int xStart = getXStart();
		final VoxelShape main = IBlock.getVoxelShapeByDirection(xStart - 0.75, 0, 7, 16, 12, 9, facing);
		final VoxelShape pole = IBlock.getVoxelShapeByDirection(xStart - 2, 0, 7, xStart - 0.75, 16, 9, facing);
		return Shapes.or(main, pole);
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return getShape(state, level, pos, context);
	}

	@Override
	public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, java.util.List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.translatable("tooltip.mtr.railway_sign_length", length));
		tooltip.add(Component.translatable(isOdd ? "tooltip.mtr.railway_sign_odd" : "tooltip.mtr.railway_sign_even"));
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		return openScreenWithBrush(state, level, pos, player, hitResult);
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
		final InteractionResult result = openScreenWithBrush(state, level, pos, player, hitResult);
		return result.consumesAction() ? ItemInteractionResult.SUCCESS : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	public int getLength() {
		return length;
	}

	public boolean isOdd() {
		return isOdd;
	}

	public int getXStart() {
		switch (length % 4) {
			default:
				return isOdd ? 8 : 16;
			case 1:
				return isOdd ? 4 : 12;
			case 2:
				return isOdd ? 16 : 8;
			case 3:
				return isOdd ? 12 : 4;
		}
	}

	public int getMiddleLength() {
		return (length - (4 - getXStart() / 4)) / 2;
	}

	private boolean shouldCreateChain(Level level, BlockPos pos, BlockState state) {
		if (this == MSDBlocks.YAMANOTE_RAILWAY_SIGN_MIDDLE.get()) {
			return false;
		}
		final Direction facing = state.getValue(FACING);
		return !(level.getBlockState(pos.relative(facing.getClockWise())).getBlock() instanceof YamanoteRailwaySignBlock)
				&& !(level.getBlockState(pos.relative(facing.getCounterClockWise())).getBlock() instanceof YamanoteRailwaySignBlock);
	}

	private void placeSignChain(Level level, BlockPos pos, BlockState state) {
		if (this == MSDBlocks.YAMANOTE_RAILWAY_SIGN_MIDDLE.get()) {
			return;
		}
		final Direction facing = state.getValue(FACING);
		CHAIN_PLACEMENT_IN_PROGRESS.set(true);
		try {
			level.setBlock(pos.relative(facing.getClockWise(), getMiddleLength() + 1), defaultBlockState().setValue(FACING, facing.getOpposite()), 3);
			for (int i = getMiddleLength(); i >= 1; i--) {
				level.setBlock(pos.relative(facing.getClockWise(), i), MSDBlocks.YAMANOTE_RAILWAY_SIGN_MIDDLE.get().defaultBlockState().setValue(FACING, facing), 3);
			}
		} finally {
			CHAIN_PLACEMENT_IN_PROGRESS.set(false);
		}
	}

	private InteractionResult openScreenWithBrush(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		return IBlock.checkHoldingBrush(level, player, () -> {
			final Direction facing = state.getValue(FACING);
			final Direction hitSide = hitResult.getDirection();
			if (hitSide == facing || hitSide == facing.getOpposite()) {
				final BlockPos checkPos = findEndWithDirection(level, pos, hitSide.getOpposite(), false);
				if (checkPos != null && player instanceof ServerPlayer serverPlayer) {
					RegistryServer.sendPacketToClient(serverPlayer, new MSDPacketOpenBlockEntityScreen(checkPos));
				}
			}
		});
	}

	private BlockPos findEndWithDirection(LevelReader level, BlockPos startPos, Direction direction, boolean allowOpposite) {
		int offset = 0;
		while (true) {
			final BlockPos checkPos = startPos.relative(direction.getCounterClockWise(), offset);
			final BlockState checkState = level.getBlockState(checkPos);
			if (checkState.getBlock() instanceof YamanoteRailwaySignBlock) {
				final Direction facing = checkState.getValue(FACING);
				if (!checkState.is(MSDBlocks.YAMANOTE_RAILWAY_SIGN_MIDDLE.get()) && (facing == direction || allowOpposite && facing == direction.getOpposite())) {
					return checkPos;
				}
			} else {
				return null;
			}
			offset++;
		}
	}

	public static class YamanoteRailwaySignBlockEntity extends BlockEntity {

		private final LongAVLTreeSet[] selectedIds;
		private final String[] signIds;

		public YamanoteRailwaySignBlockEntity(int length, boolean isOdd, BlockEntityType<?> type, BlockPos pos, BlockState state) {
			super(type, pos, state);
			this.selectedIds = new LongAVLTreeSet[length];
			this.signIds = new String[length];
			for (int i = 0; i < length; i++) {
				this.selectedIds[i] = new LongAVLTreeSet();
			}
		}

		public LongAVLTreeSet[] getSelectedIds() {
			return selectedIds;
		}

		public String[] getSignIds() {
			return signIds;
		}

		public void setData(LongAVLTreeSet selectedIds, String[] signTypes) {
			final LongAVLTreeSet[] selectedIdsBySlot = getSelectedIds();
			final LongAVLTreeSet[] updatedSelectedIds = new LongAVLTreeSet[selectedIdsBySlot.length];
			for (int i = 0; i < selectedIdsBySlot.length; i++) {
				updatedSelectedIds[i] = new LongAVLTreeSet(selectedIds);
			}
			setData(updatedSelectedIds, signTypes);
		}

		public void setData(LongAVLTreeSet[] selectedIds, String[] signTypes) {
			for (int i = 0; i < this.selectedIds.length; i++) {
				this.selectedIds[i].clear();
				if (i < selectedIds.length && selectedIds[i] != null) {
					this.selectedIds[i].addAll(selectedIds[i]);
				}
			}
			if (signIds.length == signTypes.length) {
				System.arraycopy(signTypes, 0, signIds, 0, signTypes.length);
			}
			setChanged();
			if (level != null) {
				level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
			}
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
			for (int i = 0; i < selectedIds.length; i++) {
				selectedIds[i].clear();
			}
			if (tag.contains(KEY_SELECTED_IDS) && selectedIds.length > 0 && selectedIds[0].isEmpty()) {
				final LongAVLTreeSet legacySelectedIds = new LongAVLTreeSet(tag.getLongArray(KEY_SELECTED_IDS));
				for (int i = 0; i < selectedIds.length; i++) {
					selectedIds[i].clear();
					selectedIds[i].addAll(legacySelectedIds);
				}
			}
			for (int i = 0; i < signIds.length; i++) {
				if (tag.contains("selected_ids_" + i)) {
					selectedIds[i].addAll(new LongAVLTreeSet(tag.getLongArray("selected_ids_" + i)));
				}
				if (tag.contains(KEY_SIGN_LENGTH + i)) {
					final String signId = tag.getString(KEY_SIGN_LENGTH + i);
					signIds[i] = signId.isEmpty() ? null : signId;
				} else {
					signIds[i] = null;
				}
			}
		}

		@Override
		protected void saveAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
			super.saveAdditional(tag, registries);
			if (selectedIds.length > 0) {
				tag.putLongArray(KEY_SELECTED_IDS, new java.util.ArrayList<>(selectedIds[0]));
			}
			for (int i = 0; i < signIds.length; i++) {
				tag.putLongArray("selected_ids_" + i, new java.util.ArrayList<>(selectedIds[i]));
				tag.putString(KEY_SIGN_LENGTH + i, signIds[i] == null ? "" : signIds[i]);
			}
		}
	}
}
