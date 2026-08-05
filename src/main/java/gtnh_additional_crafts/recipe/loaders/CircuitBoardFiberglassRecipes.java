package gtnh_additional_crafts.recipe.loaders;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.metadata.PCBFactoryTierKey;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.PCBFactoryManager;
import gtnh_additional_crafts.MyMod;
import gtnh_additional_crafts.recipe.util.FluidLookup;
import gtnh_additional_crafts.recipe.util.MachineRecipes;

public final class CircuitBoardFiberglassRecipes {

    private CircuitBoardFiberglassRecipes() {}

    public static void register() {
        registerFiberglassBoardAlternateFoilRecipes();
        registerFiberglassBoardCopperPcbFactoryRecipe();
    }

    public static void registerFiberglassBoardAlternateFoilRecipes() {
        registerFiberglassBoardCopperFoilLuVRecipe();
        registerFiberglassBoardSilverFoilZpmRecipe();
        registerFiberglassBoardGoldFoilUvRecipe();
    }

    public static void registerFiberglassBoardCopperPcbFactoryRecipe() {
        // Budget sibling of GT's "More Advanced Circuit Board" PCB Factory loop (tier 3+, IC1/2/3 ->
        // Fiberglass_Advanced via Aluminium+EnergeticAlloy foils). No circuit gate here - Copper+AnnealedCopper
        // foils (the same cheap conductor pair GT already uses for base Plastic boards) already disambiguate
        // this recipe from GT's own -> plain Fiberglass board.
        // Shares the tier-3 EpoxidFiberReinforced/etc plastic slot, no new plastic tier registered.
        for (int tier = 3; tier <= PCBFactoryManager.mTiersOfPlastics; tier++) {
            Materials plasticMaterial = PCBFactoryManager.getPlasticMaterialFromTier(tier);
            ItemStack resinPlate = plasticMaterial == null ? null : plasticMaterial.getPlates(1);
            ItemStack annealedCopperFoil = GTOreDictUnificator
                .get(OrePrefixes.foil, Materials.AnnealedCopper, (long) (16 * (Math.sqrt(tier - 2))));
            ItemStack copperFoil = GTOreDictUnificator
                .get(OrePrefixes.foil, Materials.Copper, (long) (16 * (Math.sqrt(tier - 2))));
            FluidStack sulfuricAcid = FluidLookup
                .getFluidOrGas(Materials.SulfuricAcid, (long) (500 * (Math.sqrt(tier - 2))));
            FluidStack ironIIIChloride = FluidLookup
                .getFluidOrGas(Materials.IronIIIChloride, (long) (1000 * (Math.sqrt(tier - 2))));

            if (resinPlate == null || resinPlate.getItem() == null
                || annealedCopperFoil == null
                || annealedCopperFoil.getItem() == null
                || copperFoil == null
                || copperFoil.getItem() == null
                || sulfuricAcid == null
                || ironIIIChloride == null) {
                MyMod.logInfo(
                    "Skipped Copper Fiberglass Board PCB Factory recipe at tier " + tier
                        + ": required inputs unavailable.");
                continue;
            }

            int amountOfBoards = (int) Math.ceil(8 * (Math.sqrt(Math.pow(2, tier - 3))));
            List<ItemStack> boards = new ArrayList<>();
            for (int remaining = amountOfBoards; remaining > 64; remaining -= 64) {
                boards.add(ItemList.Circuit_Board_Fiberglass.get(64));
            }
            boards.add(ItemList.Circuit_Board_Fiberglass.get(amountOfBoards % 64 == 0 ? 64 : amountOfBoards % 64));

            GTValues.RA.stdBuilder()
                .itemInputs(resinPlate, annealedCopperFoil, copperFoil)
                .fluidInputs(sulfuricAcid, ironIIIChloride)
                .itemOutputs(boards.toArray(new ItemStack[0]))
                .duration((int) Math.ceil(600 / Math.sqrt(Math.pow(1.5, tier - 3.5))))
                .eut((int) GTValues.VP[tier] * 3 / 4)
                .metadata(PCBFactoryTierKey.INSTANCE, 1)
                .addTo(RecipeMaps.pcbFactoryRecipes);
        }

        MyMod.logInfo(
            "Registered PCB Factory budget recipe line: Copper+AnnealedCopper foils -> plain Fiberglass Circuit Board (tier 3+).");
    }

