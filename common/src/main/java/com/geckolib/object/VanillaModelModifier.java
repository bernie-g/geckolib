package com.geckolib.object;

import com.geckolib.constant.DataTickets;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.base.GeoRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.client.model.Model;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/// Render callback interface for modifying vanilla models at render time
///
/// Allows for modifying vanilla models during a render submission, where it isn't otherwise possible
public interface VanillaModelModifier<S, T extends Model<? super S>> {
    /// Make any modifications necessary to the model for render
    ///
    /// Called during [Model#setupAnim]
    void setupAnim(T model);

    /// Reset modifications made in [#setupAnim]
    ///
    /// Called at the end of [Model#renderToBuffer(PoseStack, VertexConsumer, int, int, int)]
    void postRenderReset(T model);

    /// Shorthand helper method for creating a [VanillaModelModifier] instance that doesn't require a reset after rendering
    static <S extends GeoRenderState, T extends Model<? super S>> VanillaModelModifier<S, T> ofSetupOnly(Consumer<T> setup) {
        return of(setup, _ -> {});
    }

    /// Shorthand helper method for creating a [VanillaModelModifier] instance
    static <S extends GeoRenderState, T extends Model<? super S>> VanillaModelModifier<S, T> of(Consumer<T> setup, Consumer<T> reset) {
        return new VanillaModelModifier<>() {
            @Override
            public void setupAnim(T model) {
                setup.accept(model);
            }

            @Override
            public void postRenderReset(T model) {
                reset.accept(model);
            }
        };
    }

    /// Add a [VanillaModelModifier] to a [GeoRenderState] via its [DataTickets#VANILLA_MODEL_MODIFIERS] [DataTicket]
    static <S extends GeoRenderState, T extends Model<? super S>> void addModifierToState(S renderState, T model, VanillaModelModifier<S, T> modifier) {
        final Map<Model<?>, List<VanillaModelModifier<?, ?>>> modifiers;

        if (renderState.hasGeckolibData(DataTickets.VANILLA_MODEL_MODIFIERS)) {
            //noinspection unchecked,rawtypes
            modifiers = (Map)renderState.getGeckolibData(DataTickets.VANILLA_MODEL_MODIFIERS);
        }
        else {
            renderState.addGeckolibData(DataTickets.VANILLA_MODEL_MODIFIERS, modifiers = new Reference2ObjectArrayMap<>());
        }

        //noinspection DataFlowIssue
        modifiers.computeIfAbsent(model, _ -> new ObjectArrayList<>()).add(modifier);
    }
}
