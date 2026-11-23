## GeckoLib v5.5.3

### Bug Fixes
- Fixed ClientUtil#getCurrentTick returning bad values after a world has existed for a while
- Fixed query.time_of_day being wrong after the first day
- Fixed RenderUtil#translateMatrix incorrectly translating
- Fixed `RenderUtil#fixInvertedFlatCube` not being quite correct
- Fixed GeckoLib crashing if a cube is missing the optional uv property on models
- Fixed GeckoLib's deduplication on ID'd stacks not applying when drag-splitting a stack
- Add caching to missing animation error messages to remove log/console spam
- Fixed an issue caused by bones over-transitioning when mid-transition
- Fixed some other animation transition issues

### Additions
- Add query shorthand support for the non-query query types (`v.`, `c.`, `t.`)