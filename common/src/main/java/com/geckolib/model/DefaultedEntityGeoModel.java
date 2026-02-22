package com.geckolib.model;

import com.geckolib.animatable.GeoAnimatable;
import net.minecraft.resources.Identifier;

/// [DefaultedGeoModel] specific to [Entities][net.minecraft.world.entity.Entity]
///
/// Using this class pre-sorts provided asset paths into the "entity" subdirectory
///
/// Additionally it can automatically handle head-turning if the entity has a "head" bone
public class DefaultedEntityGeoModel<T extends GeoAnimatable> extends DefaultedGeoModel<T> {
	/// Create a new instance of this model class
	///
	/// The asset path should be the truncated relative path from the base folder
	///
	/// E.G.
	/// <pre>`new Identifier("myMod", "animals/red_fish")`</pre>
	public DefaultedEntityGeoModel(Identifier assetSubpath) {
		super(assetSubpath);
	}

	/// Returns the subtype string for this type of model
	///
	/// This allows for sorting of asset files into neat subdirectories for clean management.
	@Override
	protected String subtype() {
		return "entity";
	}

	/// Changes the constructor-defined model path for this model to an alternate
	///
	/// This is useful if your animatable shares a model path with another animatable that differs in path to the texture and animations for this model
	@Override
	public DefaultedEntityGeoModel<T> withAltModel(Identifier altPath) {
		return (DefaultedEntityGeoModel<T>)super.withAltModel(altPath);
	}

	/// Changes the constructor-defined animations path for this model to an alternate
	///
	/// This is useful if your animatable shares an animations path with another animatable that differs in path to the model and texture for this model
	@Override
	public DefaultedEntityGeoModel<T> withAltAnimations(Identifier altPath) {
		return (DefaultedEntityGeoModel<T>)super.withAltAnimations(altPath);
	}

	/// Changes the constructor-defined texture path for this model to an alternate
	///
	/// This is useful if your animatable shares a texture path with another animatable that differs in path to the model and animations for this model
	@Override
	public DefaultedEntityGeoModel<T> withAltTexture(Identifier altPath) {
		return (DefaultedEntityGeoModel<T>)super.withAltTexture(altPath);
	}
}
