package com.myname.mymodid.compat.gregtech;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.lazy;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
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
import com.myname.mymodid.Config;

import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchDynamo;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.recipe.maps.FuelBackend;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.base.GTPPMultiBlockBase;
import gtPlusPlus.xmod.gregtech.common.tileentities.machines.multi.production.MTELargeSemifluidGenerator;
import tectech.thing.metaTileEntity.hatch.MTEHatchDynamoMulti;

public class PatchedMTELargeSemifluidGenerator extends MTELargeSemifluidGenerator {

    private static final byte TITANIUM_GEARBOX_META = 4;
    private static final int MINIMUM_CASINGS = 16;
    private static final int REQUIRED_GEARBOXES = 2;
    private static final int LUBRICANT_CYCLE_TICKS = 72;
    private static final String NBT_KEY_PERFORMANCE_MODE = "PerformanceMode";

    private static final List<Class<? extends IMetaTileEntity>> DYNAMO_HATCH_TYPES = Arrays
        .<Class<? extends IMetaTileEntity>>asList(MTEHatchDynamo.class, MTEHatchDynamoMulti.class);

    private static final IStructureDefinition<MTELargeSemifluidGenerator> CUSTOM_STRUCTURE_DEFINITION = createStructureDefinition();

    private int casingCount = 0;
    private int steelGearboxCount = 0;
    private int titaniumGearboxCount = 0;
    private boolean titaniumGearboxInstalled = false;
    private boolean performanceMode = false;
    private final ArrayList<MTEHatchDynamoMulti> multiAmpDynamoHatches = new ArrayList<>();

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

    public PatchedMTELargeSemifluidGenerator(int id, String name, String nameRegional) {
        super(id, name, nameRegional);
    }

    public PatchedMTELargeSemifluidGenerator(String name) {
        super(name);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new PatchedMTELargeSemifluidGenerator(super.mName);
    }

    @Override
    public IStructureDefinition<MTELargeSemifluidGenerator> getStructureDefinition() {
        return CUSTOM_STRUCTURE_DEFINITION;
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        int steelDefaultOutput = calculateDisplayedOutput(Config.lsbBaseNominalOutput, Config.lsbDefaultMaxEfficiency);
        int steelBoostedOutput = calculateDisplayedOutput(
            Config.lsbBoostedNominalOutput,
            Config.lsbOxygenBoostedMaxEfficiency);
        int titaniumDefaultOutput = calculateDisplayedOutput(
            applyTitaniumOutputMultiplier(Config.lsbBaseNominalOutput),
            Config.lsbDefaultMaxEfficiency);
        int titaniumBoostedOutput = calculateDisplayedOutput(
            applyTitaniumOutputMultiplier(Config.lsbBoostedNominalOutput),
            Config.lsbOxygenBoostedMaxEfficiency);
        int performanceBoostedOutput = calculateDisplayedOutput(
            applyPerformanceOutputMultiplier(Config.lsbBoostedNominalOutput),
            applyPerformanceEfficiencyPenalty(Config.lsbOxygenBoostedMaxEfficiency));
        int maxBoostedOutput = calculateDisplayedOutput(
            applyPerformanceOutputMultiplier(applyTitaniumOutputMultiplier(Config.lsbBoostedNominalOutput)),
            applyPerformanceEfficiencyPenalty(Config.lsbOxygenBoostedMaxEfficiency));

        return new MultiblockTooltipBuilder().addMachineType(getMachineType())
            .addInfo("Engine Intake Casings must not be obstructed in front (only air blocks)")
            .addInfo("Supply Semifluid Fuels and 2000L of Lubricant per hour to run")
            .addInfo("Supply 80L/s Oxygen to boost output (optional)")
            .addInfo(
                "Titanium gearboxes replace steel gearboxes: up to " + GTUtility.formatNumbers(titaniumBoostedOutput)
                    + "EU/t with higher fuel use")
            .addInfo(
                "Screwdriver right-click toggles performance mode: up to " + GTUtility.formatNumbers(maxBoostedOutput)
                    + "EU/t, more oxygen/lubricant use, lower fuel efficiency")
            .addInfo("Supports standard and 16A dynamo hatches")
            .addInfo(
                "Steel build: " + GTUtility.formatNumbers(steelDefaultOutput)
                    + "EU/t default, "
                    + GTUtility.formatNumbers(steelBoostedOutput)
                    + "EU/t with oxygen")
            .addInfo(
                "Titanium build: " + GTUtility.formatNumbers(titaniumDefaultOutput)
                    + "EU/t default, "
                    + GTUtility.formatNumbers(titaniumBoostedOutput)
                    + "EU/t with oxygen")
            .addInfo(
                "Performance + oxygen: " + GTUtility.formatNumbers(performanceBoostedOutput)
                    + "EU/t on steel gearboxes")
            .addPollutionAmount(getPollutionPerSecond(null))
            .beginStructureBlock(3, 3, 4, false)
            .addController("Front Center")
            .addCasingInfoMin("Stable Titanium Machine Casing", MINIMUM_CASINGS, false)
            .addCasingInfoMin("Steel or Titanium Gear Box Machine Casing", REQUIRED_GEARBOXES, false)
            .addCasingInfoMin("Engine Intake Machine Casing", 8, false)
            .addInputHatch("Any Casing", 1)
            .addMaintenanceHatch("Any Casing", 1)
            .addMufflerHatch("Any Casing", 1)
            .addDynamoHatch("Back Center", 2)
            .toolTipFinisher();
    }

