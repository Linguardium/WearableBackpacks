package dev.sapphic.wearablebackpacks.event;

import dev.sapphic.wearablebackpacks.BackpackWearer;
import dev.sapphic.wearablebackpacks.inventory.WornBackpack;
import dev.sapphic.wearablebackpacks.item.BackpackItem;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

import javax.swing.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public final class BackpackEntityEvents {
  public static final double MIN_REQUIRED_DISTANCE = 1.8;
  public static final double ANGLE_BOUNDS = 110;

  public static UseBlockCallback sneakUsageEvent(BackpackItem item, Function<ItemUsageContext, ActionResult> callback) {
    return (PlayerEntity player, World world, Hand hand, BlockHitResult hitResult)->{
      if (player.isSneaking()) {
        if (player.getStackInHand(hand).isOf(item)) {
           return callback.apply(new ItemUsageContext(world, player, hand, player.getStackInHand(hand), hitResult));
        }else if(player.getMainHandStack().isEmpty() && player.getOffHandStack().isEmpty() && player.getEquippedStack(item.getSlotType()).isOf(item)) {
          return callback.apply(new ItemUsageContext(world, player, hand, player.getEquippedStack(item.getSlotType()), hitResult));
        }
      }
      return ActionResult.PASS;
    };
  }

  public static ServerEntityEvents.EquipmentChange equipItemEvent(BackpackItem item, BiConsumer<LivingEntity, ItemStack> callback) {
    return (LivingEntity livingEntity, EquipmentSlot equipmentSlot, ItemStack previousStack, ItemStack currentStack)->{
      if (currentStack.isOf(item)) callback.accept(livingEntity,currentStack);
    };
  }

  public static ActionResult tryPlaceBackpack(final PlayerEntity player, final World world, final Hand hand, final BlockHitResult hit) {
    if (player.isSneaking() && player.getMainHandStack().isEmpty() && player.getOffHandStack().isEmpty()) {
      final ItemStack stack = player.getEquippedStack(EquipmentSlot.CHEST);
      if (stack.getItem() instanceof BackpackItem) {
        final ItemPlacementContext context = new ItemPlacementContext(player, hand, stack, hit);
        if (((BackpackItem) stack.getItem()).place(context).isAccepted()) {
          if (!player.getAbilities().creativeMode) {
            stack.decrement(1);
          }
          return ActionResult.SUCCESS;
        }
      }
    }
    return ActionResult.PASS;
  }
  
  public static ActionResult tryOpenBackpack(
          final PlayerEntity player, final World world, final Hand hand, final Entity entity,
          final EntityHitResult hit
  ) {
    if (!(entity instanceof LivingEntity wearer)) {
      return ActionResult.PASS;
    }
    final ItemStack stack = (wearer).getEquippedStack(EquipmentSlot.CHEST);
    if ((stack.getItem() instanceof BackpackItem) && canOpenBackpack(player, wearer)) {
      if (world.isClient()) {
        final float pitch = (player.getWorld().getRandom().nextFloat() * 0.1F) + 0.9F;
        player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.5F, pitch);
      } else {
        player.openHandledScreen(WornBackpack.of(wearer, stack));
        BackpackWearer.getBackpackState(wearer).opened();
      }
      return ActionResult.SUCCESS;
    }
    return ActionResult.PASS;
  }
  
  private static boolean canOpenBackpack(final PlayerEntity player, final LivingEntity entity) {
    if (player.distanceTo(entity) <= MIN_REQUIRED_DISTANCE) {
      final double theta = StrictMath.atan2(entity.getZ() - player.getZ(), entity.getX() - player.getX());
      //noinspection OverlyComplexArithmeticExpression
      final double angle = ((((Math.toDegrees(theta) - entity.bodyYaw - 90) % 360) + 540) % 360) - 180;
      return Math.abs(angle) < (ANGLE_BOUNDS / 2);
    }
    return false;
  }
}
