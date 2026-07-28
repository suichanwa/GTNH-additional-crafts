# Fusion Reactor Plasma Recipes (GTNH, all installed addons)

Extracted from every decompiled source file in `tmp_gt5check/` that adds recipes to GT5's `fusionRecipes` recipe map (vanilla `gregtech`, plus `gtPlusPlus` addon loaders). Only recipes whose **output** is a Plasma fluid are listed — each of these files also registers non-plasma fusion recipes (molten Platinum/Uranium/Naquadah/Curium/Californium/Flerovium/etc.), omitted here.

**31 total plasma-producing Fusion Reactor recipes found** across the whole modpack (17 vanilla + 14 from GT++ addon loaders). See "Why not ~200?" at the bottom — other machines (Plasma Forge, Transcendent Plasma Mixer) *consume* plasma rather than produce it, and many materials have a Plasma form defined without any recipe that actually makes it.

Units: fluid amounts in L (= mB). `NUGGETS` = 16L, `HALF_INGOTS` = 72L, `INGOTS` = 144L. `TICKS` = 1, `SECONDS` = 20 ticks.

Fusion Reactor Mk startup-energy / EU-t caps (vanilla):
- **Mk1**: ≤ 32,768 EU/t, ≤ 160,000,000 EU startup
- **Mk2**: ≤ 65,536 EU/t, ≤ 320,000,000 EU startup
- **Mk3**: ≤ 131,073 EU/t, ≤ 640,000,000 EU startup
- Higher (Mk4+/FT5) tiers needed for the UEV-class recipes below (GTNH-specific reactor tiers beyond vanilla Mk3).

Tier notation: `FTn` = minimum Mk tier by EU/t. A `+` means the startup-energy requirement pushes it one Mk tier higher than the EU/t alone would need; `++` means two tiers higher.

| Output Plasma | Inputs | Duration | EU/t | EU/op (duration × EU/t) | Startup Energy | Fusion Tier |
|---|---|---|---|---|---|---|
| Helium Plasma 125L | Deuterium 125L + Tritium 125L | 16 ticks (0.8s) | 4,096 | 65,536 | 40,000,000 | FT1 (cheap route) |
| Helium Plasma 125L | Deuterium 125L + Helium-3 125L | 16 ticks (0.8s) | 1,920 | 30,720 | 60,000,000 | FT1 (expensive route) |
| Sulfur Plasma 144L | Molten Aluminium 16L + Molten Lithium 16L | 32 ticks (1.6s) | 10,240 | 327,680 | 240,000,000 | FT1+ (cheap) |
| Nitrogen Plasma 125L | Molten Beryllium 16L + Deuterium 375L | 16 ticks (0.8s) | 16,384 | 262,144 | 180,000,000 | FT1+ (expensive) |
| Iron Plasma 144L | Molten Silicon 16L + Molten Magnesium 16L | 32 ticks (1.6s) | 7,680 | 245,760 | 360,000,000 | FT1++ (cheap) |
| Nickel Plasma 144L | Molten Potassium 16L + Fluorine 144L | 16 ticks (0.8s) | 30,720 | 491,520 | 480,000,000 | FT1++ (expensive) |
| Calcium Plasma 16L | Molten Magnesium 128L + Oxygen 128L | 128 ticks (6.4s) | 7,680 | 983,040 | 120,000,000 | FT1 |
| Zinc Plasma 72L | Molten Copper 72L + Tritium 250L | 16 ticks (0.8s) | 49,152 | 786,432 | 180,000,000 | FT2 (farmable) |
| Niobium Plasma 144L | Molten Cobalt 144L + Molten Silicon 144L | 16 ticks (0.8s) | 49,152 | 786,432 | 200,000,000 | FT2 |
| Silver Plasma 144L | Molten Gold 144L + Molten Arsenic 144L | 16 ticks (0.8s) | 49,152 | 786,432 | 350,000,000 | FT2+ |
| Tin Plasma 288L | Molten Silver 144L + Helium-3 375L | 16 ticks (0.8s) | 49,152 | 786,432 | 280,000,000 | FT2 |
| Bismuth Plasma 144L | Molten Tantalum 144L + **Zinc Plasma 72L** | 16 ticks (0.8s) | 98,304 | 1,572,864 | 350,000,000 | FT3 (farmable, needs Zinc Plasma as an input) |
| Radon Plasma 144L | Molten Iridium 144L + Fluorine 500L | 32 ticks (1.6s) | 98,304 | 3,145,728 | 450,000,000 | FT3 |
| Americium Plasma 144L | Molten Plutonium-241 144L + Hydrogen 2,000L | 64 ticks (3.2s) | 98,304 | 6,291,456 | 500,000,000 | FT3 |
| Plutonium-241 Plasma 576L | Molten Lutetium 576L + Molten Vanadium 576L | 4 ticks (0.2s) | 3,932,160 (UEV/2) | 15,728,640 | 6,000,000,000 | FT5 |
| Lead Plasma 576L | Molten Tellurium 576L + Molten Zinc 576L | 4 ticks (0.2s) | 3,932,160 (UEV/2) | 15,728,640 | 6,000,000,000 | FT5 |
| Thorium Plasma 576L | Molten Osmium 576L + Molten Silicon 576L | 4 ticks (0.2s) | 3,932,160 (UEV/2) | 15,728,640 | 6,000,000,000 | FT5 |

