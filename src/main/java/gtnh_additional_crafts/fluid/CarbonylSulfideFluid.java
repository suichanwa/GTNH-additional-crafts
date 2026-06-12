package gtnh_additional_crafts.fluid;

import net.minecraftforge.fluids.Fluid;

public class CarbonylSulfideFluid extends Fluid {

    private static final int CARBONYL_SULFIDE_COLOR = 0xE8E4C9;

    public CarbonylSulfideFluid() {
        super("carbonyl_sulfide");
        setUnlocalizedName("carbonyl_sulfide");
        setDensity(-1);
        setTemperature(300);
        setViscosity(200);
        setGaseous(true);
    }

    @Override
    public int getColor() {
        return CARBONYL_SULFIDE_COLOR;
    }
}
