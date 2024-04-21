package dev.sapphic.wearablebackpacks.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import dev.sapphic.wearablebackpacks.Backpack;
import dev.sapphic.wearablebackpacks.Backpacks;
import dev.sapphic.wearablebackpacks.advancement.BackpackCriteria;
import dev.sapphic.wearablebackpacks.block.entity.BackpackBlockEntity;
import dev.sapphic.wearablebackpacks.initializers.BackpackBlocks;
import dev.sapphic.wearablebackpacks.initializers.BackpackSlotIntegrations;
import dev.sapphic.wearablebackpacks.integration.SlotHandler;
import dev.sapphic.wearablebackpacks.integration.TrinketsIntegration;
import dev.sapphic.wearablebackpacks.item.BackpackItem;
import dev.sapphic.wearablebackpacks.stat.BackpackStats;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.tick.OrderedTick;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

import static net.minecraft.state.property.Properties.HORIZONTAL_FACING;

@SuppressWarnings("deprecation")
public final class BackpackBlock extends BlockWithEntity implements Waterloggable {

    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    private static final Map<Direction, VoxelShape> SHAPES = getDirectionShapes();

    public BackpackBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(   this.stateManager.getDefaultState()
                .with(HORIZONTAL_FACING, Direction.NORTH)
                .with(WATERLOGGED, false)
        );
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {

        if (state.get(WATERLOGGED)) {
            world.getFluidTickScheduler().scheduleTick(OrderedTick.create(Fluids.WATER, pos));
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void onStateReplaced(final BlockState state, final World world, final BlockPos pos, final BlockState next, final boolean moved) {
        if (state.getBlock() != next.getBlock()) {
            if (world.getBlockEntity(pos) instanceof BackpackBlockEntity be) {
                ItemScatterer.spawn(world, pos, be);
                world.updateComparators(pos, this);
            }
        }
        super.onStateReplaced(state, world, pos, next, moved);
    }

    @Override
    public ActionResult onUse(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof BackpackBlockEntity backpack) {
            final ItemStack stack = player.getStackInHand(hand);
            player.incrementStat(BackpackStats.OPENED);

            if (player.canModifyAt(world,pos)) {
                if (stack.getItem() instanceof DyeItem dyeItem) return useDyeItemOnBlock(world, player, backpack, stack, dyeItem);
                if (backpack.hasColor()) {
                    ActionResult usedWater = tryUseWaterOnBlock(world, player, hand, backpack);
                    if (! usedWater.equals(ActionResult.PASS)) return usedWater;
                }
            }
            player.openHandledScreen(backpack);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    private ActionResult useDyeItemOnBlock(World world, PlayerEntity player, BackpackBlockEntity backpack, ItemStack dyeStack, DyeItem dye) {
        if (!world.isClient()) {
            final int newColor = this.getBlendedColor(backpack, dye);
            if (!backpack.hasColor() || (backpack.getColor() != newColor)) {
                backpack.setColor(newColor);
                world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_ITEM_FRAME_ADD_ITEM, player.getSoundCategory(),
                        0.5F, (world.getRandom().nextFloat() * 0.1F) + 0.9F
                );
                if (!player.getAbilities().creativeMode) {
                    dyeStack.decrement(1);
                }
                BackpackCriteria.DYED.trigger((ServerPlayerEntity) player);
            }
        }
        return ActionResult.success(world.isClient());
    }

    @SuppressWarnings("UnstableApiUsage")
    private static ActionResult tryUseWaterOnBlock(World world, PlayerEntity player, Hand hand, BackpackBlockEntity backpack) {
        Storage<FluidVariant> fluidVariantStorage = ContainerItemContext.forPlayerInteraction(player,hand).find(FluidStorage.ITEM);
        if (fluidVariantStorage != null && fluidVariantStorage.supportsExtraction()) {
            if (world.isClient()) return ActionResult.CONSUME;
            try (Transaction transaction = Transaction.openOuter()) {
                long extracted = fluidVariantStorage.extract(FluidVariant.of(Fluids.WATER), FluidConstants.BUCKET, transaction);
                if (extracted == FluidConstants.BUCKET) {
                    transaction.commit();
                    backpack.clearColor();
                    player.incrementStat(BackpackStats.CLEANED);
                    return ActionResult.SUCCESS;
                }
                transaction.abort();
                return ActionResult.FAIL;
            }
        }
        return ActionResult.PASS;
    }

    @Override
    @Deprecated
    public FluidState getFluidState(final BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    @Deprecated
    public boolean hasComparatorOutput(final BlockState state) {
        return true;
    }

    @Override
    @Deprecated
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        return state.with(HORIZONTAL_FACING, rotation.rotate(state.get(HORIZONTAL_FACING)));
    }

    @Override
    @Deprecated
    public BlockState mirror(final BlockState state, final BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(HORIZONTAL_FACING)));
    }

