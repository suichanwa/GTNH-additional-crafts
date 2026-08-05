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

public final class CircuitBoardEliteRecipes {

    private CircuitBoardEliteRecipes() {}

    public static void register() {
        registerEliteBoardPlatinumIridiumIronChlorideRecipe();
        registerEliteBoardPlatinumIridiumSodiumPersulfateRecipe();
        registerEliteBoardMelodicAlloyIronChlorideRecipe();
        registerEliteBoardMelodicAlloySodiumPersulfateRecipe();
    }

    public static void registerEliteBoardPlatinumIridiumIronChlorideRecipe() {
        // Sibling of the Elite Circuit Board line (tier 4+, Multilayered Fiber-Reinforced plate + Palladium
        // foil + Platinum foil + Sulfuric Acid + IronIIIChloride -> Multifiberglass_Elite board). Same plate,
        // same Palladium foil, same fluids/duration formula - only the Platinum foil slot is split into a
        // real Platinum-Iridium alloy pair (4+4), the genuine electrode-grade alloy used for spark-plug and
        // pacemaker contacts.
        for (int tier = 4; tier <= PCBFactoryManager.mTiersOfPlastics; tier++) {
            Materials plasticMaterial = PCBFactoryManager.getPlasticMaterialFromTier(tier);
            ItemStack resinPlate = plasticMaterial == null ? null : plasticMaterial.getPlates(1);
            ItemStack palladiumFoil = GTOreDictUnificator
                .get(OrePrefixes.foil, Materials.Palladium, (long) (16 * (Math.sqrt(tier - 3))));
            ItemStack platinumFoil = GTOreDictUnificator.get(OrePrefixes.foil, Materials.Platinum, 4L);
            ItemStack iridiumFoil = GTOreDictUnificator.get(OrePrefixes.foil, Materials.Iridium, 4L);
            FluidStack sulfuricAcid = FluidLookup
                .getFluidOrGas(Materials.SulfuricAcid, (long) (500 * (Math.sqrt(tier - 3))));
            FluidStack ironIIIChloride = FluidLookup
                .getFluidOrGas(Materials.IronIIIChloride, (long) (2000 * (Math.sqrt(tier - 3))));

            if (resinPlate == null || resinPlate.getItem() == null
                || palladiumFoil == null
                || palladiumFoil.getItem() == null
                || platinumFoil == null
                || platinumFoil.getItem() == null
                || iridiumFoil == null
                || iridiumFoil.getItem() == null
                || sulfuricAcid == null
                || ironIIIChloride == null) {
                MyMod.logInfo(
                    "Skipped Platinum-Iridium (IronIIIChloride) Elite Board PCB Factory recipe at tier " + tier
                        + ": required inputs unavailable.");
                continue;
            }

            int amountOfBoards = (int) Math.ceil(8 * (Math.sqrt(Math.pow(2, tier - 4))));
            List<ItemStack> boards = new ArrayList<>();
            for (int remaining = amountOfBoards; remaining > 64; remaining -= 64) {
                boards.add(ItemList.Circuit_Board_Multifiberglass_Elite.get(64));
            }
            boards.add(
                ItemList.Circuit_Board_Multifiberglass_Elite.get(amountOfBoards % 64 == 0 ? 64 : amountOfBoards % 64));

            GTValues.RA.stdBuilder()
                .itemInputs(resinPlate, palladiumFoil, platinumFoil, iridiumFoil)
                .fluidInputs(sulfuricAcid, ironIIIChloride)
                .itemOutputs(boards.toArray(new ItemStack[0]))
                .duration((int) Math.ceil(600 / Math.sqrt(Math.pow(1.5, tier - 4.5))))
                .eut((int) GTValues.VP[tier] * 3 / 4)
                .metadata(PCBFactoryTierKey.INSTANCE, 1)
                .addTo(RecipeMaps.pcbFactoryRecipes);
        }

        MyMod.logInfo(
            "Registered PCB Factory recipe line: Palladium + 4x Platinum + 4x Iridium foils + Sulfuric Acid + IronIIIChloride -> Elite Circuit Board (tier 4+).");
    }

