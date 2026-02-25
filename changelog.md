## GeckoLib v5.4.4

* Port to 26.1

### Changes
#### API
* Refactored the base packages of GeckoLib from `software.bernie.geckolib` to `com.geckolib`
* Added `GeoLocator` - A object implementation for locators. They are only used for listening to render positions
  * Added `BakedGeoModel#getLocator` and `BakedGeoModel#locators`
  * Added `GeoBone#locators`
  * Added `RenderPassUtil#addLocatorPositionListener`
  * Extracted `GeoBone#updateBonePositionListeners` to `RenderUtil#providePositionsToListeners`
* Modified GeckoLib's resource loading to allow for completely custom resource formats
  * Gives root-level access to `Resource` instances. Write an adapter to read resource files from any format you want!
  * Changed `GeckoLibLoader` from an SPI to a registerable interface
  * Added `GeckoLibUtil#addResourceLoader`
  * Removed `GeckoLibUtil#addCustomBakedModelFactory`
  * Converted `BakedGeoModel` to a non-record, extendable class, for potential third-party adapters
  * Converted `Animation` to a non-record, extendable class, for potential third-party adapters
  * Converted `ModelProperties` to a non-record, extendable class, for potential third-party adapters
  * Converted `MathParser` back to an instantiable object to allow for extensible implementations, added a deduplication schema to it, and performed a general cleanup
  * Improved the deduplication schema for `MathParser`, further reducing memory consumption
  * Removed `BakedAnimationsAdapter` and `KeyFrameMarkersAdapter`
  * Removed the legacy loading objects:
    * `Bone`
    * `Cube`
    * `FaceUV`
    * `LocatorClass`
    * `LocatorValue`
    * `MinecraftGeometry`
    * `Model`
    * `PolyMesh`
    * `PolysUnion`
    * `TextureMesh`
    * `UVFaces`
    * `UVUnion`
    * `BoneStructure`
    * `GeometryTree`
    * Moved `ModelProperties` to `geckolib.cache.model`
    * Moved `BakedAnimations` to `geckolib.cache.animation`
* Cleaned up `EasingType`
  * Added a zero-parameter factory constructor to `EasingType` and in `GeckoLibUtil`
  * Added `EasingState#interpolate`
  * Added `EasingState#getFirstEasingArg`
  * Added `EasingType#modifyKeyframes`
  * Changed `EasingType#register` to return a generic type instead of `EasingType`
  * Removed `EasingType#easeIn`
  * Removed `EasingType#lerpWithOverride`
  * Removed `EasingType#fromJson`
* Added `BakedAnimationCache#size` and `BakedModelCache#size`
* Added the `.json` resource path to `BakedGeoModel`'s `ModelProperties`
* Removed `GeoReplacedEntity#getReplacingEntityType` as it was no longer used
* Converted all Javadocs to Markdown format
* Folded internal implementation methods in `BakedGeoModel` and `GeoBone` for API visibility
* Removed the class parameter for TokenType-based `DataTickets`, and properly typed the builtin GeckoLib tickets
* Removed `MiscUtil#WORLD_TO_MODEL_SIZE`
* Removed `MiscUtil#MODEL_TO_WORLD_SIZE`
* Added `JsonUtil#worldToModelUnits` and `JsonUtil#modelToWorldUnits`
* Removed the dual-type generics for renderstates from:
  * `DirectionalProjectileRenderer`
  * `DyeableGeoArmorRenderer`
  * `GeoArmorRenderer`
  * `GeoBlockRenderer`
  * `GeoEntityRenderer`
  * `GeoReplacedEntityRenderer`
* Moved `ModelFormatVersion` to `com.geckolib.loading.definition.geometry.object`
* Renamed `Rotation` to `UvFaceRotation` and moved to `com.geckolib.loading.definition.geometry.object`
* Removed `headBone` and its associated constructor in `DefaultedEntityGeoModel` since it's no longer used
* Added direct-object constructors for `DefaultedBlockGeoModel`, `DefaultedEntityGeoModel`, and `DefaultedItemGeoModel`
* Removed `GeckoLibPlatform#getGameDir` as it is no longer used

#### Other
* Added `1.16.0` and `1.19.30` to the known geometry model definitions
* Improved resource loading performance
* Added an interface injection for `BlockEntityRenderState` -> `GeoRenderState`
* GeckoLib will no longer crash when unable to find a model; and will instead render a missing texture cube

### Bug Fixes
* Fixed `RenderUtil#transformToBone` running in reverse order
* Fixed `AnimationController` having the wrong InternalApi annotations
* Fixed top-level bones not always providing the correct position to `BonePositionListeners` 
* Fixed GeckoLib model bones not rendering in the correct order when nested under parent bones
* Fixed GeckoLib model loading not respecting `mirror` and `inflate` values properly
* Reduced backface z-fighting on 0-depth cubes. Stop making your cubes 0-thickness!