    @Override
    @Deprecated
    public int getComparatorOutput(final BlockState state, final World world, final BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    @Override
    @Deprecated
    public VoxelShape getOutlineShape(
            final BlockState state, final BlockView view, final BlockPos pos, final ShapeContext context
    ) {
        return SHAPES.get(state.get(HORIZONTAL_FACING));
    }

    @Override
    @Deprecated
    public float calcBlockBreakingDelta(
            final BlockState state, final PlayerEntity player, final BlockView world, final BlockPos pos
    ) {
        final @Nullable BlockEntity be = world.getBlockEntity(pos);
        if ((be instanceof BackpackBlockEntity) && ((Inventory) be).isEmpty()) {
            return super.calcBlockBreakingDelta(state, player, world, pos);
        }
        if (player.isSneaking() && !(player.getEquippedStack(EquipmentSlot.CHEST).getItem() instanceof BackpackItem)) {
            return super.calcBlockBreakingDelta(state, player, world, pos);
        }
        return 0.005F;
    }

    @Override
    public @Nullable BlockState getPlacementState(final ItemPlacementContext context) {
        final Direction facing = context.getHorizontalPlayerFacing().getOpposite();
        final Fluid fluid = context.getWorld().getFluidState(context.getBlockPos()).getFluid();
        return this.getDefaultState().with(HORIZONTAL_FACING, facing).with(WATERLOGGED, fluid == Fluids.WATER);
    }

    @Override
    public void afterBreak(final World world, final PlayerEntity player, final BlockPos pos, final BlockState state, final @Nullable BlockEntity be, final ItemStack stack) {
        player.incrementStat(Stats.MINED.getOrCreateStat(this));
        player.addExhaustion(0.005F);
        if (!player.isSneaking()) {
            dropStacks(state, world, pos, be, player, stack);
        }
    }

    @Override
    public ItemStack getPickStack(final BlockView world, final BlockPos pos, final BlockState state) {
        return this.getPickStack(world.getBlockEntity(pos), world, pos, state);
    }

    @Override
    public void onBreak(final World world, final BlockPos pos, final BlockState state, final PlayerEntity player) {
        final @Nullable BlockEntity be = world.getBlockEntity(pos);
        List<SlotHandler> slotIntegrations = BackpackSlotIntegrations.streamEnabledIntegrations()
                                                .filter(handler->!handler.isBackpackEquipped(player))
                                                .toList();

        if ((be instanceof BackpackBlockEntity) && player.isSneaking() && !slotIntegrations.isEmpty()) {
            final ItemStack stack = this.getPickStack(be, world, pos, state);
            final NbtCompound tag = stack.getOrCreateSubNbt("BlockEntityTag");
            Inventories.writeNbt(tag, ((Backpack) be).getContents());
            // Both slots are empty
            // prefer trinket slot
            for (SlotHandler handler : slotIntegrations) {
                if (handler.equipBackpack(stack, player)) {
                    super.onBreak(world, pos, state, player);
                    world.removeBlockEntity(pos);
                    return;
                }
            }
        }
        super.onBreak(world, pos, state, player);
    }

    @Override
    protected void appendProperties(final StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(HORIZONTAL_FACING, WATERLOGGED);
    }

    @Override
    public void appendTooltip(final ItemStack stack, final @Nullable BlockView world, final List<Text> tooltip, final TooltipContext options) {
        super.appendTooltip(stack, world, tooltip, options);
        final NbtCompound tag = stack.getOrCreateSubNbt("BlockEntityTag");
        boolean hasItems = tag.contains("LootTable", NbtElement.STRING_TYPE);
        if (!hasItems && tag.contains("Items", NbtElement.LIST_TYPE)) {
            final DefaultedList<ItemStack> contents = DefaultedList.ofSize(27, ItemStack.EMPTY);
            Inventories.readNbt(tag, contents);
            for (final ItemStack contentsStack : contents) {
                if (!contentsStack.isEmpty()) {
                    hasItems = true;
                    break;
                }
            }
            if (hasItems) {
                tooltip.add(Text.translatable("container." + Backpacks.ID + ".items").formatted(Formatting.GOLD));
            }
        }
    }

    @Override
    public BlockRenderType getRenderType(final BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BackpackBlockEntity(pos, state);
    }

    private int getBlendedColor(final Backpack backpack, final DyeItem dye) {
        if (backpack.hasColor()) {
            final ItemStack tmp = new ItemStack(this);
            final DyeableItem item = (DyeableItem) tmp.getItem();
            item.setColor(tmp, backpack.getColor());
            return item.getColor(DyeableItem.blendAndSetColor(tmp, ImmutableList.of(dye)));
        }
        float[] colors = dye.getColor().getColorComponents();
        int red = 0xFF & (int)(colors[0]*255);
        int blue = 0xFF & (int)(colors[1]*255);
        int green = 0xFF & (int)(colors[2] * 255);
        return (red << 16) | (blue << 8) | green;
    }

    private ItemStack getPickStack(
            final BlockEntity be, final BlockView world, final BlockPos pos, final BlockState state
    ) {
        final ItemStack stack = super.getPickStack(world, pos, state);
        if (be instanceof BackpackBlockEntity) {
            ((BackpackBlockEntity) be).writeToStack(stack);
        }
        return stack;
    }

    private static Map<Direction, VoxelShape> getDirectionShapes() {
        return ImmutableMap.of(
                Direction.NORTH, VoxelShapes.union(
                        VoxelShapes.cuboid(0.1875, 0.0, 0.375, 0.8125, 0.75, 0.6875),
                        VoxelShapes.cuboid(0.25, 0.0625, 0.25, 0.75, 0.4375, 0.375)
                ),
                Direction.EAST, VoxelShapes.union(
                        VoxelShapes.cuboid(0.3125, 0.0, 0.1875, 0.625, 0.75, 0.8125),
                        VoxelShapes.cuboid(0.625, 0.0625, 0.25, 0.75, 0.4375, 0.75)
                ),
                Direction.SOUTH, VoxelShapes.union(
                        VoxelShapes.cuboid(0.1875, 0.0, 0.3125, 0.8125, 0.75, 0.625),
                        VoxelShapes.cuboid(0.25, 0.0625, 0.625, 0.75, 0.4375, 0.75)
                ),
                Direction.WEST, VoxelShapes.union(
                        VoxelShapes.cuboid(0.375, 0.0, 0.1875, 0.6875, 0.75, 0.8125),
                        VoxelShapes.cuboid(0.25, 0.0625, 0.25, 0.375, 0.4375, 0.75)
                )
        );
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, BackpackBlocks.BACKPACK_BLOCK_ENTITY, (entityWorld, pos, blockState, blockEntity) -> blockEntity.tick(entityWorld, pos, blockState, blockEntity));
    }
}
