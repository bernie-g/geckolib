package com.geckolib.model;

import com.geckolib.animatable.GeoAnimatable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Objects;

/// [DefaultedGeoModel] specific to [Items][net.minecraft.world.item.Item]
///
/// Using this class pre-sorts provided asset paths into the "item" subdirectory
public class DefaultedItemGeoModel<T extends GeoAnimatable> extends DefaultedGeoModel<T> {
	/// Create a new instance of this model class with no asset subpath
	///
	/// The resultant asset id will be named from your [BlockEntityType]'s registered id<br/>
	public <B extends Item & GeoAnimatable> DefaultedItemGeoModel(Item item) {
		this(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item)));
	}
	/// Create a new instance of this model class
	///
	/// The asset path should be the truncated relative path from the base folder
	///
	/// E.G.
	/// <pre>
	/// `new Identifier("myMod", "armor/obsidian")`</pre>
	public DefaultedItemGeoModel(Identifier assetSubpath) {
		super(assetSubpath);
	}

	/// Returns the subtype string for this type of model
	///
	/// This allows for sorting of asset files into neat subdirectories for clean management
	@Override
	protected String subtype() {
		return "item";
	}

	/// Changes the constructor-defined model path for this model to an alternate
	///
	/// This is useful if your animatable shares a model path with another animatable that differs in path to the texture and animations for this model
	@Override
	public DefaultedItemGeoModel<T> withAltModel(Identifier altPath) {
		return (DefaultedItemGeoModel<T>)super.withAltModel(altPath);
	}

	/// Changes the constructor-defined animations path for this model to an alternate
	///
	/// This is useful if your animatable shares an animations path with another animatable that differs in path to the model and texture for this model
	@Override
	public DefaultedItemGeoModel<T> withAltAnimations(Identifier altPath) {
		return (DefaultedItemGeoModel<T>)super.withAltAnimations(altPath);
	}

	/// Changes the constructor-defined texture path for this model to an alternate
	///
	/// This is useful if your animatable shares a texture path with another animatable that differs in path to the model and animations for this model
	@Override
	public DefaultedItemGeoModel<T> withAltTexture(Identifier altPath) {
		return (DefaultedItemGeoModel<T>)super.withAltTexture(altPath);
	}
}
