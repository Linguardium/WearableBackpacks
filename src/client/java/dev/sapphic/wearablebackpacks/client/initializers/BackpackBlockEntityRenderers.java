package dev.sapphic.wearablebackpacks.client.initializers;

import dev.sapphic.wearablebackpacks.client.render.BackpackBlockRenderer;
import dev.sapphic.wearablebackpacks.initializers.BackpackBlocks;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class BackpackBlockEntityRenderers {
    public static void init() {
        BlockEntityRendererFactories.register(BackpackBlocks.BACKPACK_BLOCK_ENTITY, BackpackBlockRenderer::new);
    }
}
