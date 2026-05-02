package gtnh_additional_crafts.fluid;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import cpw.mods.fml.common.registry.GameRegistry;

public final class ModFluids {

    public static Fluid bioTar;
    public static BlockBioTarFluid bioTarBlock;
    public static Fluid cryonitroxOxidizer;
    public static BlockCryonitroxFluid cryonitroxOxidizerBlock;

    private ModFluids() {}

    public static void registerFluids() {
        Fluid existingBioTar = FluidRegistry.getFluid("bio_tar");
        if (existingBioTar != null) {
            bioTar = existingBioTar;
        } else {
            bioTar = new BioTarFluid();
            FluidRegistry.registerFluid(bioTar);
        }

        if (bioTarBlock == null) {
            bioTarBlock = new BlockBioTarFluid(bioTar);
            GameRegistry.registerBlock(bioTarBlock, "bio_tar");
        }

        Fluid existingCryonitroxOxidizer = FluidRegistry.getFluid("cryonitrox_oxidizer");
        if (existingCryonitroxOxidizer != null) {
            cryonitroxOxidizer = existingCryonitroxOxidizer;
        } else {
            cryonitroxOxidizer = new CryonitroxFluid();
            FluidRegistry.registerFluid(cryonitroxOxidizer);
        }

        if (cryonitroxOxidizerBlock == null) {
            cryonitroxOxidizerBlock = new BlockCryonitroxFluid(cryonitroxOxidizer);
            GameRegistry.registerBlock(cryonitroxOxidizerBlock, "cryonitrox_oxidizer");
        }
    }

    public static FluidStack getBioTar(int amount) {
        Fluid fluid = FluidRegistry.getFluid("bio_tar");
        return fluid == null ? null : new FluidStack(fluid, amount);
    }

    public static FluidStack getCryonitroxOxidizer(int amount) {
        Fluid fluid = FluidRegistry.getFluid("cryonitrox_oxidizer");
        return fluid == null ? null : new FluidStack(fluid, amount);
    }
}
