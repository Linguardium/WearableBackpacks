package dev.sapphic.wearablebackpacks.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

import static dev.sapphic.wearablebackpacks.initializers.BackpackScreenHandlers.TYPE;

public class BackpackMenu extends DynamicSizeContainerScreenHandler {

  private final BackpackContainer backpack;

  public BackpackMenu(final int containerId, final PlayerInventory inventory) {
    this(containerId, inventory, new WornBackpack());
  }
  
  public BackpackMenu(final int containerId, final PlayerInventory playerInventory, final BackpackContainer backpack) {
    super(TYPE, containerId, playerInventory,backpack, backpack.getRows(), backpack.getColumns());
    this.backpack = backpack;
    this.backpack.onOpen(playerInventory.player);

    addContainerSlots();
    addPlayerInventorySlots(playerInventory);
    addHotbarSlots(playerInventory);
  }

  @Override
  public void onClosed(final PlayerEntity player) {
    super.onClosed(player);
    this.backpack.onClose(player);
  }
  
  @Override
  public boolean canUse(final PlayerEntity player) {
    return this.backpack.canPlayerUse(player);
  }

  @Override
  protected Slot createContainerSlot(Inventory inventory, int inventorySlotId, int x, int y) {
    return new BackpackSlot(inventory, inventorySlotId, x, y);
  }
}
