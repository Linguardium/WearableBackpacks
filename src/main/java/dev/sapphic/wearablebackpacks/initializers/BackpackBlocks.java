package dev.sapphic.wearablebackpacks.initializers;

import com.google.common.collect.ImmutableSet;
import dev.sapphic.wearablebackpacks.Backpacks;
import dev.sapphic.wearablebackpacks.block.BackpackBlock;
import dev.sapphic.wearablebackpacks.block.entity.BackpackBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import static dev.sapphic.wearablebackpacks.Backpacks.BACKPACK_ID;

public class BackpackBlocks {
    public static final Identifier BACKPACK_LID = new Identifier(Backpacks.ID, "backpack_lid");


    public static final Block BACKPACK_BLOCK = Registry.register(Registries.BLOCK, BACKPACK_ID, new BackpackBlock(AbstractBlock.Settings.create().mapColor(MapColor.CLEAR).sounds(BlockSoundGroup.WOOL).nonOpaque().strength(0.5F, 0.5F)));
    public static final Block BACKPACK_LID_BLOCK = Registry.register(Registries.BLOCK, BACKPACK_LID, new Block(AbstractBlock.Settings.create().mapColor(MapColor.CLEAR).dropsNothing()));
    public static final BlockEntityType<BackpackBlockEntity> BACKPACK_BLOCK_ENTITY =  Registry.register(Registries.BLOCK_ENTITY_TYPE, BACKPACK_ID, new BlockEntityType<>(BackpackBlockEntity::new, ImmutableSet.of(BACKPACK_BLOCK), null));

    public static void init() { }
}
