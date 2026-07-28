package gtnh_additional_crafts.fluid;

import net.minecraftforge.fluids.Fluid;

public class CalciumHypochloriteFluid extends Fluid {

    private static final int CALCIUM_HYPOCHLORITE_COLOR = 0xE8F0DC;

    public CalciumHypochloriteFluid() {
        super("calcium_hypochlorite");
        setUnlocalizedName("calcium_hypochlorite");
        setDensity(1350);
        setTemperature(300);
        setViscosity(1100);
    }

    @Override
    public int getColor() {
        return CALCIUM_HYPOCHLORITE_COLOR;
    }
}
