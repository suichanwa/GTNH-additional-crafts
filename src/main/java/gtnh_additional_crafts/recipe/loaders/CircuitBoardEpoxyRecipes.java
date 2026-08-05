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

public final class CircuitBoardEpoxyRecipes {

    private CircuitBoardEpoxyRecipes() {}

    public static void register() {
        registerEpoxyAdvancedElectrumPcbFactoryRecipe();
    }

    public static void registerEpoxyAdvancedElectrumPcbFactoryRecipe() {
        // Sibling of the Advanced Circuit Board line (tier 2+, plate + Gold foil + Sulfuric Acid +
        // IronIIIChloride -> Epoxy_Advanced board). Same plate, same fluids/duration formula - the Gold foil
        // slot becomes a flat 6x Electrum foil instead. No circuit gate - the single Electrum foil already
        // disambiguates this recipe from GT's own paired-foil recipe.
        for (int tier = 2; tier <= PCBFactoryManager.mTiersOfPlastics; tier++) {
            Materials plasticMaterial = PCBFactoryManager.getPlasticMaterialFromTier(tier);
            ItemStack resinPlate = plasticMaterial == null ? null : plasticMaterial.getPlates(1);
            ItemStack electrumFoil = GTOreDictUnificator.get(OrePrefixes.foil, Materials.Electrum, 6L);
            FluidStack sulfuricAcid = FluidLookup
                .getFluidOrGas(Materials.SulfuricAcid, (long) (500 * (Math.sqrt(tier - 1))));
            FluidStack ironIIIChloride = FluidLookup
                .getFluidOrGas(Materials.IronIIIChloride, (long) (500 * (Math.sqrt(tier - 1))));

            if (resinPlate == null || resinPlate.getItem() == null
                || electrumFoil == null
                || electrumFoil.getItem() == null
                || sulfuricAcid == null
                || ironIIIChloride == null) {
                MyMod.logInfo(
                    "Skipped Electrum Epoxy Advanced Board PCB Factory recipe at tier " + tier
                        + ": required inputs unavailable.");
                continue;
            }

            int amountOfBoards = (int) Math.ceil(8 * (Math.sqrt(Math.pow(2, tier - 2))));
            List<ItemStack> boards = new ArrayList<>();
            for (int remaining = amountOfBoards; remaining > 64; remaining -= 64) {
                boards.add(ItemList.Circuit_Board_Epoxy_Advanced.get(64));
            }
            boards.add(ItemList.Circuit_Board_Epoxy_Advanced.get(amountOfBoards % 64 == 0 ? 64 : amountOfBoards % 64));

            GTValues.RA.stdBuilder()
                .itemInputs(resinPlate, electrumFoil)
                .fluidInputs(sulfuricAcid, ironIIIChloride)
                .itemOutputs(boards.toArray(new ItemStack[0]))
                .duration((int) Math.ceil(600 / Math.sqrt(Math.pow(1.5, tier - 2.5))))
                .eut((int) GTValues.VP[tier] * 3 / 4)
                .metadata(PCBFactoryTierKey.INSTANCE, 1)
                .addTo(RecipeMaps.pcbFactoryRecipes);
        }

        MyMod.logInfo(
            "Registered PCB Factory recipe line: 6x Electrum foil + Sulfuric Acid + IronIIIChloride -> Epoxy Advanced Circuit Board (tier 2+).");
    }

}