    public static void registerEliteBoardPlatinumIridiumSodiumPersulfateRecipe() {
        // Same as the IronIIIChloride Platinum-Iridium line above, etchant swapped for Sodium Persulfate.
        for (int tier = 4; tier <= PCBFactoryManager.mTiersOfPlastics; tier++) {
            Materials plasticMaterial = PCBFactoryManager.getPlasticMaterialFromTier(tier);
            ItemStack resinPlate = plasticMaterial == null ? null : plasticMaterial.getPlates(1);
            ItemStack palladiumFoil = GTOreDictUnificator
                .get(OrePrefixes.foil, Materials.Palladium, (long) (16 * (Math.sqrt(tier - 3))));
            ItemStack platinumFoil = GTOreDictUnificator.get(OrePrefixes.foil, Materials.Platinum, 4L);
            ItemStack iridiumFoil = GTOreDictUnificator.get(OrePrefixes.foil, Materials.Iridium, 4L);
            FluidStack sulfuricAcid = FluidLookup
                .getFluidOrGas(Materials.SulfuricAcid, (long) (500 * (Math.sqrt(tier - 3))));
            FluidStack sodiumPersulfate = FluidLookup
                .getFluidOrGas(Materials.SodiumPersulfate, (long) (4000 * (Math.sqrt(tier - 3))));

            if (resinPlate == null || resinPlate.getItem() == null
                || palladiumFoil == null
                || palladiumFoil.getItem() == null
                || platinumFoil == null
                || platinumFoil.getItem() == null
                || iridiumFoil == null
                || iridiumFoil.getItem() == null
                || sulfuricAcid == null
                || sodiumPersulfate == null) {
                MyMod.logInfo(
                    "Skipped Platinum-Iridium (SodiumPersulfate) Elite Board PCB Factory recipe at tier " + tier
                        + ": required inputs unavailable.");
                continue;
            }

            int amountOfBoards = (int) Math.ceil(8 * (Math.sqrt(Math.pow(2, tier - 4))));
            List<ItemStack> boards = new ArrayList<>();
            for (int remaining = amountOfBoards; remaining > 64; remaining -= 64) {
                boards.add(ItemList.Circuit_Board_Multifiberglass_Elite.get(64));
            }
            boards.add(
                ItemList.Circuit_Board_Multifiberglass_Elite.get(amountOfBoards % 64 == 0 ? 64 : amountOfBoards % 64));

            GTValues.RA.stdBuilder()
                .itemInputs(resinPlate, palladiumFoil, platinumFoil, iridiumFoil)
                .fluidInputs(sulfuricAcid, sodiumPersulfate)
                .itemOutputs(boards.toArray(new ItemStack[0]))
                .duration((int) Math.ceil(600 / Math.sqrt(Math.pow(1.5, tier - 4.5))))
                .eut((int) GTValues.VP[tier] * 3 / 4)
                .metadata(PCBFactoryTierKey.INSTANCE, 1)
                .addTo(RecipeMaps.pcbFactoryRecipes);
        }

        MyMod.logInfo(
            "Registered PCB Factory recipe line: Palladium + 4x Platinum + 4x Iridium foils + Sulfuric Acid + Sodium Persulfate -> Elite Circuit Board (tier 4+).");
    }

    public static void registerEliteBoardMelodicAlloyIronChlorideRecipe() {
        // Sibling of the Elite Circuit Board line (tier 4+). Same plate, same Palladium foil, same
        // fluids/duration formula - the Platinum foil slot becomes a flat 8x Melodic Alloy foil instead.
        for (int tier = 4; tier <= PCBFactoryManager.mTiersOfPlastics; tier++) {
            Materials plasticMaterial = PCBFactoryManager.getPlasticMaterialFromTier(tier);
            ItemStack resinPlate = plasticMaterial == null ? null : plasticMaterial.getPlates(1);
            ItemStack palladiumFoil = GTOreDictUnificator
                .get(OrePrefixes.foil, Materials.Palladium, (long) (16 * (Math.sqrt(tier - 3))));
            ItemStack melodicAlloyFoil = GTOreDictUnificator.get(OrePrefixes.foil, Materials.MelodicAlloy, 8L);
            FluidStack sulfuricAcid = FluidLookup
                .getFluidOrGas(Materials.SulfuricAcid, (long) (500 * (Math.sqrt(tier - 3))));
            FluidStack ironIIIChloride = FluidLookup
                .getFluidOrGas(Materials.IronIIIChloride, (long) (2000 * (Math.sqrt(tier - 3))));

            if (resinPlate == null || resinPlate.getItem() == null
                || palladiumFoil == null
                || palladiumFoil.getItem() == null
                || melodicAlloyFoil == null
                || melodicAlloyFoil.getItem() == null
                || sulfuricAcid == null
                || ironIIIChloride == null) {
                MyMod.logInfo(
                    "Skipped Melodic Alloy (IronIIIChloride) Elite Board PCB Factory recipe at tier " + tier
                        + ": required inputs unavailable.");
                continue;
            }

            int amountOfBoards = (int) Math.ceil(8 * (Math.sqrt(Math.pow(2, tier - 4))));
            List<ItemStack> boards = new ArrayList<>();
            for (int remaining = amountOfBoards; remaining > 64; remaining -= 64) {
                boards.add(ItemList.Circuit_Board_Multifiberglass_Elite.get(64));
            }
            boards.add(
                ItemList.Circuit_Board_Multifiberglass_Elite.get(amountOfBoards % 64 == 0 ? 64 : amountOfBoards % 64));

            GTValues.RA.stdBuilder()
                .itemInputs(resinPlate, palladiumFoil, melodicAlloyFoil)
                .fluidInputs(sulfuricAcid, ironIIIChloride)
                .itemOutputs(boards.toArray(new ItemStack[0]))
                .duration((int) Math.ceil(600 / Math.sqrt(Math.pow(1.5, tier - 4.5))))
                .eut((int) GTValues.VP[tier] * 3 / 4)
                .metadata(PCBFactoryTierKey.INSTANCE, 1)
                .addTo(RecipeMaps.pcbFactoryRecipes);
        }

        MyMod.logInfo(
            "Registered PCB Factory recipe line: Palladium + 8x Melodic Alloy foils + Sulfuric Acid + IronIIIChloride -> Elite Circuit Board (tier 4+).");
    }

