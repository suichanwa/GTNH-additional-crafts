package com.myname.mymodid.compat.gregtech;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.lazy;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;

import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchDynamo;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.tileentities.machines.multi.MTEDieselEngine;
import tectech.thing.metaTileEntity.hatch.MTEHatchDynamoMulti;

public class PatchedMTEDieselEngine extends MTEDieselEngine {

    private static final List<Class<? extends IMetaTileEntity>> DYNAMO_HATCH_TYPES = Arrays
        .<Class<? extends IMetaTileEntity>>asList(MTEHatchDynamo.class, MTEHatchDynamoMulti.class);

    private static final IStructureDefinition<MTEDieselEngine> CUSTOM_STRUCTURE_DEFINITION = createStructureDefinition();

    private static final int DEFAULT_MAX_EFFICIENCY = 10000;
    private static final int OXYGEN_BOOSTED_MAX_EFFICIENCY = 30000;
    private static final int DINITROGEN_TETROXIDE_BOOSTED_MAX_EFFICIENCY = 40000;
    private static final int OVERCLOCKED_MAX_EFFICIENCY_NUMERATOR = 4;
    private static final int OVERCLOCKED_MAX_EFFICIENCY_DENOMINATOR = 5;

    private static final int OVERCLOCKED_NOMINAL_OUTPUT = 5120;

    // 1 mB/t = 20 L/s at 20 TPS.
    private static final int DINITROGEN_TETROXIDE_CONSUMPTION_PER_TICK = 1;
    private static final String NBT_KEY_OVERCLOCK_MODE = "OverclockMode";

    private BoostMode boostMode = BoostMode.NONE;
    private boolean overclockMode = false;
    private final ArrayList<MTEHatchDynamoMulti> multiAmpDynamoHatches = new ArrayList<>();

    private enum BoostMode {
        NONE,
        OXYGEN,
        DINITROGEN_TETROXIDE
    }

    private static final class DynamoSink {

        private final IGregTechTileEntity baseMetaTileEntity;
        private final long voltage;
        private long remainingAmperes;

