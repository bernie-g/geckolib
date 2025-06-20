## GeckoLib v5.2.0

### BREAKING CHANGE - SORRY!

## Additions
* GeoItemRenderer has had its render object switched from ItemStack to GeoItemRenderer$RenderData.
  * This means that GeoItemRenderer now has access to a few extra context objects, including the player holding the item.

## Bug Fixes
* Fixed GeoItems not having the `ItemRenderContext` available during `AnimationTest` stage (#735)