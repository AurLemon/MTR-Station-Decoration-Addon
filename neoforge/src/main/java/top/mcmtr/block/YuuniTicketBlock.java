package top.mcmtr.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.mtr.block.BlockDirectionalDoubleBlockBase;
import org.mtr.block.IBlock;
import org.mtr.data.TicketSystem;
import org.mtr.packet.PacketOpenTicketMachineScreen;
import org.mtr.registry.RegistryServer;

public class YuuniTicketBlock extends BlockDirectionalDoubleBlockBase {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

	public YuuniTicketBlock(BlockBehaviour.Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(HALF, IBlock.DoubleBlockHalf.LOWER));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
		builder.add(FACING, HALF);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		final BlockState state = super.getStateForPlacement(context);
		return state == null ? null : state.setValue(FACING, context.getHorizontalDirection());
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
			RegistryServer.sendPacketToClient(serverPlayer, new PacketOpenTicketMachineScreen(TicketSystem.getBalance(level, player)));
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		final Direction facing = state.getValue(FACING);
		final int height = state.getValue(HALF) == IBlock.DoubleBlockHalf.UPPER ? 15 : 16;
		return IBlock.getVoxelShapeByDirection(0, 0, 2, 16, height, 14, facing);
	}

	@Override
	protected BlockState getAdditionalState(BlockPos pos, Direction facing) {
		return defaultBlockState().setValue(FACING, facing).setValue(HALF, IBlock.DoubleBlockHalf.UPPER);
	}
}
