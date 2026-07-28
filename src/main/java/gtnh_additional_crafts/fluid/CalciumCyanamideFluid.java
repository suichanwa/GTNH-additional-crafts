package gtnh_additional_crafts.fluid;

import net.minecraftforge.fluids.Fluid;

public class CalciumCyanamideFluid extends Fluid {

    private static final int CALCIUM_CYANAMIDE_COLOR = 0x4A4A42;

    public CalciumCyanamideFluid() {
        super("calcium_cyanamide");
        setUnlocalizedName("calcium_cyanamide");
        setDensity(1900);
        setTemperature(320);
        setViscosity(2000);
    }

    @Override
    public int getColor() {
        return CALCIUM_CYANAMIDE_COLOR;
    }
}
