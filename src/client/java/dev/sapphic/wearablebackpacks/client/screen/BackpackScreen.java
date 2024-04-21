package dev.sapphic.wearablebackpacks.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.sapphic.wearablebackpacks.Backpacks;
import dev.sapphic.wearablebackpacks.inventory.BackpackMenu;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public final class BackpackScreen extends HandledScreen<BackpackMenu> {
  private static final Identifier TEXTURE = new Identifier(Backpacks.ID, "textures/gui/container/backpack.png");
  
  public BackpackScreen(final BackpackMenu menu, final PlayerInventory inventory, final Text name) {
    super(menu, inventory, name);
    this.backgroundWidth = (7 * 2) + (Math.max(menu.getColumns(), 9) * 18);
    this.backgroundHeight = 114 + (menu.getRows() * 18);
    this.playerInventoryTitleY = this.backgroundHeight - 94;
  }
  
//  private static void drawTexture(
//    final MatrixStack stack, final int x, final int y, final int z, final int w, final int h,
//    final float u, final float v, final int rw, final int rh, final int tw, final int th
//  ) {
//    DrawableHelperAccessor.invokeDrawTexture(stack, x, x + w, y, y + h, z, rw, rh, u, v, tw, th);
//  }

  @Override
  public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    this.renderBackground(context);
    super.render(context, mouseX, mouseY, delta);
    this.drawMouseoverTooltip(context, mouseX, mouseY);
  }
  
  @Override
  protected void drawBackground(DrawContext context, final float tickDelta, final int mx, final int my) {
//        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
//        RenderSystem.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
    //noinspection ConstantConditions
//        this.client.getTextureManager().bindTexture(TEXTURE);

//    RenderSystem.setShaderTexture(0, TEXTURE);
    final int bgW = this.x + this.backgroundWidth;
    final int bgH = this.y + this.backgroundHeight;
    final int fillW = this.backgroundWidth - (4 * 2);
    final int fillH = this.backgroundHeight - (4 * 2);
    
    context.drawTexture(TEXTURE, this.x, this.y, 0, 18.0F, 0.0F, 4, 10, 64, 64); // TOP LEFT
    context.drawTexture(TEXTURE, bgW - 4, this.y, 0, 18.0F + 14.0F, 0.0F, 4, 10, 64, 64); // TOP RIGHT
    context.drawTexture(TEXTURE, this.x, bgH - 4, 0, 18.0F, 14.0F, 4, 10, 64, 64); //BOTTOM LEFT
    context.drawTexture(TEXTURE, bgW - 4, bgH - 4, 0, 18.0F + 14.0F, 14.0F, 4, 10, 64, 64); // BOTTOM RIGHT
    context.drawTexture(TEXTURE, this.x + 4, this.y, fillW, 4, 18.0F + 4.0F, 0.0F, 10, 4, 64, 64); // TOP
    context.drawTexture(TEXTURE, this.x, this.y + 4, 4, fillH, 18.0F, 4.0F, 4, 10, 64, 64); // LEFT
    context.drawTexture(TEXTURE, this.x + 4, bgH - 4, fillW, 4, 18.0F + 4.0F, 14.0F, 10, 4, 64, 64); // BOTTOM
    context.drawTexture(TEXTURE, bgW - 4, this.y + 4, 4, fillH, 18.0F + 14.0F, 4.0F, 4, 10, 64, 64); // RIGHT
    context.drawTexture(TEXTURE, this.x + 4, this.y + 4, fillW, fillH, 22.0F, 4.0F, 10, 10, 64, 64); // FILL
    
    for (final Slot slot : this.getScreenHandler().slots) {
      final int x = (this.x + slot.x) - 1;
      final int y = (this.y + slot.y) - 1;
      context.drawTexture(TEXTURE, x, y, 0, 0.0F, 0.0F, 18, 18, 64, 64);
    }
  }
}
