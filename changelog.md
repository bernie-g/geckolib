## GeckoLib v5.0-alpha2

## NOTE
* Still looking into alternate solutions for per-bone render handling since Mojang has made the previous Dynamic renderers not practical.

## Bug Fixes
* Fixed GeoObjectRenderer not storing packed light (#712)
* Fixed GeoEntityRenderer not allowing for generically-extendable RenderStates
* Fixed some incorrect javadocs in `AutoGlowingGeoLayer`
* Fixed GeoArmorRenderer not accounting for glowing or invisibility DataTickets
* Reloading textures causes odd issues with Glowing & animated textures

## Internal Changes
* AutoGlowingGeoLayer no longer removes sections of the base texture, allowing for selective dynamic emissivity
* AutoGlowingGeoLayer no longer disables shader compatibility
* Animated textures & glowmasks are no longer dependent on each other. You can have a non-animated glowmask on an animated texture, etc.

## New Stuff
* Added `RenderUtil#getEmissiveResource` helper method
* AutoGlowingGeoLayer now has a `shouldRespectWorldLighting` method, allowing you to choose between absolute emissivity, and emissivity that shades in conjunction with world lighting

## Removals
* GeckoLib emissive textures no longer support mcmeta creation. Use a glowmask image instead (It's way easier anyway)