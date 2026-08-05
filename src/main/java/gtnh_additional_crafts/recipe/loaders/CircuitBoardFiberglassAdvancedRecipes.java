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

public final class CircuitBoardFiberglassAdvancedRecipes {

    private CircuitBoardFiberglassAdvancedRecipes() {}

    public static void register() {
        registerFiberglassAdvancedVibrantAlloyPcbFactoryRecipe();
        registerFiberglassAdvancedEnergeticSilverIronChlorideRecipe();
        registerFiberglassAdvancedEnergeticSilverSodiumPersulfateRecipe();
    }

    public static void registerFiberglassAdvancedVibrantAlloyPcbFactoryRecipe() {
        // Sibling of GT's "More Advanced Circuit Board" PCB Factory loop (tier 3+, IC1 ->
        // Fiberglass_Advanced via Aluminium+EnergeticAlloy foils). Same Aluminium foil, same fluids, same
        // duration formula - only the coating foil changes: a flat 6x Vibrant Alloy instead of the
        // tier-scaled EnergeticAlloy amount. No circuit gate - the Vibrant Alloy foil already disambiguates
        // this recipe from GT's own.
        for (int tier = 3; tier <= PCBFactoryManager.mTiersOfPlastics; tier++) {
            Materials plasticMaterial = PCBFactoryManager.getPlasticMaterialFromTier(tier);
            ItemStack resinPlate = plasticMaterial == null ? null : plasticMaterial.getPlates(1);
            ItemStack aluminiumFoil = GTOreDictUnificator
                .get(OrePrefixes.foil, Materials.Aluminium, (long) (16 * (Math.sqrt(tier - 2))));
            ItemStack vibrantAlloyFoil = GTOreDictUnificator.get(OrePrefixes.foil, Materials.VibrantAlloy, 6L);
            FluidStack sulfuricAcid = FluidLookup
                .getFluidOrGas(Materials.SulfuricAcid, (long) (500 * (Math.sqrt(tier - 2))));
            FluidStack ironIIIChloride = FluidLookup
                .getFluidOrGas(Materials.IronIIIChloride, (long) (1000 * (Math.sqrt(tier - 2))));

            if (resinPlate == null || resinPlate.getItem() == null
                || aluminiumFoil == null
                || aluminiumFoil.getItem() == null
                || vibrantAlloyFoil == null
                || vibrantAlloyFoil.getItem() == null
                || sulfuricAcid == null
                || ironIIIChloride == null) {
                MyMod.logInfo(
                    "Skipped Vibrant Alloy Fiberglass Advanced Board PCB Factory recipe at tier " + tier
                        + ": required inputs unavailable.");
                continue;
            }

            int amountOfBoards = (int) Math.ceil(8 * (Math.sqrt(Math.pow(2, tier - 3))));
            List<ItemStack> boards = new ArrayList<>();
            for (int remaining = amountOfBoards; remaining > 64; remaining -= 64) {
                boards.add(ItemList.Circuit_Board_Fiberglass_Advanced.get(64));
            }
            boards.add(
                ItemList.Circuit_Board_Fiberglass_Advanced.get(amountOfBoards % 64 == 0 ? 64 : amountOfBoards % 64));

            GTValues.RA.stdBuilder()
                .itemInputs(resinPlate, aluminiumFoil, vibrantAlloyFoil)
                .fluidInputs(sulfuricAcid, ironIIIChloride)
                .itemOutputs(boards.toArray(new ItemStack[0]))
                .duration((int) Math.ceil(600 / Math.sqrt(Math.pow(1.5, tier - 3.5))))
                .eut((int) GTValues.VP[tier] * 3 / 4)
                .metadata(PCBFactoryTierKey.INSTANCE, 1)
                .addTo(RecipeMaps.pcbFactoryRecipes);
        }

        MyMod.logInfo(
            "Registered PCB Factory recipe line: Aluminium + 6x Vibrant Alloy foils -> Fiberglass Advanced Circuit Board (tier 3+).");
    }

