package gtnh_additional_crafts.machine;

import gregtech.GTMod;
import gregtech.api.enums.Textures;
import gregtech.api.enums.TieredVariant;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.render.TextureFactory;
import gtnh_additional_crafts.client.ModTextures;

public class MTESmallLiquidBoiler extends MTEBoilerLiquidBase {

    public MTESmallLiquidBoiler(int aID, String aName, String aNameRegional) {
        super(
            aID,
            aName,
            aNameRegional,
            new String[] { "An early way to get Steam Power from Liquid Fuels", "Produces 160L of Steam per second",
                "Causes " + GTMod.proxy.mPollutionSmallCoalBoilerPerSecond + " Pollution per second",
                "Fuels (1000L): Creosote (160s), Biomass (120s), Oil (100s), Fish Oil (80s), Seed Oil (60s)",
                "Consumes fuel in 100L increments (16s / 100L Creosote)" },
            16000,
            new ITexture[0]);
    }

    public MTESmallLiquidBoiler(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aDescription, 16000, aTextures);
    }

    @Override
    public ITexture[][][] getTextureSet(ITexture[] aTextures) {
        ITexture[][][] rTextures = new ITexture[5][17][];
        ITexture[] texBottom = new ITexture[] {
            TextureFactory.of(Textures.BlockIcons.MACHINE_BRONZEBRICKS_BOTTOM) };
        ITexture[] texTop = new ITexture[] { TextureFactory.of(Textures.BlockIcons.MACHINE_BRONZEBRICKS_TOP),
            TextureFactory.of(Textures.BlockIcons.OVERLAY_PIPE) };
        ITexture[] texSide = new ITexture[] { TextureFactory.of(Textures.BlockIcons.MACHINE_BRONZEBRICKS_SIDE),
            TextureFactory.of(Textures.BlockIcons.OVERLAY_PIPE) };
        ITexture[] texFront = new ITexture[] { TextureFactory.of(Textures.BlockIcons.MACHINE_BRONZEBRICKS_SIDE),
            TextureFactory.of(ModTextures.BOILER_LIQUID_FRONT) };
        ITexture[] texFrontActive = new ITexture[] {
            TextureFactory.of(Textures.BlockIcons.MACHINE_BRONZEBRICKS_SIDE),
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
        return new MTESmallLiquidBoiler(this.mName, this.mTier, this.mDescriptionArray, this.mTextures);
    }

    @Override
    public int maxProgresstime() {
        return 500;
    }

    @Override
    protected int getMaxTemperature() {
        return 500;
    }

    @Override
    protected int getProductionPerSecond() {
        return 160;
    }

    @Override
    protected int getEnergyConsumption() {
        return 1;
    }

    @Override
    protected int getCooldownInterval() {
        return 45;
    }

    @Override
    protected int getHeatUpRate() {
        return 12;
    }

    @Override
    protected int getHeatUpAmount() {
        return 1;
    }

    @Override
    protected int getPollution() {
        return GTMod.proxy.mPollutionSmallCoalBoilerPerSecond;
    }

    @Override
    public TieredVariant getTieredVariant() {
        return TieredVariant.BRONZE;
    }

    @Override
    public int getCapacity() {
        return 16000;
    }
}
