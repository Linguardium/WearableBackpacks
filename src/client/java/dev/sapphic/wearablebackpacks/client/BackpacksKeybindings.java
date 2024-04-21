package dev.sapphic.wearablebackpacks.client;

import dev.sapphic.wearablebackpacks.Backpacks;
import dev.sapphic.wearablebackpacks.client.network.BackpackClientNetwork;
import dev.sapphic.wearablebackpacks.item.BackpackItem;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import org.lwjgl.glfw.GLFW;

public class BackpacksKeybindings {
    private static final KeyBinding BACKPACK_KEY_BINDING =
            new KeyBinding("key." + Backpacks.ID + ".backpack", GLFW.GLFW_KEY_B, "key.categories.inventory");

    public static void init() {
        KeyBindingHelper.registerKeyBinding(BACKPACK_KEY_BINDING);
        ClientTickEvents.END_CLIENT_TICK.register(BackpacksKeybindings::pollBackpackKey);

    }

    private static void pollBackpackKey(final MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if ((player != null) && player.getWorld() != null) {
            while (BACKPACK_KEY_BINDING.wasPressed()) {
                final ItemStack stack = player.getEquippedStack(EquipmentSlot.CHEST);
                if (stack.getItem() instanceof BackpackItem) {
                    final float pitch = (player.getWorld().getRandom().nextFloat() * 0.1F) + 0.9F;
                    player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.5F, pitch);
                    BackpackClientNetwork.tryOpenOwnBackpack();
                }
            }
        }
    }
}
