package gtnh_additional_crafts.machine;

import gregtech.GTMod;
import gregtech.api.enums.Textures;
import gregtech.api.enums.TieredVariant;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.render.TextureFactory;
import gtnh_additional_crafts.client.ModTextures;

public class MTELargeLiquidBoiler extends MTEBoilerLiquidBase {

    public MTELargeLiquidBoiler(int aID, String aName, String aNameRegional) {
        super(
            aID,
            aName,
            aNameRegional,
            new String[] { "High Pressure Liquid-burning Steam Boiler", "Produces 320L of Steam per second",
                "Causes " + GTMod.proxy.mPollutionHighPressureCoalBoilerPerSecond + " Pollution per second",
                "Fuels (1000L): Creosote (40s), Biomass (30s), Oil (25s), Fish Oil (20s), Seed Oil (15s)",
                "Consumes fuel in 100L increments (4x burn rate, 4s / 100L Creosote)" },
            32000,
            new ITexture[0]);
    }

    public MTELargeLiquidBoiler(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aDescription, 32000, aTextures);
    }

    @Override
    public ITexture[][][] getTextureSet(ITexture[] aTextures) {
        ITexture[][][] rTextures = new ITexture[5][17][];
        ITexture[] texBottom = new ITexture[] {
            TextureFactory.of(Textures.BlockIcons.MACHINE_STEELBRICKS_BOTTOM) };
        ITexture[] texTop = new ITexture[] { TextureFactory.of(Textures.BlockIcons.MACHINE_STEELBRICKS_TOP),
            TextureFactory.of(Textures.BlockIcons.OVERLAY_PIPE) };
        ITexture[] texSide = new ITexture[] { TextureFactory.of(Textures.BlockIcons.MACHINE_STEELBRICKS_SIDE),
            TextureFactory.of(Textures.BlockIcons.OVERLAY_PIPE) };
        ITexture[] texFront = new ITexture[] { TextureFactory.of(Textures.BlockIcons.MACHINE_STEELBRICKS_SIDE),
            TextureFactory.of(ModTextures.BOILER_LIQUID_FRONT) };
        ITexture[] texFrontActive = new ITexture[] {
            TextureFactory.of(Textures.BlockIcons.MACHINE_STEELBRICKS_SIDE),
            TextureFactory.of(ModTextures.BOILER_LIQUID_FRONT_ACTIVE),
            TextureFactory.builder()
                .addIcon(ModTextures.BOILER_LIQUID_FRONT_ACTIVE_GLOW)
                .glow()
                .build() };
        for (int i = 0; i < 17; ++i) {
            rTextures[0][i] = texBottom;
            rTextures[1][i] = texTop;
            rTextures[2][i] = texSide;
            rTextures[3][i] = texFront;
            rTextures[4][i] = texFrontActive;
        }
        return rTextures;
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new MTELargeLiquidBoiler(this.mName, this.mTier, this.mDescriptionArray, this.mTextures);
    }

    @Override
    public int maxProgresstime() {
        return 1000;
    }

    @Override
    protected int getMaxTemperature() {
        return 1000;
    }

    @Override
    protected int getProductionPerSecond() {
        return 320;
    }

    @Override
    protected int getEnergyConsumption() {
        return 2;
    }

    @Override
    protected int getCooldownInterval() {
        return 40;
    }

    @Override
    protected int getHeatUpRate() {
        return 6;
    }

    @Override
    protected int getHeatUpAmount() {
        return 1;
    }

    @Override
    protected int getPollution() {
        return GTMod.proxy.mPollutionHighPressureCoalBoilerPerSecond;
    }

    @Override
    public TieredVariant getTieredVariant() {
        return TieredVariant.STEEL;
    }

    @Override
    public int getCapacity() {
        return 32000;
    }
}
