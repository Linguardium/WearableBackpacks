package dev.sapphic.wearablebackpacks.client.render;

import dev.sapphic.wearablebackpacks.Backpack;
import dev.sapphic.wearablebackpacks.BackpackWearer;
import dev.sapphic.wearablebackpacks.initializers.BackpackBlocks;
import dev.sapphic.wearablebackpacks.initializers.BackpackSlotIntegrations;
import dev.sapphic.wearablebackpacks.integration.SlotHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;

import static dev.sapphic.wearablebackpacks.client.initializers.BackpackRenderers.BACKPACK_RENDERER;
import static dev.sapphic.wearablebackpacks.client.render.BackpacksModelManager.getLidModel;

public class AlternateSlotIntegrationRenderer extends FeatureRenderer<LivingEntity, BipedEntityModel<LivingEntity>> {
    public AlternateSlotIntegrationRenderer(FeatureRendererContext<LivingEntity, BipedEntityModel<LivingEntity>> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack stack, VertexConsumerProvider vertexConsumers, int light, LivingEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        SlotHandler handler = BackpackSlotIntegrations.streamEnabledIntegrations().filter(slotHandler->slotHandler.isBackpackEquipped(entity)).findFirst().orElse(null);
        if (handler == null) return;

        ItemStack backpack = handler.getBackpack(entity);
        if (stack.isEmpty()) return;

        final BlockRenderManager manager = MinecraftClient.getInstance().getBlockRenderManager();
        final BlockModels models = manager.getModels();
        final BlockModelRenderer renderer = manager.getModelRenderer();
        final RenderLayer layer = RenderLayer.getArmorCutoutNoCull(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
        final VertexConsumer pipeline = ItemRenderer.getArmorGlintConsumer(vertexConsumers, layer, false, backpack.hasGlint());
        final BakedModel backpackModel = models.getModel(BackpackBlocks.BACKPACK_BLOCK.getDefaultState());
        final BakedModel lidModel = models.getModelManager().getModel(getLidModel(Direction.NORTH));

        final int color = Backpack.getColor(backpack);
        final float red = ((color >> 16) & 255) / 255.0F;
        final float green = ((color >> 8) & 255) / 255.0F;
        final float blue = (color & 255) / 255.0F;

        //noinspection NumericCastThatLosesPrecision
        final float pitch = 180.0F + (entity.isInSneakingPose() ? (float) Math.toDegrees(getContextModel().body.pitch) : 0.0F);

        stack.push();
        stack.translate(0.0, entity.isInSneakingPose() ? 0.2 : 0.0, 0.0);
        stack.multiply(new Quaternionf().rotateX(MathHelper.RADIANS_PER_DEGREE * pitch));
        stack.scale(0.8F, 0.8F, 0.8F);
        stack.translate(-0.5, -0.5 - (0.0625 * 4), -0.5 - (0.0625 * 5.5));
        renderer.render(stack.peek(), pipeline, null, backpackModel, red, green, blue, light, OverlayTexture.DEFAULT_UV);

        final double xPivot = 0.0;
        final double yPivot = 0.5625;
        final double zPivot = 1.0 - 0.3125;
        //noinspection CastToIncompatibleInterface
        final float lidDelta = ((BackpackWearer) entity).wearableBackpacks$getBackpackState().lidDelta(tickDelta);
        final Quaternionf rotation = new Quaternionf().rotationX(MathHelper.RADIANS_PER_DEGREE * 45.0F * lidDelta);

        stack.push();
        stack.translate(xPivot, yPivot, zPivot);
        stack.multiply(rotation);
        stack.translate(-xPivot, -yPivot, -zPivot);
        renderer.render(stack.peek(), pipeline, null, lidModel, red, green, blue, light, OverlayTexture.DEFAULT_UV);
        stack.pop();
        stack.pop();
    }
}
