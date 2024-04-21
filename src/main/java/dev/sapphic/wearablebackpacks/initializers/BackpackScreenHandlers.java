package dev.sapphic.wearablebackpacks.initializers;

import dev.sapphic.wearablebackpacks.inventory.BackpackMenu;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;

import static dev.sapphic.wearablebackpacks.Backpacks.BACKPACK_ID;

public class BackpackScreenHandlers {
    public static final ScreenHandlerType<BackpackMenu> TYPE = Registry.register(Registries.SCREEN_HANDLER, BACKPACK_ID,new ScreenHandlerType<>(BackpackMenu::new, FeatureSet.empty()));

    public static void init() { }

}
