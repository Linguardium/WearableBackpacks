package dev.sapphic.wearablebackpacks.initializers;

import dev.sapphic.wearablebackpacks.integration.EquipmentSlotIntegration;
import dev.sapphic.wearablebackpacks.integration.SlotHandler;
import dev.sapphic.wearablebackpacks.integration.TrinketsIntegration;
import net.fabricmc.loader.api.FabricLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class BackpackSlotIntegrations {
    private static final List<SlotHandler> integrations = new ArrayList<>();
    public static void init() {
        integrations.clear();
        List<Supplier<SlotHandler>> integrationSuppliers = new ArrayList<>();
        if (FabricLoader.getInstance().isModLoaded("trinkets")) {
            integrationSuppliers.add(TrinketsIntegration::new);
        }
        integrationSuppliers.forEach(s->integrations.add(s.get()));
        integrations.add(new EquipmentSlotIntegration());
    }
    public static Stream<SlotHandler> streamEnabledIntegrations() {
        return integrations.stream().filter(SlotHandler::isEnabled);
    }
}
