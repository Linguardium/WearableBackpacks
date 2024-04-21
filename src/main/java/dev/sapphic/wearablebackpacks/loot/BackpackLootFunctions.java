package dev.sapphic.wearablebackpacks.loot;

import dev.sapphic.wearablebackpacks.Backpacks;
import net.fabricmc.api.ModInitializer;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;


public final class BackpackLootFunctions {
  public static final LootFunctionType COPY_COLOR = Registry.register(Registries.LOOT_FUNCTION_TYPE, new Identifier(Backpacks.ID,"copy_color"), new LootFunctionType(CopyColorLootFunction.serializer()));
  public static void init() { }
}