    private static void registerFiberglassBoardCopperFoilLuVRecipe() {
        ItemStack resinPlate = Materials.EpoxidFiberReinforced.getPlates(1);
        ItemStack copperFoil = GTOreDictUnificator.get(OrePrefixes.foil, Materials.Copper, 20L);
        FluidStack sulfuricAcid = FluidLookup.getFluidOrGas(Materials.SulfuricAcid, 1000L);
        ItemStack fiberglassBoard = ItemList.Circuit_Board_Fiberglass.get(1);

        // Copper is the real industry-standard PCB conductor (Aluminium-clad boards are a niche exception).
        // Cheaper/more abundant than Aluminium by LuV, but copper oxidizes faster than aluminium's native
        // oxide layer, so it needs a longer passivation step before it bonds cleanly to the resin.
        MachineRecipes.chemicalReactor()
            .itemInputs(resinPlate, copperFoil)
            .itemOutputs(fiberglassBoard)
            .fluidInputs(sulfuricAcid)
            .duration(50)
            .eut(1920)
            .register(
                "Skipped Copper Foil Fiberglass Board (LuV) recipe: required items or fluid unavailable.",
                "Registered Chemical Reactor recipe: Resin Plate + 20x Copper Foil + 1000L Sulfuric Acid -> 1x Fiberglass Circuit Board.");
    }

    private static void registerFiberglassBoardSilverFoilZpmRecipe() {
        ItemStack resinPlate = Materials.EpoxidFiberReinforced.getPlates(1);
        ItemStack silverFoil = GTOreDictUnificator.get(OrePrefixes.foil, Materials.Silver, 16L);
        FluidStack sulfuricAcid = FluidLookup.getFluidOrGas(Materials.SulfuricAcid, 500L);
        ItemStack fiberglassBoard = ItemList.Circuit_Board_Fiberglass.get(1);

        // Silver's higher conductivity than Copper/Aluminium means fewer foil sheets give equivalent trace
        // performance - real-world grounding for premium RF/hybrid-circuit silver traces. Net cheaper than
        // the Copper path despite silver's higher unit cost, once ZPM-tier materials are flowing.
        MachineRecipes.largeChemicalReactor()
            .itemInputs(resinPlate, silverFoil)
            .itemOutputs(fiberglassBoard)
            .fluidInputs(sulfuricAcid)
            .duration(16)
            .eut(7680)
            .register(
                "Skipped Silver Foil Fiberglass Board (ZPM) recipe: required items or fluid unavailable.",
                "Registered LCR recipe: Resin Plate + 16x Silver Foil + 500L Sulfuric Acid -> 1x Fiberglass Circuit Board.");
    }

    private static void registerFiberglassBoardGoldFoilUvRecipe() {
        ItemStack resinPlate = Materials.EpoxidFiberReinforced.getPlates(1);
        ItemStack goldFoil = GTOreDictUnificator.get(OrePrefixes.foil, Materials.Gold, 8L);
        FluidStack sulfuricAcid = FluidLookup.getFluidOrGas(Materials.SulfuricAcid, 1000L);
        ItemStack fiberglassBoards = ItemList.Circuit_Board_Fiberglass.get(2);

        // Gold-plated/gold-traced boards are real aerospace and satellite-grade PCB practice (ENIG-style
        // corrosion immunity for ultra-reliability electronics). UV-tier batch scale doubles board output
        // per craft.
        MachineRecipes.largeChemicalReactor()
            .itemInputs(resinPlate, goldFoil)
            .itemOutputs(fiberglassBoards)
            .fluidInputs(sulfuricAcid)
            .duration(8)
            .eut(30720)
            .register(
                "Skipped Gold Foil Fiberglass Board (UV) recipe: required items or fluid unavailable.",
                "Registered LCR recipe: Resin Plate + 8x Gold Foil + 1000L Sulfuric Acid -> 2x Fiberglass Circuit Board.");
    }

}