        private DynamoSink(IGregTechTileEntity baseMetaTileEntity, long voltage, long remainingAmperes) {
            this.baseMetaTileEntity = baseMetaTileEntity;
            this.voltage = voltage;
            this.remainingAmperes = remainingAmperes;
        }
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
    public IStructureDefinition<MTEDieselEngine> getStructureDefinition() {
        return CUSTOM_STRUCTURE_DEFINITION;
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType("Combustion Generator, LCE")
            .addInfo("Supply Diesel Fuels and 1000L of Lubricant per hour to run")
            .addInfo("Supply 40L/s Oxygen or 20L/s Dinitrogen Tetroxide to boost output (optional)")
            .addInfo("Default: Produces 2048EU/t at 100% fuel efficiency")
            .addInfo("Boosted: Produces 6144EU/t at 150% fuel efficiency")
            .addInfo("N2O4 boost can reach 400% max efficiency (up to 8192EU/t)")
            .addInfo("Screwdriver right-click toggles overclock mode (2x output, lower fuel efficiency)")
            .addInfo("Supports standard and 16A dynamo hatches")
            .addPollutionAmount(getPollutionPerSecond(null))
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
            .addInputHatch("Oxygen or Dinitrogen Tetroxide, optional, next to a Gear Box", 1)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public boolean depleteInput(FluidStack fluidStack) {
        if (!isOxygenRequest(fluidStack)) {
            return super.depleteInput(fluidStack);
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
            return OVERCLOCKED_NOMINAL_OUTPUT;
        }
        return super.getNominalOutput();
    }

    @Override
    public int getMaxEfficiency(ItemStack itemStack) {
        int maxEfficiency;
        if (!super.boostEu) {
            maxEfficiency = DEFAULT_MAX_EFFICIENCY;
        } else if (boostMode == BoostMode.DINITROGEN_TETROXIDE) {
            maxEfficiency = DINITROGEN_TETROXIDE_BOOSTED_MAX_EFFICIENCY;
        } else {
            maxEfficiency = OXYGEN_BOOSTED_MAX_EFFICIENCY;
        }
        return applyOverclockEfficiencyPenalty(maxEfficiency);
    }

    @Override
    public CheckRecipeResult checkProcessing() {
        boostMode = BoostMode.NONE;
        return super.checkProcessing();
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity baseMetaTileEntity, ItemStack stack) {
        multiAmpDynamoHatches.clear();
        return super.checkMachine(baseMetaTileEntity, stack);
    }

    @Override
    public boolean addDynamoToMachineList(IGregTechTileEntity tileEntity, int casingTextureIndex) {
        if (tileEntity == null) {
            return false;
        }

        IMetaTileEntity metaTileEntity = tileEntity.getMetaTileEntity();
        if (metaTileEntity == null) {
            return false;
        }

        if (metaTileEntity instanceof MTEHatchDynamoMulti) {
            MTEHatchDynamoMulti multiAmpDynamo = (MTEHatchDynamoMulti) metaTileEntity;
            multiAmpDynamo.updateTexture(casingTextureIndex);
            multiAmpDynamo.updateCraftingIcon(getMachineCraftingIcon());
            return multiAmpDynamoHatches.add(multiAmpDynamo);
        }

        return super.addDynamoToMachineList(tileEntity, casingTextureIndex);
    }

    @Override
    public boolean addEnergyOutput(long euToOutput) {
        if (euToOutput <= 0L) {
            return true;
        }
        if (mDynamoHatches.isEmpty() && multiAmpDynamoHatches.isEmpty()) {
            return false;
        }
        return addEnergyOutputToAllDynamos(euToOutput, true);
    }

    @Override
    public void onScrewdriverRightClick(ForgeDirection side, EntityPlayer player, float x, float y, float z,
        ItemStack tool) {
        // Intentionally bypass superclass screwdriver behavior: this click toggles overclock mode.
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
                overclockMode ? "Overclock mode enabled: 2x output, lower fuel efficiency."
                    : "Overclock mode disabled: normal output and fuel efficiency.");
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

    private boolean isOxygenRequest(FluidStack fluidStack) {
        if (fluidStack == null) {
            return false;
        }
        FluidStack oxygenGas = Materials.Oxygen.getGas(1L);
        return oxygenGas != null && fluidStack.isFluidEqual(oxygenGas);
    }

    private boolean depleteDinitrogenTetroxideForBoost() {
        FluidStack dinitrogenTetroxideGas = Materials.DinitrogenTetroxide
            .getGas(DINITROGEN_TETROXIDE_CONSUMPTION_PER_TICK);
        if (dinitrogenTetroxideGas != null && super.depleteInput(dinitrogenTetroxideGas)) {
            return true;
        }

        FluidStack dinitrogenTetroxideFluid = Materials.DinitrogenTetroxide
            .getFluid(DINITROGEN_TETROXIDE_CONSUMPTION_PER_TICK);
        return dinitrogenTetroxideFluid != null && super.depleteInput(dinitrogenTetroxideFluid);
    }

    private int applyOverclockEfficiencyPenalty(int maxEfficiency) {
        if (!overclockMode) {
            return maxEfficiency;
        }
        return maxEfficiency * OVERCLOCKED_MAX_EFFICIENCY_NUMERATOR / OVERCLOCKED_MAX_EFFICIENCY_DENOMINATOR;
    }

    private boolean addEnergyOutputToAllDynamos(long euToOutput, boolean allowMixedVoltageDynamos) {
        List<DynamoSink> sinks = collectDynamoSinks();
        long totalOutputCapacity = calculateTotalCapacity(sinks);
        boolean hasMixedVoltageDynamos = hasMixedVoltageDynamos(sinks);
        if (totalOutputCapacity < euToOutput || (!allowMixedVoltageDynamos && hasMixedVoltageDynamos)) {
            explodeMultiblock();
            return false;
        }

        long euRemaining = euToOutput;

        // Proportional first pass, then one-amp round-robin to distribute leftover fairly.
        for (DynamoSink sink : sinks) {
            if (euRemaining <= 0L) {
                break;
            }
            long sinkCapacity = safeMultiplyClamp(sink.voltage, sink.remainingAmperes);
            long proportionalShare = (long) Math
                .floor((double) euToOutput * (double) sinkCapacity / (double) totalOutputCapacity);
            long euForSink = Math.min(euRemaining, proportionalShare);
            euRemaining -= outputEnergyToSingleDynamo(euForSink, sink);
        }

        while (euRemaining > 0L) {
            boolean injectedAny = false;
            for (DynamoSink sink : sinks) {
                if (euRemaining <= 0L) {
                    break;
                }
                long euForStep = Math.min(euRemaining, sink.voltage);
                long injected = outputEnergyToSingleDynamo(euForStep, sink);
                if (injected > 0L) {
                    euRemaining -= injected;
                    injectedAny = true;
                }
            }
            if (!injectedAny) {
                break;
            }
        }

        return euRemaining < euToOutput;
    }

    private long outputEnergyToSingleDynamo(long euRemaining, DynamoSink sink) {
        if (euRemaining <= 0L || sink == null
            || sink.baseMetaTileEntity == null
            || sink.voltage <= 0L
            || sink.remainingAmperes <= 0L) {
            return 0L;
        }

        long fullAmperesNeeded = euRemaining / sink.voltage;
        long amperesToInsert = Math.min(sink.remainingAmperes, fullAmperesNeeded);
        for (long i = 0L; i < amperesToInsert; i++) {
            sink.baseMetaTileEntity.increaseStoredEnergyUnits(sink.voltage, false);
        }
        sink.remainingAmperes -= amperesToInsert;

        long euInjected = sink.voltage * amperesToInsert;
        long remainder = euRemaining - euInjected;
        if (remainder > 0L && sink.remainingAmperes > 0L) {
            sink.baseMetaTileEntity.increaseStoredEnergyUnits(remainder, false);
            sink.remainingAmperes--;
            euInjected += remainder;
        }
        return euInjected;
    }

    private List<DynamoSink> collectDynamoSinks() {
        List<DynamoSink> sinks = new ArrayList<>();
        for (MTEHatchDynamo dynamoHatch : GTUtility.validMTEList(mDynamoHatches)) {
            sinks.add(
                new DynamoSink(
                    dynamoHatch.getBaseMetaTileEntity(),
                    dynamoHatch.maxEUOutput(),
                    Math.max(0L, dynamoHatch.maxAmperesOut())));
        }
        for (MTEHatchDynamoMulti dynamoHatch : GTUtility.validMTEList(multiAmpDynamoHatches)) {
            sinks.add(
                new DynamoSink(
                    dynamoHatch.getBaseMetaTileEntity(),
                    dynamoHatch.maxEUOutput(),
                    Math.max(0L, dynamoHatch.maxAmperesOut())));
        }
        return sinks;
    }

    private long calculateTotalCapacity(List<DynamoSink> sinks) {
        long totalCapacity = 0L;
        for (DynamoSink sink : sinks) {
            totalCapacity = safeAddClamp(totalCapacity, safeMultiplyClamp(sink.voltage, sink.remainingAmperes));
        }
        return totalCapacity;
    }

    private boolean hasMixedVoltageDynamos(List<DynamoSink> sinks) {
        long referenceVoltage = -1L;
        for (DynamoSink sink : sinks) {
            if (sink.voltage <= 0L || sink.remainingAmperes <= 0L) {
                continue;
            }
            if (referenceVoltage == -1L) {
                referenceVoltage = sink.voltage;
            } else if (referenceVoltage != sink.voltage) {
                return true;
            }
        }
        return false;
    }

    private static long safeMultiplyClamp(long left, long right) {
        try {
            return Math.multiplyExact(left, right);
        } catch (ArithmeticException ignored) {
            return Long.MAX_VALUE;
        }
    }

    private static long safeAddClamp(long left, long right) {
        try {
            return Math.addExact(left, right);
        } catch (ArithmeticException ignored) {
            return Long.MAX_VALUE;
        }
    }

    private static IStructureDefinition<MTEDieselEngine> createStructureDefinition() {
        return StructureDefinition.<MTEDieselEngine>builder()
            .addShape(
                "main",
                transpose(
                    new String[][] { { "---", "iii", "chc", "chc", "ccc" }, { "---", "i~i", "hgh", "hgh", "cdc" },
                        { "---", "iii", "chc", "chc", "ccc" } }))
            .addElement('i', lazy(mte -> ofBlock(mte.getIntakeBlock(), mte.getIntakeMeta())))
            .addElement('c', lazy(mte -> ofBlock(mte.getCasingBlock(), mte.getCasingMeta())))
            .addElement('g', lazy(mte -> ofBlock(mte.getGearboxBlock(), mte.getGearboxMeta())))
            .addElement(
                'd',
                lazy(
                    mte -> HatchElement.Dynamo.withMteClasses(DYNAMO_HATCH_TYPES)
                        .newAny(mte.getCasingTextureIndex(), 2)))
            .addElement(
                'h',
                lazy(
                    mte -> buildHatchAdder(MTEDieselEngine.class)
                        .atLeast(
                            HatchElement.InputHatch,
                            HatchElement.InputHatch,
                            HatchElement.InputHatch,
                            HatchElement.Muffler,
                            HatchElement.Maintenance)
                        .casingIndex(mte.getCasingTextureIndex())
                        .dot(1)
                        .buildAndChain(mte.getCasingBlock(), mte.getCasingMeta())))
            .build();
    }
}
