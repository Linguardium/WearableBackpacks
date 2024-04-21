package dev.sapphic.wearablebackpacks.integration;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.stream.Stream;

public interface SlotHandler {

    default boolean equipBackpack(ItemStack backpack, LivingEntity entity) { return false; }

    default ItemStack getBackpack(LivingEntity entity) { return ItemStack.EMPTY; }

    default boolean isBackpackEquipped(LivingEntity entity) { return false; }

    default boolean isEnabled() {
        return true;
    }

    default boolean isSpecificBackpackEquipped(LivingEntity entity, ItemStack backpack) { return false; }

    SlotHandler MISSING = new SlotHandler() { };

}