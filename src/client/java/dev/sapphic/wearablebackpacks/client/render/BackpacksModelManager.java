package dev.sapphic.wearablebackpacks.client.render;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import dev.sapphic.wearablebackpacks.block.BackpackBlock;
import dev.sapphic.wearablebackpacks.client.mixin.ModelLoaderAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.Arrays;
import java.util.Locale;
import java.util.function.Function;

import static dev.sapphic.wearablebackpacks.initializers.BackpackBlocks.BACKPACK_LID;

public class BackpacksModelManager {

    public static ModelIdentifier getLidModel(final Direction facing) {
        return LID_MODELS.getOrDefault(facing, ModelLoader.MISSING_ID);
    }
    public static final ImmutableMap<Direction, ModelIdentifier> LID_MODELS = Arrays.stream(Direction.values())
            .filter(Direction.Type.HORIZONTAL).collect(Maps.toImmutableEnumMap(Function.<Direction>identity(), facing ->
                    new ModelIdentifier(BACKPACK_LID, String.format(Locale.ROOT, "facing=%s", facing.asString()))
            ));

    private static void addLidStateDefinitions() {
        ModelLoaderAccessor.setStaticDefinitions(
                ImmutableMap.<Identifier, StateManager<Block, BlockState>>builder()
                        .putAll(ModelLoaderAccessor.getStaticDefinitions())
                        .put(BACKPACK_LID, new StateManager.Builder<Block, BlockState>(Blocks.AIR)
                                .add(Properties.HORIZONTAL_FACING).build(Block::getDefaultState, BlockState::new)
                        ).build()
        );
    }
    public static void init() {
        addLidStateDefinitions();
    }


}
