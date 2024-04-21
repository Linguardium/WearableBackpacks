package dev.sapphic.wearablebackpacks.client.network;

import dev.sapphic.wearablebackpacks.BackpackWearer;
import dev.sapphic.wearablebackpacks.network.BackpackServerNetwork;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;

import static dev.sapphic.wearablebackpacks.network.BackpackServerNetwork.BACKPACK_STATE_CHANGED;
import static dev.sapphic.wearablebackpacks.network.BackpackServerNetwork.OPEN_OWN_BACKPACK;

public final class BackpackClientNetwork {

  private static final PacketByteBuf EMPTY_BUFFER = new PacketByteBuf(Unpooled.EMPTY_BUFFER);
  
  public static void tryOpenOwnBackpack() {
    ClientPlayNetworking.send(OPEN_OWN_BACKPACK, EMPTY_BUFFER);
  }
  
    public static void init() {
    ClientPlayNetworking.registerGlobalReceiver(
      BackpackServerNetwork.BACKPACK_UPDATED, (client, handler, buf, sender) -> {
        final int entityId = buf.readInt();
        final int openCount = buf.readInt();
        
        client.execute(() -> {
          if (client.world != null) {
            final Entity entity = client.world.getEntityById(entityId);
            if (entity instanceof LivingEntity) {
              BackpackWearer.getBackpackState((LivingEntity) entity).count(openCount);
            }
          }
        });
      });

      ClientPlayNetworking.registerGlobalReceiver(BACKPACK_STATE_CHANGED, (client, handler, buf, sender) -> {
          final int entityId = buf.readInt();
          final boolean opened = buf.readBoolean();
          client.execute(() -> {
              final Entity entity = client.world.getEntityById(entityId);
              if (!(entity instanceof BackpackWearer)) {
                  throw new IllegalStateException(String.valueOf(entity));
              }
              if (opened) {
                  ((BackpackWearer) entity).wearableBackpacks$getBackpackState().opened();
              } else {
                  ((BackpackWearer) entity).wearableBackpacks$getBackpackState().closed();
              }
          });
      });

  }
}
