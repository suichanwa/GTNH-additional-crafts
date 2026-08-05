package gtnh_additional_crafts.recipe.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.enums.GTValues;
import gregtech.api.recipe.RecipeMap;
import gtnh_additional_crafts.MyMod;

/**
 * Fluent wrapper around {@code GTValues.RA.stdBuilder()} for a single target {@link RecipeMap}.
 * Bakes in the null-check-and-skip pattern repeated across every recipe registration method in
 * this mod, so loader code only needs to describe the recipe, not re-derive the boilerplate.
 */
public final class MachineRecipeBuilder {

    private static final ItemStack[] NO_ITEMS = new ItemStack[0];
    private static final FluidStack[] NO_FLUIDS = new FluidStack[0];

    private final RecipeMap<?> recipeMap;
    private ItemStack[] itemsIn = NO_ITEMS;
    private ItemStack[] itemsOut = NO_ITEMS;
    private FluidStack[] fluidsIn = NO_FLUIDS;
    private FluidStack[] fluidsOut = NO_FLUIDS;
    private int duration;
    private int eut;

    MachineRecipeBuilder(RecipeMap<?> recipeMap) {
        this.recipeMap = recipeMap;
    }

    public MachineRecipeBuilder itemInputs(ItemStack... items) {
        this.itemsIn = items;
        return this;
    }

    public MachineRecipeBuilder itemOutputs(ItemStack... items) {
        this.itemsOut = items;
        return this;
    }

    public MachineRecipeBuilder fluidInputs(FluidStack... fluids) {
        this.fluidsIn = fluids;
        return this;
    }

    public MachineRecipeBuilder fluidOutputs(FluidStack... fluids) {
        this.fluidsOut = fluids;
        return this;
    }

    public MachineRecipeBuilder duration(int ticks) {
        this.duration = ticks;
        return this;
    }

    public MachineRecipeBuilder eut(int eut) {
        this.eut = eut;
        return this;
    }

    /**
     * Validates every declared item/fluid input and output is non-null, then registers the recipe.
     * Logs {@code skipMessage} and does nothing if any of them is missing; otherwise registers the
     * recipe and logs {@code successMessage}.
     *
     * @return true if the recipe was registered.
     */
    public boolean register(String skipMessage, String successMessage) {
        if (!allPresent(itemsIn) || !allPresent(itemsOut) || !allPresent(fluidsIn) || !allPresent(fluidsOut)) {
            MyMod.logInfo(skipMessage);
            return false;
        }

        var builder = GTValues.RA.stdBuilder();
        if (itemsIn.length > 0) {
            builder.itemInputs(itemsIn);
        }
        if (itemsOut.length > 0) {
            builder.itemOutputs(itemsOut);
        }
        if (fluidsIn.length > 0) {
            builder.fluidInputs(fluidsIn);
        }
        if (fluidsOut.length > 0) {
            builder.fluidOutputs(fluidsOut);
        }
        builder.duration(duration)
            .eut(eut)
            .addTo(recipeMap);

        MyMod.logInfo(successMessage);
        return true;
    }

    private static boolean allPresent(Object[] values) {
        if (values == null) {
            return false;
        }
        for (Object value : values) {
            if (value == null) {
                return false;
            }
            if (value instanceof ItemStack stack && stack.getItem() == null) {
                return false;
            }
        }
        return true;
    }
}