    @Override
    public CheckRecipeResult checkProcessing() {
        ArrayList<FluidStack> storedFluids = getStoredFluids();
        FluidStack availableLubricant = Materials.Lubricant.getFluid(0L);
        FluidStack availableOxygen = Materials.Oxygen.getGas(0L);

        for (FluidStack storedFluid : storedFluids) {
            if (availableLubricant != null && storedFluid.isFluidEqual(availableLubricant)) {
                availableLubricant.amount = Math.max(availableLubricant.amount, storedFluid.amount);
            } else if (availableOxygen != null && storedFluid.isFluidEqual(availableOxygen)) {
                availableOxygen.amount = Math.max(availableOxygen.amount, storedFluid.amount);
            }
        }

        int oxygenPerTick = getOxygenConsumptionPerTick();
        boostEu = availableOxygen != null && availableOxygen.amount >= oxygenPerTick;
        if (availableLubricant == null || availableLubricant.amount < getLubricantConsumptionPerCycle(boostEu)) {
            return SimpleCheckRecipeResult.ofFailure("no_lubricant");
        }

        FuelBackend fuelBackend = (FuelBackend) GTPPRecipeMaps.semiFluidFuels.getBackend();
        for (FluidStack storedFluid : storedFluids) {
            GTRecipe fuelRecipe = fuelBackend.findFuel(storedFluid);
            if (fuelRecipe == null) {
                continue;
            }

            int nominalOutput = getNominalOutputForCurrentState();
            fuelConsumption = nominalOutput / fuelRecipe.mSpecialValue;
            FluidStack toConsume = new FluidStack(storedFluid.getFluid(), fuelConsumption);
            if (!depleteInput(toConsume)) {
                continue;
            }

            if (boostEu && !depleteInput(Materials.Oxygen.getGas(oxygenPerTick))) {
                return SimpleCheckRecipeResult.ofFailure("no_oxygen");
            }

            int lubricantPerCycle = getLubricantConsumptionPerCycle(boostEu);
            if ((mRuntime % LUBRICANT_CYCLE_TICKS == 0 || mRuntime == 0)
                && !depleteInput(Materials.Lubricant.getFluid(lubricantPerCycle))) {
                return SimpleCheckRecipeResult.ofFailure("no_lubricant");
            }

            fuelValue = fuelRecipe.mSpecialValue;
            fuelRemaining = storedFluid.amount;
            lEUt = mEfficiency >= 2000 ? nominalOutput : 0L;
            mProgresstime = 1;
            mMaxProgresstime = 1;
            mEfficiencyIncrease = 15;
            return CheckRecipeResultRegistry.GENERATING;
        }

        lEUt = 0L;
        mEfficiency = 0;
        return CheckRecipeResultRegistry.NO_FUEL_FOUND;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity baseMetaTileEntity, ItemStack stack) {
        resetStructureState();
        boolean structureValid = checkPiece(mName, 1, 1, 0);
        titaniumGearboxInstalled = titaniumGearboxCount == REQUIRED_GEARBOXES && steelGearboxCount == 0;
        return structureValid && casingCount >= MINIMUM_CASINGS && hasValidGearboxConfiguration() && checkHatch();
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
            addToMachineList(tileEntity, casingTextureIndex);
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
    public void onModeChangeByScrewdriver(ForgeDirection side, EntityPlayer player, float x, float y, float z) {
        IGregTechTileEntity baseMetaTileEntity = getBaseMetaTileEntity();
        if (baseMetaTileEntity != null && baseMetaTileEntity.isClientSide()) {
            return;
        }

        performanceMode = !performanceMode;
        if (baseMetaTileEntity != null) {
            baseMetaTileEntity.issueTextureUpdate();
        }

        if (player != null) {
            GTUtility.sendChatToPlayer(
                player,
                performanceMode ? "Performance mode enabled: more output, more oxygen/lubricant use, lower efficiency."
                    : "Performance mode disabled: normal Large Semifluid Burner behavior.");
        }
    }

    @Override
    public void saveNBTData(NBTTagCompound nbt) {
        super.saveNBTData(nbt);
        nbt.setBoolean(NBT_KEY_PERFORMANCE_MODE, performanceMode);
    }

    @Override
    public void loadNBTData(NBTTagCompound nbt) {
        super.loadNBTData(nbt);
        performanceMode = nbt.getBoolean(NBT_KEY_PERFORMANCE_MODE);
    }

    @Override
    public int getMaxEfficiency(ItemStack stack) {
        int maxEfficiency = boostEu ? Config.lsbOxygenBoostedMaxEfficiency : Config.lsbDefaultMaxEfficiency;
        return applyPerformanceEfficiencyPenalty(maxEfficiency);
    }

    @Override
    public String[] getExtraInfoData() {
        String[] base = super.getExtraInfoData();
        if (base == null) {
            base = new String[0];
        }
        String[] extended = Arrays.copyOf(base, base.length + 2);
        extended[base.length] = "Gearbox Mode: " + (titaniumGearboxInstalled ? "Titanium" : "Steel");
        extended[base.length + 1] = "Performance Mode: " + (performanceMode ? "Enabled" : "Disabled");
        return extended;
    }

    private void incrementCasingCount() {
        casingCount++;
    }

    private void recordSteelGearbox() {
        steelGearboxCount++;
    }

    private void recordTitaniumGearbox() {
        titaniumGearboxCount++;
    }

    private void resetStructureState() {
        casingCount = 0;
        steelGearboxCount = 0;
        titaniumGearboxCount = 0;
        titaniumGearboxInstalled = false;
        multiAmpDynamoHatches.clear();
        mDynamoHatches.clear();
        mTecTechDynamoHatches.clear();
        mAllDynamoHatches.clear();
    }

    private boolean hasValidGearboxConfiguration() {
        return steelGearboxCount == REQUIRED_GEARBOXES && titaniumGearboxCount == 0
            || titaniumGearboxCount == REQUIRED_GEARBOXES && steelGearboxCount == 0;
    }

    private int getNominalOutputForCurrentState() {
        int nominalOutput = boostEu ? Config.lsbBoostedNominalOutput : Config.lsbBaseNominalOutput;
        if (titaniumGearboxInstalled) {
            nominalOutput = applyTitaniumOutputMultiplier(nominalOutput);
        }
        if (performanceMode) {
            nominalOutput = applyPerformanceOutputMultiplier(nominalOutput);
        }
        return nominalOutput;
    }

    private int getLubricantConsumptionPerCycle(boolean oxygenBoosted) {
        int lubricantPerCycle = oxygenBoosted ? Config.lsbBoostedLubricantPerCycle : Config.lsbBaseLubricantPerCycle;
        if (!performanceMode) {
            return lubricantPerCycle;
        }
        return scaleInt(
            lubricantPerCycle,
            Config.lsbPerformanceLubricantNumerator,
            Config.lsbPerformanceLubricantDenominator);
    }

    private int getOxygenConsumptionPerTick() {
        int oxygenPerTick = Config.lsbBaseOxygenConsumptionPerTick;
        if (!performanceMode) {
            return oxygenPerTick;
        }
        return scaleInt(oxygenPerTick, Config.lsbPerformanceOxygenNumerator, Config.lsbPerformanceOxygenDenominator);
    }

    private int applyTitaniumOutputMultiplier(int value) {
        return scaleInt(value, Config.lsbTitaniumOutputNumerator, Config.lsbTitaniumOutputDenominator);
    }

    private int applyPerformanceOutputMultiplier(int value) {
        return scaleInt(value, Config.lsbPerformanceOutputNumerator, Config.lsbPerformanceOutputDenominator);
    }

    private int applyPerformanceEfficiencyPenalty(int value) {
        if (!performanceMode) {
            return value;
        }
        return scaleInt(value, Config.lsbPerformanceEfficiencyNumerator, Config.lsbPerformanceEfficiencyDenominator);
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

    private static int calculateDisplayedOutput(int nominalOutput, int maxEfficiency) {
        return GTUtility.safeInt((long) nominalOutput * (long) maxEfficiency / 10000L);
    }

    private static int scaleInt(int value, int numerator, int denominator) {
        return Math.max(1, GTUtility.safeInt((long) value * numerator / Math.max(1, denominator)));
    }

    private static IStructureDefinition<MTELargeSemifluidGenerator> createStructureDefinition() {
        return StructureDefinition.<MTELargeSemifluidGenerator>builder()
            .addShape(
                "main",
                transpose(
                    new String[][] { { "III", "CCC", "CCC", "CCC" }, { "I~I", "CGC", "CGC", "CMC" },
                        { "III", "CCC", "CCC", "CCC" } }))
            .addElement(
                'C',
                lazy(
                    mte -> buildHatchAdder(MTELargeSemifluidGenerator.class)
                        .atLeast(HatchElement.InputHatch, HatchElement.InputHatch, HatchElement.Maintenance)
                        .casingIndex(mte.getCasingTextureIndex())
                        .dot(1)
                        .buildAndChain(
                            onElementPass(
                                machine -> ((PatchedMTELargeSemifluidGenerator) machine).incrementCasingCount(),
                                ofBlock(mte.getCasingBlock(), mte.getCasingMeta())))))
            .addElement(
                'G',
                lazy(
                    mte -> ofChain(
                        onElementPass(
                            machine -> ((PatchedMTELargeSemifluidGenerator) machine).recordSteelGearbox(),
                            ofBlock(mte.getGearboxBlock(), mte.getGearboxMeta())),
                        onElementPass(
                            machine -> ((PatchedMTELargeSemifluidGenerator) machine).recordTitaniumGearbox(),
                            ofBlock(mte.getGearboxBlock(), TITANIUM_GEARBOX_META)))))
            .addElement('I', lazy(mte -> ofBlock(mte.getIntakeBlock(), mte.getIntakeMeta())))
            .addElement(
                'M',
                lazy(
                    mte -> HatchElement.Dynamo.withMteClasses(DYNAMO_HATCH_TYPES)
                        .or(GTPPMultiBlockBase.GTPPHatchElement.TTDynamo)
                        .newAny(mte.getCasingTextureIndex(), 2)))
            .build();
    }
}
