package gtnh_additional_crafts.recipe.loaders;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import goodgenerator.loader.Loaders;
import gregtech.api.enums.GTValues;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipeBuilder;
import gtnh_additional_crafts.MyMod;
import gtnh_additional_crafts.recipe.util.MachineRecipes;

public final class RadioactiveWasteRecipes {

    private RadioactiveWasteRecipes() {}

    public static void register() {
        registerRadioactiveWasteNoveltyRecipes();
    }

    public static void registerRadioactiveWasteNoveltyRecipes() {
        registerRadioactiveWasteFireworkStarRecipe();
        registerRadioactiveWasteLimeDyeRecipe();
    }

    private static void registerRadioactiveWasteFireworkStarRecipe() {
        ItemStack radioactiveWaste = new ItemStack(Loaders.radioactiveWaste, 1);
        ItemStack gunpowder = new ItemStack(Items.gunpowder, 2);

        // Novelty item, not real chemistry: a glowing, twinkling green firework star. No stat value,
        // just a "Chernobyl fireworks" meme sink for the Neutron Activator's accidental byproduct.
        NBTTagCompound explosion = new NBTTagCompound();
        explosion.setByte("Type", (byte) 0);
        explosion.setIntArray("Colors", new int[] { 0x39FF14 });
        explosion.setBoolean("Flicker", true);
        explosion.setBoolean("Trail", true);
        NBTTagCompound fireworkTag = new NBTTagCompound();
        fireworkTag.setTag("Explosion", explosion);
        ItemStack radioactiveFireworkStar = new ItemStack(Items.firework_charge, 1);
        radioactiveFireworkStar.setTagCompound(fireworkTag);

        MachineRecipes.assembler()
            .itemInputs(radioactiveWaste, gunpowder)
            .itemOutputs(radioactiveFireworkStar)
            .duration(5 * GTRecipeBuilder.SECONDS)
            .eut(16)
            .register(
                "Skipped Radioactive Waste -> Firework Star recipe: required items unavailable.",
                "Registered Assembler recipe: 1x Radioactive Waste + 2x Gunpowder -> 1x glowing green Firework Star (novelty, no stats).");
    }

    private static void registerRadioactiveWasteLimeDyeRecipe() {
        ItemStack radioactiveWaste = new ItemStack(Loaders.radioactiveWaste, 1);
        ItemStack boneMeal = new ItemStack(Items.dye, 1, 15);
        ItemStack limeDye = new ItemStack(Items.dye, 2, 10);

        if (radioactiveWaste.getItem() == null || boneMeal.getItem() == null || limeDye.getItem() == null) {
            MyMod.logInfo("Skipped Radioactive Waste -> Lime Dye recipe: required items unavailable.");
            return;
        }

        // Novelty item, not real chemistry: irradiated Bone Meal bleaches into a sickly glow-green
        // dye. Cosmetic only - tints wool/glass, "old radium paint" meme.
        GTValues.RA.stdBuilder()
            .itemInputs(radioactiveWaste, boneMeal)
            .itemOutputs(limeDye)
            .duration(5 * GTRecipeBuilder.SECONDS)
            .eut(16)
            .addTo(RecipeMaps.mixerRecipes);

        MyMod.logInfo(
            "Registered Mixer recipe: 1x Radioactive Waste + 1x Bone Meal -> 2x Lime Dye (novelty, cosmetic only).");
    }

}
