package dev.sapphic.wearablebackpacks;

import dev.sapphic.wearablebackpacks.event.BackpackEntityEvents;
import dev.sapphic.wearablebackpacks.initializers.*;
import dev.sapphic.wearablebackpacks.loot.BackpackLootFunctions;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.function.Predicate;
import java.util.logging.Logger;

public final class Backpacks implements ModInitializer {
  public static final String ID = "wearablebackpacks";
  public static final Identifier BACKPACK_ID = new Identifier(ID, "backpack");
  public static final Logger LOGGER = Logger.getLogger(ID);
  public static BackpackOptions config;
  public static final TagKey<Item> WEARABLE_BACKPACK_ITEM = TagKey.of(RegistryKeys.ITEM, BACKPACK_ID);
  public static final Predicate<ItemStack> IS_BACKPACK_ITEM = stack -> stack.isIn(WEARABLE_BACKPACK_ITEM);
  @Override
  public void onInitialize() {
    AutoConfig.register(BackpackOptions.class, Toml4jConfigSerializer::new);
    config = AutoConfig.getConfigHolder(BackpackOptions.class).getConfig();

    BackpackItems.init();
    BackpackBlocks.init();
    BackpackScreenHandlers.init();
    BackpackRecipes.init();
    BackpackLootFunctions.init();
    BackpackSlotIntegrations.init();

    LOGGER.info("Wearable Backpacks initialized!");
  }

}
