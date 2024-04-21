package dev.sapphic.wearablebackpacks.client.initializers;

import dev.sapphic.wearablebackpacks.client.screen.BackpackScreen;
import dev.sapphic.wearablebackpacks.initializers.BackpackScreenHandlers;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class BackpackScreens {
    public static void init() {
        HandledScreens.register(BackpackScreenHandlers.TYPE, BackpackScreen::new);
    }
}
