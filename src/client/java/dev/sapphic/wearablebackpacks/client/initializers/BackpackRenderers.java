package dev.sapphic.wearablebackpacks.client.initializers;

import dev.sapphic.wearablebackpacks.client.render.AlternateSlotIntegrationRenderer;
import dev.sapphic.wearablebackpacks.client.render.BackpacksArmorRenderer;
import dev.sapphic.wearablebackpacks.initializers.BackpackItems;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;

public class BackpackRenderers {
    public static final BackpacksArmorRenderer BACKPACK_RENDERER = new BackpacksArmorRenderer();
    public static void init() {
        ArmorRenderer.register((matrices, vertexConsumers, stack, entity, slot, light, contextModel) -> {},BackpackItems.BACKPACK_ITEM);
//        ArmorRenderer.register(BACKPACK_RENDERER, BackpackItems.BACKPACK_ITEM);
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
            if (entityRenderer.getModel() instanceof BipedEntityModel)
                registrationHelper.register(new AlternateSlotIntegrationRenderer((LivingEntityRenderer)entityRenderer));
        });
    }
}
