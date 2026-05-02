package gtnh_additional_crafts.fluid;

import net.minecraftforge.fluids.Fluid;

public class CryonitroxFluid extends Fluid {

    private static final int CRYONITROX_COLOR = 0x86D8FF;

    public CryonitroxFluid() {
        super("cryonitrox_oxidizer");
        setUnlocalizedName("cryonitrox_oxidizer");
        setDensity(900);
        setTemperature(85);
        setViscosity(650);
    }

    @Override
    public int getColor() {
        return CRYONITROX_COLOR;
    }
}
