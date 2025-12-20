## GeckoLib v5.4-alpha-2

## Changes:
### API:
* GeoRenderState#addGeckolibData no longer accepts `null` values
* Re-added `#setTransitionTicks` to `AnimationController`
* Added `JsonUtil#jsonToVec3` direct helper method
* Rebuilt GeckoLib's json parsing library to be up to date with the current bedrock geometry spec (1.21.0), and moved the GSON instance to `Geometry`

### Internal:
* Moved GeckoLib's nullability annotations to the [JSpecify](https://jspecify.dev) spec
* Cleaned up more Javadocs
* Added a significant amount of additional documentation - specifically in package-info files and parameter type descriptions
* Renamed RawAnimation#additionalTicks to RawAnimation#waitTicks
* Removed `bind_pose_rotation` legacy support from the Bone format
* Removed the unused `AnimationVariables` class
* Changed the related-object generic type for `GeoObjectRenderer` from `E` to `O` to match the rest of the library
* Moved `BoneSnapshots`, `PerBoneRender`, and `RenderPassInfo` to the renderer base package
* Moved the various builtin `GeoRenderLayer` classes to a builtin subpackage
* Moved GeckoLib's GSON instance from `KeyFramesAdapter` to `GeckoLibResources`
* Renamed `KeyFramesAdapter` to `KeyFrameMarkersAdapter`
* Cleaned up and standardized nullability throughout the library
* Updated some outdated javadocs from pre-update