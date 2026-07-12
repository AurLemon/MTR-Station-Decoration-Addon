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
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.mtr.block.IBlock;
import org.mtr.registry.RegistryServer;
import top.mcmtr.packet.MSDPacketOpenCatenaryWithModelScreen;

public class BlockCatenaryWithModel extends Block implements EntityBlock {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty IS_CONNECTED = BooleanProperty.create("is_connected");

	private final CatenaryModel catenaryModel;
	private final Supplier<BlockEntityType<BlockCatenaryWithModelEntity>> blockEntityType;

	public BlockCatenaryWithModel(
			BlockBehaviour.Properties properties,
			CatenaryModel catenaryModel,
			Supplier<BlockEntityType<BlockCatenaryWithModelEntity>> blockEntityType) {
		super(properties);
		this.catenaryModel = catenaryModel;
		this.blockEntityType = blockEntityType;
		registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(IS_CONNECTED, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, IS_CONNECTED);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState()
				.setValue(FACING, context.getHorizontalDirection().getOpposite())
				.setValue(IS_CONNECTED, false);
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
	protected RenderShape getRenderShape(BlockState state) {
		return RenderShape.INVISIBLE;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new BlockCatenaryWithModelEntity(catenaryModel, blockEntityType.get(), pos, state);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		return openScreenWithBrush(state, level, pos, player);
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
		final InteractionResult result = openScreenWithBrush(state, level, pos, player);
		return result.consumesAction() ? ItemInteractionResult.SUCCESS : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return Shapes.block();
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}

	private InteractionResult openScreenWithBrush(BlockState state, Level level, BlockPos pos, Player player) {
		return IBlock.checkHoldingBrush(level, player, () -> {
			if (player instanceof ServerPlayer serverPlayer) {
				RegistryServer.sendPacketToClient(serverPlayer, new MSDPacketOpenCatenaryWithModelScreen(pos, state.getValue(IS_CONNECTED)));
			}
		});
	}

	public static class BlockCatenaryWithModelEntity extends BlockEntity {

		private static final String KEY_OFFSET_POSITION = "msd_offset_position_";
		private static final String KEY_ROTATE_POSITION = "msd_rotate_position_";

		private final CatenaryModel catenaryModel;
		private double offsetX;
		private double offsetY;
		private double offsetZ;
		private double rotationX;
		private double rotationY;
		private double rotationZ;

		public BlockCatenaryWithModelEntity(CatenaryModel catenaryModel, BlockEntityType<?> type, BlockPos pos, BlockState state) {
			super(type, pos, state);
			this.catenaryModel = catenaryModel;
		}

		public CatenaryModel getCatenaryModel() {
			return catenaryModel;
		}

		public double getOffsetX() {
			return offsetX;
		}

		public double getOffsetY() {
			return offsetY;
		}

		public double getOffsetZ() {
			return offsetZ;
		}

		public double getRotationX() {
			return rotationX;
		}

		public double getRotationY() {
			return rotationY;
		}

		public double getRotationZ() {
			return rotationZ;
		}

		public void setTransform(double offsetX, double offsetY, double offsetZ, double rotationX, double rotationY, double rotationZ) {
			this.offsetX = offsetX;
			this.offsetY = offsetY;
			this.offsetZ = offsetZ;
			this.rotationX = rotationX;
			this.rotationY = rotationY;
			this.rotationZ = rotationZ;
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
		protected void saveAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
			super.saveAdditional(tag, registries);
			tag.putDouble(KEY_OFFSET_POSITION + "x", offsetX);
			tag.putDouble(KEY_OFFSET_POSITION + "y", offsetY);
			tag.putDouble(KEY_OFFSET_POSITION + "z", offsetZ);
			tag.putDouble(KEY_ROTATE_POSITION + "x", rotationX);
			tag.putDouble(KEY_ROTATE_POSITION + "y", rotationY);
			tag.putDouble(KEY_ROTATE_POSITION + "z", rotationZ);
		}

		@Override
		protected void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
			super.loadAdditional(tag, registries);
			offsetX = tag.getDouble(KEY_OFFSET_POSITION + "x");
			offsetY = tag.getDouble(KEY_OFFSET_POSITION + "y");
			offsetZ = tag.getDouble(KEY_OFFSET_POSITION + "z");
			rotationX = tag.getDouble(KEY_ROTATE_POSITION + "x");
			rotationY = tag.getDouble(KEY_ROTATE_POSITION + "y");
			rotationZ = tag.getDouble(KEY_ROTATE_POSITION + "z");
		}
	}

	public enum CatenaryModel {
		CATENARY_LONG("catenary_long"),
		CATENARY_LONG_TOP("catenary_long_top"),
		CATENARY_LONG_COUNTERWEIGHT("catenary_long_counterweight"),
		CATENARY_LONG_COUNTERWEIGHT_MIRROR("catenary_long_counterweight_mirror"),
		CATENARY_SHORT("catenary_short"),
		CATENARY_SHORT_TOP("catenary_short_top"),
		CATENARY_SHORT_COUNTERWEIGHT("catenary_short_counterweight"),
		CATENARY_SHORT_COUNTERWEIGHT_MIRROR("catenary_short_counterweight_mirror");

		private final String modelId;

		CatenaryModel(String modelId) {
			this.modelId = modelId;
		}

		public String getModelId() {
			return modelId;
		}
	}
}