    public static void registerEliteBoardMelodicAlloySodiumPersulfateRecipe() {
        // Same as the IronIIIChloride Melodic Alloy line above, etchant swapped for Sodium Persulfate.
        for (int tier = 4; tier <= PCBFactoryManager.mTiersOfPlastics; tier++) {
            Materials plasticMaterial = PCBFactoryManager.getPlasticMaterialFromTier(tier);
            ItemStack resinPlate = plasticMaterial == null ? null : plasticMaterial.getPlates(1);
            ItemStack palladiumFoil = GTOreDictUnificator
                .get(OrePrefixes.foil, Materials.Palladium, (long) (16 * (Math.sqrt(tier - 3))));
            ItemStack melodicAlloyFoil = GTOreDictUnificator.get(OrePrefixes.foil, Materials.MelodicAlloy, 8L);
            FluidStack sulfuricAcid = FluidLookup
                .getFluidOrGas(Materials.SulfuricAcid, (long) (500 * (Math.sqrt(tier - 3))));
            FluidStack sodiumPersulfate = FluidLookup
                .getFluidOrGas(Materials.SodiumPersulfate, (long) (4000 * (Math.sqrt(tier - 3))));

            if (resinPlate == null || resinPlate.getItem() == null
                || palladiumFoil == null
                || palladiumFoil.getItem() == null
                || melodicAlloyFoil == null
                || melodicAlloyFoil.getItem() == null
                || sulfuricAcid == null
                || sodiumPersulfate == null) {
                MyMod.logInfo(
                    "Skipped Melodic Alloy (SodiumPersulfate) Elite Board PCB Factory recipe at tier " + tier
                        + ": required inputs unavailable.");
                continue;
            }

            int amountOfBoards = (int) Math.ceil(8 * (Math.sqrt(Math.pow(2, tier - 4))));
            List<ItemStack> boards = new ArrayList<>();
            for (int remaining = amountOfBoards; remaining > 64; remaining -= 64) {
                boards.add(ItemList.Circuit_Board_Multifiberglass_Elite.get(64));
            }
            boards.add(
                ItemList.Circuit_Board_Multifiberglass_Elite.get(amountOfBoards % 64 == 0 ? 64 : amountOfBoards % 64));

            GTValues.RA.stdBuilder()
                .itemInputs(resinPlate, palladiumFoil, melodicAlloyFoil)
                .fluidInputs(sulfuricAcid, sodiumPersulfate)
                .itemOutputs(boards.toArray(new ItemStack[0]))
                .duration((int) Math.ceil(600 / Math.sqrt(Math.pow(1.5, tier - 4.5))))
                .eut((int) GTValues.VP[tier] * 3 / 4)
                .metadata(PCBFactoryTierKey.INSTANCE, 1)
                .addTo(RecipeMaps.pcbFactoryRecipes);
        }

        MyMod.logInfo(
            "Registered PCB Factory recipe line: Palladium + 8x Melodic Alloy foils + Sulfuric Acid + Sodium Persulfate -> Elite Circuit Board (tier 4+).");
    }

}
