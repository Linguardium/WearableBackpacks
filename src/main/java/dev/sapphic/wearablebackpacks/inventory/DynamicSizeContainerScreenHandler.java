package dev.sapphic.wearablebackpacks.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;

import static dev.sapphic.wearablebackpacks.initializers.BackpackScreenHandlers.TYPE;

public class DynamicSizeContainerScreenHandler extends ScreenHandler {

    private final int rows;
    private final int columns;
    protected final Inventory inventory;
    protected final PlayerInventory playerInventory;

    public DynamicSizeContainerScreenHandler(ScreenHandlerType<?> type, final int containerId, PlayerInventory playerInventory, Inventory containerInventory, int rows, int columns) {
        super(type, containerId);
        this.rows = rows;
        this.columns = columns;
        this.inventory = containerInventory;
        this.playerInventory = playerInventory;
    }

    protected void addSlots(SlotSupplier slotSupplier, Inventory inventory, int slotOffset, int xOffset, int yOffset, int rows, int cols) {
        for (int row = 0; row < rows; ++row) {
            for (int column = 0; column < cols; ++column) {
                final int slotId = slotOffset + column + (row * cols);
                final int x = xOffset + (getMargin()*2) + (column * getSlotWidth());
                final int y = yOffset + (row * getSlotHeight());
                this.addSlot(slotSupplier.createSlot(inventory,slotId,x,y));
            }
        }

    }

    protected void addContainerSlots() {
        addSlots(
                this::createContainerSlot,
                this.inventory,
                0,
                getRowXOffset(this.columns),
                getTitleHeight(),
                this.rows,
                this.columns
        );
    }


    protected void addPlayerInventorySlots(PlayerInventory inventory) {
        addSlots(
                this::createPlayerSlot,
                inventory,
                this.getHotbarCols()*this.getHotbarRows(),
                getRowXOffset(getPlayerInventoryCols()),
                getPlayerSlotsYOffset()+103,
                getPlayerInventoryRows(),
                getPlayerInventoryCols()
                );
    }
    protected void addHotbarSlots(PlayerInventory inventory) {
        addSlots(
            this::createHotbarSlot,
            inventory,
            0,
            getRowXOffset(getHotbarCols()),
            getPlayerSlotsYOffset()+103+(getPlayerInventoryRows()*getSlotHeight())+getPadding(),
            getHotbarRows(),
            getHotbarCols()
        );
    }

    protected int getPlayerSlotsYOffset() {
        return (this.rows-(getPlayerInventoryRows() + getHotbarRows())) * getSlotHeight();
    }

    protected int getRowXOffset(int slotsPerRow) {
        if (getMaxCols() > slotsPerRow) {
            return (getSlotWidth() * (getMaxCols() - slotsPerRow)) / 2;
        }
        return 0;
    }

    public int getRows() {
        return this.rows;
    }

    public int getColumns() {
        return this.columns;
    }

    protected int getSlotCount() {
        return getPlayerInventorySlotCount() + getHotbarSlotCount() + getContainerSlotCount();
    }
    protected int getPlayerInventorySlotCount() {
        return getPlayerInventoryCols() * getPlayerInventoryRows();
    }
    protected int getHotbarSlotCount() {
        return getHotbarCols() * getHotbarRows();
    }
    protected int getContainerSlotCount() {
        return this.columns * this.rows;
    }
    protected int getHotbarRows() {
        return 1;
    }
    protected int getHotbarCols() {
        return 9;
    }
    protected int getPlayerInventoryRows() {
        return 3;
    }
    protected int getPlayerInventoryCols() {
        return 9;
    }
    protected int getSlotWidth() {
        return 18;
    }
    protected int getSlotHeight() {
        return 18;
    }
    protected int getMaxCols() {
        return Math.max(this.columns, Math.max(getHotbarCols(), getPlayerInventoryCols()));
    }
    protected int getPadding() {
        return 4;
    }
    protected int getMargin() {
        return 4;
    }
    protected int getTitleHeight() {
        return 18;
    }

    protected Slot createContainerSlot(Inventory inventory, int inventorySlotId, int x, int y) {
        return new Slot(inventory, inventorySlotId, x, y);
    }

    protected Slot createPlayerSlot(Inventory inventory, int inventorySlotId, int x, int y) {
        return new Slot(inventory, inventorySlotId, x, y);
    }

    protected Slot createHotbarSlot(Inventory inventory, int inventorySlotId, int x, int y) {
        return new Slot(inventory,  inventorySlotId, x, y);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotId) {

        if (slotId < 0 || slotId > getSlotCount()) return ItemStack.EMPTY;

        final Slot slot = this.slots.get(slotId);
        final ItemStack stack = slot.getStack();
        if (stack.isEmpty()) return ItemStack.EMPTY;
        ItemStack original = stack.copy();

        if (isContainerSlot(slotId)) {
            if (!this.insertItem(stack, getContainerSlotCount(), this.getSlotCount(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (!this.insertItem(stack, 0, getContainerSlotCount(), false)) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.setStack(ItemStack.EMPTY);
        } else {
            slot.markDirty();
        }
        slot.onTakeItem(player, stack);

        return original;
    }

    private boolean isContainerSlot(int slotId) {
        return slotId < getContainerSlotCount();
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @FunctionalInterface
    public interface SlotSupplier {
        Slot createSlot(Inventory inventory, int slotId, int x, int y);
    }
}
