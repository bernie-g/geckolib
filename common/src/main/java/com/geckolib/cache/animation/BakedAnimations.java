package com.geckolib.cache.animation;

import org.jspecify.annotations.Nullable;

import java.util.Map;

/// Container object that holds a deserialized map of [Animations][Animation]
///
/// Kept as a unique object so that it can be registered as a [deserializer][com.google.gson.JsonDeserializer] for [Gson][com.google.gson.Gson]
public record BakedAnimations(Map<String, Animation> animations) {
	/// Gets an [Animation] by its name, if present
	public @Nullable Animation getAnimation(String name) {
		return this.animations.get(name);
	}
}
