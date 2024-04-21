package dev.sapphic.wearablebackpacks.integration;

import com.google.common.collect.Iterators;
import dev.sapphic.wearablebackpacks.Backpacks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Equipment;
import net.minecraft.item.ItemStack;

import java.util.Arrays;

public class EquipmentSlotIntegration implements SlotHandler {
    @Override
    public boolean equipBackpack(ItemStack backpack, LivingEntity entity) {
        if (backpack.getItem() instanceof Equipment equipment) {
            if (entity.getEquippedStack(equipment.getSlotType()).isEmpty()) {
                entity.equipStack(equipment.getSlotType(), backpack);
                return true;
            }
        }
        return false;
    }

    @Override
    public ItemStack getBackpack(LivingEntity entity) {
        return Arrays.stream(EquipmentSlot.values())
                .map(entity::getEquippedStack)
                .filter(Backpacks.IS_BACKPACK_ITEM)
                .findFirst()
                .orElse(ItemStack.EMPTY);
    }

    @Override
    public boolean isBackpackEquipped(LivingEntity entity) {
        return Iterators.any(entity.getItemsEquipped().iterator(), stack->stack.isIn(Backpacks.WEARABLE_BACKPACK_ITEM));
    }

    @Override
    public boolean isSpecificBackpackEquipped(LivingEntity entity, ItemStack backpack) {
        return Iterators.any(entity.getItemsEquipped().iterator(), stack->stack == backpack);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
