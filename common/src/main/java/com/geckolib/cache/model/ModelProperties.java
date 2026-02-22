package com.geckolib.cache.model;

import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import com.geckolib.util.JsonUtil;

/// Container class for model property information
@SuppressWarnings("ClassCanBeRecord")
public class ModelProperties {
	protected final Identifier resourcePath;
	protected final String identifier;
	protected final @Nullable Float visibleBoundsWidth;
	protected final @Nullable Float visibleBoundsHeight;
	protected final @Nullable Vec3 visibleBoundsOffset;
	protected final int textureWidth;
	protected final int textureHeight;

	public ModelProperties(Identifier resourcePath, String identifier, @Nullable Float visibleBoundsWidth, @Nullable Float visibleBoundsHeight, @Nullable Vec3 visibleBoundsOffset, int textureWidth, int textureHeight) {
		this.resourcePath = resourcePath;
		this.identifier = identifier;
		this.visibleBoundsWidth = visibleBoundsWidth;
		this.visibleBoundsHeight = visibleBoundsHeight;
		this.visibleBoundsOffset = visibleBoundsOffset;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
	}

	/// @return The resource asset file path (relative to assets root) of this model
	public Identifier resourcePath() {
		return this.resourcePath;
	}

	/// @return The model identifier for this model (usually `geometry.unknown`)
	public String identifier() {
		return this.identifier;
	}

	/// @return The rendered bounds width (in [world units][JsonUtil#modelToWorldUnits(double)]) offset from the model origin, or null if not computed
	public @Nullable Float visibleBoundsWidth() {
		return this.visibleBoundsWidth;
	}

	/// @return The rendered bounds height (in [world units][JsonUtil#modelToWorldUnits(double)]) of the model, or null if not computed
	public @Nullable Float visibleBoundsHeight() {
		return this.visibleBoundsHeight;
	}

	/// @return The rendered bounds (in [world units][JsonUtil#modelToWorldUnits(double)]) offset from the model origin
	public @Nullable Vec3 visibleBoundsOffset() {
		return this.visibleBoundsOffset;
	}

	/// @return The modelled width of the texture for this model (in pixels)
	public int textureWidth() {
		return this.textureWidth;
	}

	/// @return The modelled height of the texture for this model (in pixels)
	public int textureHeight() {
		return this.textureHeight;
	}
}