    public static void registerFiberglassAdvancedEnergeticSilverIronChlorideRecipe() {
        // Sibling of GT's "More Advanced Circuit Board" PCB Factory loop (tier 3+, IC1 -> Fiberglass_Advanced
        // via Aluminium+EnergeticAlloy foils). Same Aluminium foil, same duration/fluids formula - only the
        // coating foil changes: a flat 16x Energetic Silver instead of the tier-scaled EnergeticAlloy amount.
        // No circuit gate - the Energetic Silver foil already disambiguates this recipe from GT's own.
        for (int tier = 3; tier <= PCBFactoryManager.mTiersOfPlastics; tier++) {
            Materials plasticMaterial = PCBFactoryManager.getPlasticMaterialFromTier(tier);
            ItemStack resinPlate = plasticMaterial == null ? null : plasticMaterial.getPlates(1);
            ItemStack aluminiumFoil = GTOreDictUnificator
                .get(OrePrefixes.foil, Materials.Aluminium, (long) (16 * (Math.sqrt(tier - 2))));
            ItemStack energeticSilverFoil = GTOreDictUnificator.get(OrePrefixes.foil, Materials.EnergeticSilver, 16L);
            FluidStack sulfuricAcid = FluidLookup
                .getFluidOrGas(Materials.SulfuricAcid, (long) (500 * (Math.sqrt(tier - 2))));
            FluidStack ironIIIChloride = FluidLookup
                .getFluidOrGas(Materials.IronIIIChloride, (long) (1000 * (Math.sqrt(tier - 2))));

            if (resinPlate == null || resinPlate.getItem() == null
                || aluminiumFoil == null
                || aluminiumFoil.getItem() == null
                || energeticSilverFoil == null
                || energeticSilverFoil.getItem() == null
                || sulfuricAcid == null
                || ironIIIChloride == null) {
                MyMod.logInfo(
                    "Skipped Energetic Silver (IronIIIChloride) Fiberglass Advanced Board PCB Factory recipe at tier "
                        + tier
                        + ": required inputs unavailable.");
                continue;
            }

            int amountOfBoards = (int) Math.ceil(8 * (Math.sqrt(Math.pow(2, tier - 3))));
            List<ItemStack> boards = new ArrayList<>();
            for (int remaining = amountOfBoards; remaining > 64; remaining -= 64) {
                boards.add(ItemList.Circuit_Board_Fiberglass_Advanced.get(64));
            }
            boards.add(
                ItemList.Circuit_Board_Fiberglass_Advanced.get(amountOfBoards % 64 == 0 ? 64 : amountOfBoards % 64));

            GTValues.RA.stdBuilder()
                .itemInputs(resinPlate, aluminiumFoil, energeticSilverFoil)
                .fluidInputs(sulfuricAcid, ironIIIChloride)
                .itemOutputs(boards.toArray(new ItemStack[0]))
                .duration((int) Math.ceil(600 / Math.sqrt(Math.pow(1.5, tier - 3.5))))
                .eut((int) GTValues.VP[tier] * 3 / 4)
                .metadata(PCBFactoryTierKey.INSTANCE, 1)
                .addTo(RecipeMaps.pcbFactoryRecipes);
        }

        MyMod.logInfo(
            "Registered PCB Factory recipe line: Aluminium + 16x Energetic Silver foils + Sulfuric Acid + IronIIIChloride -> Fiberglass Advanced Circuit Board (tier 3+).");
    }

    public static void registerFiberglassAdvancedEnergeticSilverSodiumPersulfateRecipe() {
        // Same as the IronIIIChloride Energetic Silver line above, but the etchant is swapped for Sodium
        // Persulfate - a real-world alternative PCB copper etchant used in industry (cleaner, regenerable,
        // no iron sludge byproduct unlike ferric/iron-III-chloride etching). Fluid alone disambiguates it
        // from the IronIIIChloride sibling.
        for (int tier = 3; tier <= PCBFactoryManager.mTiersOfPlastics; tier++) {
            Materials plasticMaterial = PCBFactoryManager.getPlasticMaterialFromTier(tier);
            ItemStack resinPlate = plasticMaterial == null ? null : plasticMaterial.getPlates(1);
            ItemStack aluminiumFoil = GTOreDictUnificator
                .get(OrePrefixes.foil, Materials.Aluminium, (long) (16 * (Math.sqrt(tier - 2))));
            ItemStack energeticSilverFoil = GTOreDictUnificator.get(OrePrefixes.foil, Materials.EnergeticSilver, 16L);
            FluidStack sulfuricAcid = FluidLookup
                .getFluidOrGas(Materials.SulfuricAcid, (long) (500 * (Math.sqrt(tier - 2))));
            FluidStack sodiumPersulfate = FluidLookup
                .getFluidOrGas(Materials.SodiumPersulfate, (long) (1000 * (Math.sqrt(tier - 2))));

            if (resinPlate == null || resinPlate.getItem() == null
                || aluminiumFoil == null
                || aluminiumFoil.getItem() == null
                || energeticSilverFoil == null
                || energeticSilverFoil.getItem() == null
                || sulfuricAcid == null
                || sodiumPersulfate == null) {
                MyMod.logInfo(
                    "Skipped Energetic Silver (SodiumPersulfate) Fiberglass Advanced Board PCB Factory recipe at tier "
                        + tier
                        + ": required inputs unavailable.");
                continue;
            }

            int amountOfBoards = (int) Math.ceil(8 * (Math.sqrt(Math.pow(2, tier - 3))));
            List<ItemStack> boards = new ArrayList<>();
            for (int remaining = amountOfBoards; remaining > 64; remaining -= 64) {
                boards.add(ItemList.Circuit_Board_Fiberglass_Advanced.get(64));
            }
            boards.add(
                ItemList.Circuit_Board_Fiberglass_Advanced.get(amountOfBoards % 64 == 0 ? 64 : amountOfBoards % 64));

            GTValues.RA.stdBuilder()
                .itemInputs(resinPlate, aluminiumFoil, energeticSilverFoil)
                .fluidInputs(sulfuricAcid, sodiumPersulfate)
                .itemOutputs(boards.toArray(new ItemStack[0]))
                .duration((int) Math.ceil(600 / Math.sqrt(Math.pow(1.5, tier - 3.5))))
                .eut((int) GTValues.VP[tier] * 3 / 4)
                .metadata(PCBFactoryTierKey.INSTANCE, 1)
                .addTo(RecipeMaps.pcbFactoryRecipes);
        }

        MyMod.logInfo(
            "Registered PCB Factory recipe line: Aluminium + 16x Energetic Silver foils + Sulfuric Acid + Sodium Persulfate -> Fiberglass Advanced Circuit Board (tier 3+).");
    }

}
