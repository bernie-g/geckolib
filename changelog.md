## GeckoLib v5.4.2

### Additions
* Added a bone name based equivalent of `RenderPassInfo#addBonePositionListener`
* Added transitive interface injections for Common, NeoForge, and Fabric
* Added a `TypeToken` alternative to classes for `DataTickets`, to allow for generic-typed DataTickets

### Bug Fixes
* Fixed a bug causing the first keyframe of animations to be skipped entirely (#807, #805)
* Fixed hold on last frame loop type not working (#806)