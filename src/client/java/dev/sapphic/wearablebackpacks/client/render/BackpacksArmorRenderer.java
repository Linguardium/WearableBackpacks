package dev.sapphic.wearablebackpacks.client.render;

import dev.sapphic.wearablebackpacks.Backpack;
import dev.sapphic.wearablebackpacks.BackpackWearer;
import dev.sapphic.wearablebackpacks.initializers.BackpackBlocks;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;

import java.util.List;

import static dev.sapphic.wearablebackpacks.client.render.BackpacksModelManager.getLidModel;

public class BackpacksArmorRenderer implements ArmorRenderer {


    @Override
    public void render(MatrixStack stack, VertexConsumerProvider vertexConsumers, ItemStack backpack, LivingEntity entity, EquipmentSlot slot, int light, BipedEntityModel<LivingEntity> model) {

    }

    public static void renderBackpackQuad(final MatrixStack.Entry entry, final VertexConsumer pipeline, final float red, final float green, final float blue, final List<BakedQuad> quads, final int light, final int overlay) {
//        final VertexConsumer delegate = ((DualVertexConsumerAccessor) pipeline).getSecond();
        for (final BakedQuad quad : quads) {
            if (quad.hasColor()) {
                final float quadRed = MathHelper.clamp(red, 0.0F, 1.0F);
                final float quadGreen = MathHelper.clamp(green, 0.0F, 1.0F);
                final float quadBlue = MathHelper.clamp(blue, 0.0F, 1.0F);
                pipeline.quad(entry, quad, quadRed, quadGreen, quadBlue, light, overlay);
            } else {
//       delegate.quad(entry, quad, 1.0F, 1.0F, 1.0F, light, overlay);
            }
        }
    }
}