## GT++ addon Fusion Reactor plasma recipes

From `gtPlusPlus/xmod/gregtech/loaders/recipe/RecipeLoaderChemicalSkips.java` (`fusionReactorRecipes()`):

| Output Plasma | Inputs | Duration | EU/t | EU/op | Startup Energy | Tier |
|---|---|---|---|---|---|---|
| Neptunium Plasma 100L | Radon Plasma 100L + Nitrogen Plasma 100L | 600 ticks (30s) | 1,966,080 (UHV) | 1,179,648,000 | 1,000,000,000 | Mk4 |
| Fermium Plasma 100L | Americium Plasma 100L + Boron Plasma 100L | 600 ticks (30s) | 1,966,080 (UHV) | 1,179,648,000 | 1,000,000,000 | Mk4 |
| Neptunium Plasma 576L | Xenon Plasma 576L + Molten Yttrium 576L | 32 ticks (1.6s) | 7,864,320 (UEV) | 251,658,240 | 6,000,000,000 | Mk5 |
| Fermium Plasma 576L | Force Plasma 576L + Molten Rubidium 576L | 32 ticks (1.6s) | 7,864,320 (UEV) | 251,658,240 | 6,000,000,000 | Mk5 |

From `gtPlusPlus/xmod/gregtech/loaders/recipe/RecipeLoaderGTNH.java` (both under the file's own "MK4" comment block):

| Output Plasma | Inputs | Duration | EU/t | EU/op | Startup Energy | Tier |
|---|---|---|---|---|---|---|
| Bromine Plasma 144L | Molten Manganese 144L + Neon Gas 500L | 32 ticks (1.6s) | 196,608 | 6,291,456 | 1,000,000,000 | Mk4 |
| Technetium Plasma 288L | Fluorine Gas 1,000L + Molten Selenium 144L | 64 ticks (3.2s) | 196,608 | 12,582,912 | 800,000,000 | Mk4 |

From `gtPlusPlus/xmod/gregtech/loaders/recipe/RecipeLoaderNuclear.java` (`fusionChainRecipes()`) — a linear Mk1→Mk3 chain where each plasma feeds the next recipe:

| Output Plasma | Inputs | Duration | EU/t | EU/op | Startup Energy | Tier |
|---|---|---|---|---|---|---|
| Neon Plasma 1,000L | Boron Plasma 144L + Calcium Plasma 16L | 64 ticks (3.2s) | 30,720 (LuV) | 1,966,080 | 100,000,000 | Mk1 |
| Force Plasma 1,000L | Neon Plasma 144L + Arcanite Fluid 2L | 32 ticks (1.6s) | 30,720 (LuV) | 983,040 | 100,000,000 | Mk1 |
| Krypton Plasma 144L | Niobium Plasma 144L + Zinc Plasma 144L | 32 ticks (1.6s) | 122,880 (ZPM) | 3,932,160 | 300,000,000 | Mk2 |
| Astral Titanium Plasma 1,000L | Krypton Plasma 144L + Force Plasma 1,000L | 32 ticks (1.6s) | 122,880 (ZPM) | 3,932,160 | 300,000,000 | Mk2 |
| Runite Plasma 1,000L | Astral Titanium Plasma 144L + Titansteel Fluid 2L | 32 ticks (1.6s) | 122,880 (ZPM) | 3,932,160 | 300,000,000 | Mk2 |
| Xenon Plasma 144L | Curium Fluid 144L + Americium Plasma 144L | 16 ticks (0.8s) | 491,520 (UV) | 7,864,320 | 500,000,000 | Mk3 |
| Advanced Nitinol Plasma 1,000L | Xenon Plasma 144L + Runite Plasma 1,000L | 16 ticks (0.8s) | 491,520 (UV) | 7,864,320 | 500,000,000 | Mk3 |
| Celestial Tungsten Plasma 1,000L | Advanced Nitinol Plasma 72L + Molten Tartarite 2L | 8 ticks (0.4s) | 491,520 (UV) | 3,932,160 | 500,000,000 | Mk3 |

## Notes

- The two Helium Plasma recipes are alternates for the same output — the game picks whichever inputs are available (Helium-3 route trades Tritium for Helium-3 at a higher EU/t but lower startup cost).
- Bismuth Plasma is the only plasma recipe that consumes another plasma (Zinc Plasma) as an input — build a Zinc Plasma supply chain first.
- All three FT5 recipes use identical duration/EU/t/startup — only the input/output materials differ.
- L/tick throughput for any of these = output amount ÷ duration in ticks (e.g. Helium Plasma: 125L ÷ 16 ticks = 7.8125 L/t per Fusion Reactor running that recipe — note this is a different recipe than the 125L/8-tick MK2 recipe referenced earlier in conversation, which is a GT++ or modpack-specific fusion recipe, not this vanilla GT5 one).
- The Nuclear fusion chain is a genuine multi-stage progression: Neon → Force → (with Krypton) Astral Titanium → Runite → (with Curium/Americium) Xenon → Advanced Nitinol → Celestial Tungsten. Each stage consumes the previous stage's plasma, so building this chain end-to-end means running 5+ Fusion Reactors of increasing Mk tier simultaneously.

## Why not ~200?

Searched the whole decompiled tree (`gregtech`, `gtPlusPlus`, `bartworks`, `goodgenerator`, `tectech`, `kekztech`, `kubatech`, etc.) for every `.addTo(fusionRecipes)` call. Total is 31 plasma-output recipes, not ~200. Reasons the expected number might be much higher:

- **`RecipeGenPlasma.java`** auto-generates a Plasma Turbine fuel-value recipe *and* a Vacuum Freezer cooling recipe (Plasma Cell → normal Cell) for **every material that has a Plasma form defined** — this is likely 100+ materials, but these recipes *consume* an existing plasma cell, they don't produce one via fusion. A material can have `.getPlasma()` exist without any Fusion Reactor recipe that actually makes it.
- **Plasma Forge** (`plasmaForgeRecipes`) and **Transcendent Plasma Mixer** (its own recipe file) both *consume* multiple plasma types as ingredients to forge endgame materials (Excited DTCC/DTPC, Avaritia-tier stuff) — they don't produce plasma either.
- If NEI shows ~200 when you search "Plasma" as a catalyst/ingredient across all recipe types (Fusion Reactor + Plasma Forge + Transcendent Plasma Mixer + Plasma Turbine fuel + Vacuum Freezer cooling), that total is plausible — it's just spread across 5 different machines, only one of which (Fusion Reactor) actually creates plasma from non-plasma inputs.
