package gtnh_additional_crafts.recipe.util;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.enums.Materials;

public final class FluidLookup {

    private FluidLookup() {}

    public static FluidStack getFluidOrGas(Materials material, long amount) {
        if (material == null) {
            return null;
        }
        FluidStack fluid = material.getFluid(amount);
        if (fluid != null) {
            return fluid;
        }
        return material.getGas(amount);
    }

    public static FluidStack getMaterialFluidOrFallback(Materials material, long amount, String... fallbackNames) {
        FluidStack primary = getFluidOrGas(material, amount);
        if (primary != null) {
            return primary;
        }
        return getFirstAvailableFluid((int) amount, fallbackNames);
    }

    public static FluidStack getFirstAvailableFluid(int amount, String... names) {
        for (String name : names) {
            FluidStack stack = FluidRegistry.getFluidStack(name, amount);
            if (stack != null) {
                return stack;
            }
        }
        return null;
    }

}
