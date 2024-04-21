package dev.sapphic.wearablebackpacks.client.initializers;

import dev.sapphic.wearablebackpacks.Backpack;
import dev.sapphic.wearablebackpacks.initializers.BackpackBlocks;
import dev.sapphic.wearablebackpacks.initializers.BackpackItems;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;

public class BackpackColorProviders {
    public static void init() {
        ColorProviderRegistry.BLOCK.register((state, world, pos, tint) -> Backpack.getColor(world, pos), BackpackBlocks.BACKPACK_BLOCK);
        ColorProviderRegistry.ITEM.register((stack, tint) -> Backpack.getColor(stack), BackpackItems.BACKPACK_ITEM);

    }
}
