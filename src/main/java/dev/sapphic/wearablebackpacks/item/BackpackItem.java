package dev.sapphic.wearablebackpacks.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.sapphic.wearablebackpacks.Backpack;
import dev.sapphic.wearablebackpacks.Backpacks;
import dev.sapphic.wearablebackpacks.advancement.BackpackCriteria;
import dev.sapphic.wearablebackpacks.event.BackpackEntityEvents;
import dev.sapphic.wearablebackpacks.inventory.WornBackpack;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.world.World;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public final class BackpackItem extends BlockItem implements Equipment, DyeableItem {
  private static final UUID armorModifierUUID = UUID.nameUUIDFromBytes("Wearable Backpack Armor Modifier".getBytes(StandardCharsets.UTF_8));
  public BackpackItem(final Block block, final Settings settings) {
    super(block,settings);
    ServerEntityEvents.EQUIPMENT_CHANGE.register(BackpackEntityEvents.equipItemEvent(this,this::onEquip));
    UseBlockCallback.EVENT.register(BackpackEntityEvents.sneakUsageEvent(this,this::useOnBlockWhileSneaking));
    DispenserBlock.registerBehavior(this, new FallibleItemDispenserBehavior() {
                  @Override
                  protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                    this.setSuccess(ArmorItem.dispenseArmor(pointer, stack));
                    return stack;
                  }
                });
  }

  @Override
  public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
    if (slot.equals(EquipmentSlot.CHEST)) {
      return ImmutableMultimap.of(
              EntityAttributes.GENERIC_ARMOR,
                new EntityAttributeModifier(armorModifierUUID, "Armor modifier", Backpack.getDefense(), EntityAttributeModifier.Operation.ADDITION),
              EntityAttributes.GENERIC_ARMOR_TOUGHNESS,
                new EntityAttributeModifier(armorModifierUUID, "Armor toughness", Backpack.getToughness(), EntityAttributeModifier.Operation.ADDITION)
      );
    }
    return ImmutableMultimap.of();
  }

  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
    player.openHandledScreen(WornBackpack.of(player,player.getStackInHand(hand)));
    return TypedActionResult.success(player.getStackInHand(hand),world.isClient());
  }

  @Override
  public ActionResult useOnBlock(ItemUsageContext context) {
    PlayerEntity player = context.getPlayer();
    if (player != null) {
      player.openHandledScreen(WornBackpack.of(player, context.getStack()));
      return ActionResult.success(player.getWorld().isClient());
    }
    return ActionResult.PASS;
  }

  @Override
  public boolean canBeNested() {
    return false;
  }

  @Override
  public boolean canRepair(ItemStack stack, ItemStack ingredient) {
    return ingredient.isOf(Items.LEATHER);
  }

  @Override
  public void inventoryTick(
    final ItemStack backpack, final World world, final Entity entity, final int slot, final boolean selected
  ) {
    super.inventoryTick(backpack, world, entity, slot, selected);
    if (!(entity instanceof PlayerEntity player)) {
      return;
    }
    if (!world.isClient()) {
      //TODO: move this to inventory changed criterion
      if (this.hasColor(backpack)) {
        BackpackCriteria.DYED.trigger((ServerPlayerEntity) entity);
      }
    }
  }


  public void onEquip(LivingEntity entity, ItemStack stack) {
    if (entity instanceof ServerPlayerEntity player && stack.isOf(this)) BackpackCriteria.EQUIPPED.trigger(player);
  }

  public ActionResult useOnBlockWhileSneaking(ItemUsageContext context) {
    return this.place(new ItemPlacementContext(context));
  }

  @Override
  public EquipmentSlot getSlotType() {
    return Backpacks.config.enableChestArmorEquip ?  EquipmentSlot.CHEST : EquipmentSlot.MAINHAND;
  }

  @Override
  public SoundEvent getEquipSound() {
    return SoundEvents.ITEM_ARMOR_EQUIP_LEATHER;
  }
}
