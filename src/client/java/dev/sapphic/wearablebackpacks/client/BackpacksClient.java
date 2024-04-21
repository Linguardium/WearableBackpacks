package dev.sapphic.wearablebackpacks.client;

import dev.sapphic.wearablebackpacks.client.initializers.BackpackBlockEntityRenderers;
import dev.sapphic.wearablebackpacks.client.initializers.BackpackColorProviders;
import dev.sapphic.wearablebackpacks.client.initializers.BackpackRenderers;
import dev.sapphic.wearablebackpacks.client.initializers.BackpackScreens;
import dev.sapphic.wearablebackpacks.client.network.BackpackClientNetwork;
import dev.sapphic.wearablebackpacks.client.render.BackpacksModelManager;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.network.PacketByteBuf;

public final class BackpacksClient implements ClientModInitializer {
  private static final PacketByteBuf EMPTY_PACKET_BUFFER = new PacketByteBuf(Unpooled.EMPTY_BUFFER);
  

  @Override
  public void onInitializeClient() {
    //addLidStateDefinitions();
    BackpackScreens.init();
    BackpackBlockEntityRenderers.init();
    BackpackColorProviders.init();
    BackpacksKeybindings.init();
    BackpacksModelManager.init();
    BackpackClientNetwork.init();
    BackpackRenderers.init();
  }
}
