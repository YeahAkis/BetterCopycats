# BetterCopycats

Place flowers and other plants on Create copycats and Copycats+ blocks, based on the skin they are wearing.

## What it does

- A plant can be placed on a copycat whenever its skin could normally hold it. A copycat wearing moss, grass or dirt holds flowers, a copycat wearing stone does not.
- Copycats+ slabs use the half you actually see from above. A double slab uses its top half and a single top slab uses its own skin, so stone below with moss on top works and the other way around does not.
- Plants on a bottom slab sit half a block lower, so they stand on the slab instead of floating above it. The hitbox moves with them and you can target them from the side.
- If the skin underneath is changed to something that cannot hold the plant, the plant breaks and drops.
- Maps show the skin of a copycat instead of the plain copycat color, on vanilla maps and on Xaero's Minimap and World Map.

## Requirements

- Minecraft 1.20.1 with Forge 47 or newer, NeoForge 47.1 or newer (the Forge version runs on both), or Fabric with Fabric API
- Create 6.0.8 or newer, or Create Fabric 6.0.8 or newer
- Copycats+ is optional. Without it only the copycats from Create itself are supported, and bottom slabs do not exist

The mod has to be installed on both the server and the client.

## Config

The config lives in `config/bettercopycats-common.toml`.

| Option | Default | What it does |
| --- | --- | --- |
| `lowerPlantsOnBottomSlabs` | `true` | Place plants half a block lower on bottom slabs. When off, plants cannot be placed on bottom slabs at all |
| `allowSaplings` | `false` | Allow saplings on copycats. Growing trees replace the block below with dirt, which would destroy the copycat |
| `allowCrops` | `false` | Allow crops, stems and nether wart on copycats. They grow at the slowest rate because the block below is not farmland |
| `plantBlacklist` | `[]` | Block ids of plants that can never be placed on copycats |
| `mapSupport` | `true` | Show copycat skins on vanilla maps (server side) and on the Xaero's maps (client side) |

## Compatibility

- Tested with Sodium and Flywheel, and on a dedicated server with two players
- The Xaero's support shows copycat skins on both the minimap and the world map. It is written against Xaero's Minimap 26.6.0 and World Map 1.47.0 and hooks internal methods, so a Xaero's update can silently stop it from working
- Maps only pick up the new colors when Xaero's re-reads a chunk, and regions rebuilt from the world save are not covered

## Known limitations

- Plants that have a collision shape, like pitcher crops and lily pads, are not lowered
- Mushrooms are not supported
- The hitbox of a lowered two block tall plant is the vanilla full block shape moved down
- Changing a skin does not refresh the map until the chunk is read again

## License

GPL-3.0-only, see `LICENSE`.
