package dev.sapphic.wearablebackpacks.initializers;

import dev.sapphic.wearablebackpacks.item.BackpackItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static dev.sapphic.wearablebackpacks.Backpacks.BACKPACK_ID;
import static dev.sapphic.wearablebackpacks.initializers.BackpackBlocks.BACKPACK_BLOCK;

public class BackpackItems {
    public static Item BACKPACK_ITEM = Registry.register(Registries.ITEM, BACKPACK_ID, new BackpackItem(BACKPACK_BLOCK, new Item.Settings()));
    public static void init() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(BACKPACK_ITEM);
        });

    }
}
