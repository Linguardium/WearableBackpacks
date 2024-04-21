package dev.sapphic.wearablebackpacks;

import net.minecraft.entity.LivingEntity;

public interface BackpackWearer {
  static BackpackLid getBackpackState(final LivingEntity entity) {
    //noinspection CastToIncompatibleInterface
    return ((BackpackWearer) entity).wearableBackpacks$getBackpackState();
  }
  
  BackpackLid wearableBackpacks$getBackpackState();
}
