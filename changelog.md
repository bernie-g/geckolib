## GeckoLib v5.5.5

### Functionality
- Added the `math.min_angle` Molang function
- Add support for Molang's truthy half-ternaries (`cond ? 5`)
- Fixed invalid value handling in `math.mod`, `math.sqrt`, `math.ln`, `%`, and `/`

### Bug Fixes
- Fixed ternary expressions not evaluating properly at runtime if using entity queries
- Fixed compound expressions not evaluating properly at runtime if using entity queries
- Fixed calculations eagerly resolving the right-hand side, even if the left hand side is true
- Fixed `GeoRenderState` being required on all renderstate classes - will now only throw if set up incorrectly (#894)