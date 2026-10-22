package gtnh_additional_crafts.patches.gregtech;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;

import gregtech.api.casing.Casings;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.tileentities.machines.multi.MTELargeCombustionEngine;
import gtnh_additional_crafts.Config;
import gtnh_additional_crafts.fluid.ModFluids;

public class PatchedMTEDieselEngine extends MTELargeCombustionEngine {

    private static final IStructureDefinition<MTELargeCombustionEngine> CUSTOM_STRUCTURE_DEFINITION = createStructureDefinition();
    private static final String NBT_KEY_OVERCLOCK_MODE = "OverclockMode";

    private BoostMode boostMode = BoostMode.NONE;
    private boolean overclockMode = false;
    private int casingAmount = 0;

    private enum BoostMode {
        NONE,
        OXYGEN,
        DINITROGEN_TETROXIDE,
        CRYONITROX
    }

    public PatchedMTEDieselEngine(int id, String name, String nameRegional) {
        super(id, name, nameRegional);
    }

    public PatchedMTEDieselEngine(String name) {
        super(name);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new PatchedMTEDieselEngine(super.mName);
    }

    @Override
    public IStructureDefinition<MTELargeCombustionEngine> getStructureDefinition() {
        return CUSTOM_STRUCTURE_DEFINITION;
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        int normalNominalOutput = super.getNominalOutput();
        int overclockNominalOutput = Config.lceOverclockNominalOutput;
        long dinitrogenConsumptionPerSecond = (long) Config.lceDinitrogenTetroxideConsumptionPerTick * 20L;
        long normalOutput = calculateOutput(normalNominalOutput, Config.lceDefaultMaxEfficiency);
        long oxygenBoostedOutput = calculateOutput(normalNominalOutput, Config.lceOxygenBoostedMaxEfficiency);
        long dinitrogenOutput = calculateOutput(normalNominalOutput, Config.lceDinitrogenTetroxideBoostedMaxEfficiency);
        long cryonitroxOutput = calculateOutput(normalNominalOutput, Config.lceCryonitroxBoostedMaxEfficiency);
        long overclockDinitrogenOutput = calculateOutput(
            overclockNominalOutput,
            Config.lceDinitrogenTetroxideBoostedMaxEfficiency);
        long overclockCryonitroxOutput = calculateOutput(
            overclockNominalOutput,
            Config.lceCryonitroxBoostedMaxEfficiency);
        long cryonitroxConsumptionPerSecond = (long) Config.lceCryonitroxConsumptionPerTick * 20L;

        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType("Combustion Generator, LCE")
            .addInfo("Supply Diesel Fuels and 1000L of Lubricant per hour to run")
            .addInfo(
                "Supply 40L/s Oxygen or " + String.format("%,d", dinitrogenConsumptionPerSecond)
                    + "L/s Dinitrogen Tetroxide to boost output (optional)")
            .addInfo(
                "Default: Produces " + String.format("%,d", normalOutput)
                    + "EU/t at "
                    + (Config.lceDefaultMaxEfficiency / 100)
                    + "% fuel efficiency")
            .addInfo(
                "Boosted: Produces " + String.format("%,d", oxygenBoostedOutput)
                    + "EU/t at "
                    + (Config.lceOxygenBoostedMaxEfficiency / 100)
                    + "% fuel efficiency")
            .addInfo(
                "N2O4 boost can reach " + (Config.lceDinitrogenTetroxideBoostedMaxEfficiency / 100)
                    + "% max efficiency (up to "
                    + String.format("%,d", dinitrogenOutput)
                    + "EU/t)")
            .addInfo(
                "Cryonitrox boost uses " + String.format("%,d", cryonitroxConsumptionPerSecond)
                    + "L/s and reaches "
                    + (Config.lceCryonitroxBoostedMaxEfficiency / 100)
                    + "% max efficiency (up to "
                    + String.format("%,d", cryonitroxOutput)
                    + "EU/t)")
            .addInfo("Screwdriver right-click toggles overclock mode (2x output, lower fuel efficiency)")
            .addInfo(
                "Overclock nominal output: " + String.format("%,d", overclockNominalOutput)
                    + "EU/t (up to "
                    + String.format("%,d", overclockCryonitroxOutput)
                    + "EU/t with Cryonitrox, "
                    + String.format("%,d", overclockDinitrogenOutput)
                    + "EU/t with N2O4)")
            .addInfo("Supports standard and 16A dynamo hatches")
            .addPollutionAmount(getPollutionPerSecond((ItemStack) null))
            .beginStructureBlock(3, 3, 4, false)
            .addController("Front center")
            .addCasingInfoRange("Stable Titanium Machine Casing", 16, 22, false)
            .addOtherStructurePart("Titanium Gear Box Machine Casing", "Inner 2 blocks")
            .addOtherStructurePart("Engine Intake Machine Casing", "8x, ring around controller")
            .addStructureInfo("Engine Intake Casings must not be obstructed in front (only air blocks)")
            .addDynamoHatch("Back center", 2)
            .addMaintenanceHatch("One of the casings next to a Gear Box", 1)
            .addMufflerHatch("Top middle back, above the rear Gear Box", 1)
            .addInputHatch("Diesel Fuel, next to a Gear Box", 1)
            .addInputHatch("Lubricant, next to a Gear Box", 1)
            .addInputHatch("Oxygen, Cryonitrox, or Dinitrogen Tetroxide, optional, next to a Gear Box", 1)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public boolean depleteInput(FluidStack fluidStack) {
        if (!isBoosterRequest(fluidStack)) {
            return super.depleteInput(fluidStack);
        }

        if (depleteCryonitroxForBoost(fluidStack.amount)) {
            boostMode = BoostMode.CRYONITROX;
            return true;
        }

        if (super.depleteInput(fluidStack)) {
            boostMode = BoostMode.OXYGEN;
            return true;
        }

        boolean consumedDinitrogenTetroxide = depleteDinitrogenTetroxideForBoost();
        boostMode = consumedDinitrogenTetroxide ? BoostMode.DINITROGEN_TETROXIDE : BoostMode.NONE;
        return consumedDinitrogenTetroxide;
    }

    @Override
    protected int getNominalOutput() {
        if (overclockMode) {
            return Config.lceOverclockNominalOutput;
        }
        return super.getNominalOutput();
    }

    @Override
    public int getMaxEfficiency(ItemStack itemStack) {
        int maxEfficiency;
        if (!super.boostEu) {
            maxEfficiency = Config.lceDefaultMaxEfficiency;
        } else if (boostMode == BoostMode.CRYONITROX) {
            maxEfficiency = Config.lceCryonitroxBoostedMaxEfficiency;
        } else if (boostMode == BoostMode.DINITROGEN_TETROXIDE) {
            maxEfficiency = Config.lceDinitrogenTetroxideBoostedMaxEfficiency;
        } else {
            maxEfficiency = Config.lceOxygenBoostedMaxEfficiency;
        }
        return applyOverclockEfficiencyPenalty(maxEfficiency);
    }

    @Override
    public CheckRecipeResult checkProcessing() {
        boostMode = BoostMode.NONE;
        return super.checkProcessing();
    }

    @Override
    public void checkMachine(IGregTechTileEntity baseMetaTileEntity, ItemStack stack,
        List<StructureError> aErrorList) {
        casingAmount = 0;
        if (!checkPiece("main", 1, 1, 1, aErrorList)) {
            return;
        }
        checkCasingMin(aErrorList, casingAmount, 10);
        checkHasMaintenanceHatch(aErrorList);
        checkHasMufflerHatch(aErrorList);
        checkHasInputHatch(aErrorList);
    }

    @Override
    public void onScrewdriverRightClick(ForgeDirection side, EntityPlayer player, float x, float y, float z,
        ItemStack tool) {
        IGregTechTileEntity baseMetaTileEntity = getBaseMetaTileEntity();
        if (baseMetaTileEntity != null && baseMetaTileEntity.isClientSide()) {
            return;
        }

        overclockMode = !overclockMode;
        if (baseMetaTileEntity != null) {
            baseMetaTileEntity.issueTextureUpdate();
        }

        if (player != null) {
            GTUtility.sendChatToPlayer(
                player,
                overclockMode ? "Overclock mode enabled: 2x nominal output, lower fuel efficiency."
                    : "Overclock mode disabled: normal output and efficiency.");
        }
    }

    @Override
    public void saveNBTData(NBTTagCompound nbt) {
        super.saveNBTData(nbt);
        nbt.setBoolean(NBT_KEY_OVERCLOCK_MODE, overclockMode);
    }

    @Override
    public void loadNBTData(NBTTagCompound nbt) {
        super.loadNBTData(nbt);
        overclockMode = nbt.getBoolean(NBT_KEY_OVERCLOCK_MODE);
    }

    private boolean isBoosterRequest(FluidStack fluidStack) {
        if (fluidStack == null) {
            return false;
        }
        Materials booster = getBooster();
        if (booster == null) {
            return false;
        }
        FluidStack boosterGas = booster.getGas(Math.max(1, fluidStack.amount));
        return boosterGas != null && fluidStack.isFluidEqual(boosterGas);
    }

    private boolean depleteCryonitroxForBoost(int boosterRequestedPerTick) {
        int consumption = Math.max(boosterRequestedPerTick, Math.max(1, Config.lceCryonitroxConsumptionPerTick));
        FluidStack cryonitrox = ModFluids.getCryonitroxOxidizer(consumption);
        return cryonitrox != null && super.depleteInput(cryonitrox);
    }

    private boolean depleteDinitrogenTetroxideForBoost() {
        FluidStack dinitrogenTetroxide = Materials.DinitrogenTetroxide
            .getGas(Math.max(1, Config.lceDinitrogenTetroxideConsumptionPerTick));
        return dinitrogenTetroxide != null && super.depleteInput(dinitrogenTetroxide);
    }

    private int applyOverclockEfficiencyPenalty(int efficiency) {
        if (!overclockMode) {
            return efficiency;
        }
        int denominator = Math.max(1, Config.lceOverclockEfficiencyDenominator);
        return Math.max(0, efficiency * Config.lceOverclockEfficiencyNumerator / denominator);
    }

    private static IStructureDefinition<MTELargeCombustionEngine> createStructureDefinition() {
        return StructureDefinition.<MTELargeCombustionEngine>builder()
            .addShape(
                "main",
                new String[][] {
                    { "    ----", "    ----", "        " },
                    { "EBECCCCE", "E~ECCCCE", "EDEDDDDE" },
                    { "EBEFFFFE", "DBBAAAAG", "DDDDDDDD" },
                    { "EBEBBBBE", "EBEFFFFE", "EDEDDDDE" }
                })
            .addElement('A', Casings.TitaniumGearBoxCasing.asElement())
            .addElement(
                'B',
                buildHatchAdder(MTELargeCombustionEngine.class)
                    .atLeast(HatchElement.Maintenance, HatchElement.Muffler)
                    .casingIndex(Casings.StableTitaniumMachineCasing.textureId)
                    .hint(2)
                    .buildAndChain(
                        onElementPass(
                            x -> ((PatchedMTEDieselEngine) x).casingAmount++,
                            Casings.StableTitaniumMachineCasing.asElement())))
            .addElement('C', Casings.EngineIntakeCasing.asElement())
            .addElement('D', Casings.ChemicallyInertMachineCasing.asElement())
            .addElement('E', GTStructureUtility.ofFrame(Materials.Polytetrafluoroethylene))
            .addElement(
                'F',
                buildHatchAdder(MTELargeCombustionEngine.class)
                    .atLeast(HatchElement.InputHatch)
                    .casingIndex(Casings.StableTitaniumMachineCasing.textureId)
                    .hint(1)
                    .buildAndChain(
                        onElementPass(
                            x -> ((PatchedMTEDieselEngine) x).casingAmount++,
                            Casings.StableTitaniumMachineCasing.asElement())))
            .addElement(
                'G',
                HatchElement.Dynamo.or(HatchElement.ExoticDynamo)
                    .newAny(Casings.ChemicallyInertMachineCasing.textureId, 3))
            .build();
    }

    private static long calculateOutput(int nominalOutput, int maxEfficiency) {
        return (long) nominalOutput * maxEfficiency / 10000L;
    }
}
