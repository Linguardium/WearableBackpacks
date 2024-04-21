package dev.sapphic.wearablebackpacks.integration;

import dev.emi.trinkets.api.*;
import dev.sapphic.wearablebackpacks.Backpacks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;

import static dev.sapphic.wearablebackpacks.Backpacks.IS_BACKPACK_ITEM;

public class TrinketsIntegration implements SlotHandler {
    public TrinketsIntegration() {}
    /**
     * Attempts to equip the backpack on player's trinket slot
     */

    public boolean equipBackpack(ItemStack backpack, LivingEntity entity) {
        return TrinketItem.equipItem(entity,backpack);
    }

    /***
     * Gets the backpack from the trinket slot
     * @param entity The Living Entity to check.
     * @return The backpack ItemStack
     */
    public ItemStack getBackpack(LivingEntity entity) {
        return TrinketsApi.getTrinketComponent(entity).flatMap(component ->
                        component
                        .getEquipped(IS_BACKPACK_ITEM)
                        .stream()
                        .findFirst())
                .map(Pair::getRight)
                .orElse(ItemStack.EMPTY);
    }
    /**
     * Whether the backpack is equipped on the Trinket Slot.
     * @param entity The Living Entity to check.
     */
    public boolean isBackpackEquipped(LivingEntity entity) {
        return TrinketsApi
                .getTrinketComponent(entity)
                .map(cmp->cmp.isEquipped(IS_BACKPACK_ITEM))
                .orElse(false);
    }

    @Override
    public boolean isSpecificBackpackEquipped(LivingEntity entity, ItemStack backpack) {
        return TrinketsApi.getTrinketComponent(entity).map(cmp->cmp.isEquipped(stack->stack==backpack)).orElse(false);
    }

    @Override
    public boolean isEnabled() {
        return Backpacks.config.allowBackpackOnTrinketSlot;
    }
}
