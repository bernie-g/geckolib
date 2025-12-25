## GeckoLib v5.4-alpha-2

## Changes:
### Fixes:
* Fixed GeoArmorRenderer not rendering properly
* Fixed GeoArmorRenderer crashing when using renderlayers
* Fixed animations interpolating from previous animations that had already finished
* Fixed animations not transitioning back to base pose when finishing if transition time is present
* Fixed animations not rotating properly when animating bones that are rotated by default 

### API:
* GeoRenderState#addGeckolibData no longer accepts `null` values
* Re-added `#setTransitionTicks` to `AnimationController`
* Added `JsonUtil#jsonToVec3` direct helper method
* Begun building a new SPI-based .json deserialization pipeline, to allow for safer and more extensible model & animation loading
  * By default, GeckoLib will continue to use GSON to deserialize into intermediary unbaked objects
    * I am looking at making a GeckoLib addon that uses a faster library (possibly FastJson?) to speed up loading of assets, potentially substantially
  * This system is not fully implemented yet, but the majority of the code has been written and reviewed to ensure it meets current Bedrock geometry & animation specs (1.21.0 and 1.8.0 respectively)
  * This will in theory allow mod authors to create their own adapters in the event they want to do custom loading or handling, without sacrificing on safety 
* Removed `GeckoLibClient#getGeoModelForItem` as it was unused and unnecessary, use `RenderUtil#getGeckoLibItemRenderer` instead and get the model as needed
* `GeckoLibServices.Client` was extracted to `GeckoLibClientServices`, because java kept failing to compile for seemingly no reason
* Created `GeoBone#positionAndRender`, which implements the full render operation for a given bone

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