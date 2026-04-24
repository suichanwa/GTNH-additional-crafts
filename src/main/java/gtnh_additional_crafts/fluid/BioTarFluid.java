package gtnh_additional_crafts.fluid;

import net.minecraftforge.fluids.Fluid;

public class BioTarFluid extends Fluid {

    private static final int BIO_TAR_COLOR = 0x3A2413;

    public BioTarFluid() {
        super("bio_tar");
        setUnlocalizedName("bio_tar");
        setDensity(1250);
        setTemperature(330);
        setViscosity(2400);
    }

    @Override
    public int getColor() {
        return BIO_TAR_COLOR;
    }
}
