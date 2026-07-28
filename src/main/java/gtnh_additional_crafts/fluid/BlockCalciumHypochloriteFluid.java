package gtnh_additional_crafts.fluid;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCalciumHypochloriteFluid extends BlockFluidClassic {

    public BlockCalciumHypochloriteFluid(Fluid fluid) {
        super(fluid, Material.water);
        setBlockName("calcium_hypochlorite");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        definedFluid.setIcons(iconRegister.registerIcon("water_still"), iconRegister.registerIcon("water_flow"));
    }

    @Override
    public int getRenderColor(int metadata) {
        return definedFluid.getColor();
    }

    @Override
    public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
        return definedFluid.getColor();
    }
}
