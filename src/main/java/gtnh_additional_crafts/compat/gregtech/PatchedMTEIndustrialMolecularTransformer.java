package gtnh_additional_crafts.compat.gregtech;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.ExoticEnergy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.Muffler;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.IAlignmentLimits;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;

import gregtech.api.GregTechAPI;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gtPlusPlus.core.block.ModBlocks;
import gtPlusPlus.xmod.gregtech.common.tileentities.machines.multi.processing.MTEIndustrialMolecularTransformer;

public class PatchedMTEIndustrialMolecularTransformer extends MTEIndustrialMolecularTransformer {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static IStructureDefinition<MTEIndustrialMolecularTransformer> STRUCTURE_DEFINITION = null;
    private int casingCount = 0;

    public PatchedMTEIndustrialMolecularTransformer(int id, String name, String nameRegional) {
        super(id, name, nameRegional);
    }

    public PatchedMTEIndustrialMolecularTransformer(String name) {
        super(name);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new PatchedMTEIndustrialMolecularTransformer(super.mName);
    }

    @Override
    public IStructureDefinition<MTEIndustrialMolecularTransformer> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<MTEIndustrialMolecularTransformer>builder()
                .addShape(
                    STRUCTURE_PIECE_MAIN,
                    (new String[][] { { "       ", "       ", "  xxx  ", "  x~x  ", "  xxx  ", "       ", "       " },
                        { "       ", "  xxx  ", " xyyyx ", " xyzyx ", " xyyyx ", "  xxx  ", "       " },
                        { "       ", "  xxx  ", " xyyyx ", " xyzyx ", " xyyyx ", "  xxx  ", "       " },
                        { "       ", "  xxx  ", " xyyyx ", " xyzyx ", " xyyyx ", "  xxx  ", "       " },
                        { "   t   ", " ttxtt ", " tyyyt ", "txyzyxt", " tyyyt ", " ttxtt ", "   t   " },
                        { "   c   ", " ccecc ", " cxfxc ", "cefefec", " cxfxc ", " ccecc ", "   c   " },
                        { "   h   ", " hhhhh ", " hhhhh ", "hhhhhhh", " hhhhh ", " hhhhh ", "   h   " }, }))
                .addElement('x', ofBlock(getCasingBlock(), getCasingMeta()))
                .addElement('y', ofBlock(getCasingBlock(), getCasingMeta2()))
                .addElement('z', ofBlock(getCasingBlock(), getCasingMeta3()))
                .addElement('e', ofBlock(getCasingBlock2(), 0))
                .addElement('f', ofBlock(getCasingBlock2(), 4))
                .addElement('c', ofBlock(getCoilBlock(), 3))
                .addElement('t', ofBlock(getCasingBlock3(), getTungstenCasingMeta()))
                .addElement(
                    'h',
                    buildHatchAdder(MTEIndustrialMolecularTransformer.class)
                        .atLeast(InputBus, OutputBus, Maintenance, Energy.or(ExoticEnergy), Muffler)
                        .casingIndex(getCasingTextureIndex())
                        .dot(1)
                        .buildAndChain(onElementPass(machine -> {
                            if (machine instanceof PatchedMTEIndustrialMolecularTransformer patched) {
                                patched.casingCount++;
                            }
                        }, ofBlock(getCasingBlock3(), getTungstenCasingMeta()))))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity baseMetaTileEntity, ItemStack stack) {
        casingCount = 0;
        boolean didBuild = checkPiece(STRUCTURE_PIECE_MAIN, 3, 3, 0);
        if (this.mOutputBusses.size() != 1 || this.getExoticAndNormalEnergyHatchList()
            .isEmpty()) {
            return false;
        }
        return didBuild && casingCount >= 40 - 16 && checkHatch();
    }

    @Override
    protected IAlignmentLimits getInitialAlignmentLimits() {
        return (d, r, f) -> d == ForgeDirection.UP;
    }

    protected static int getCasingTextureIndex() {
        return 48;
    }

    protected static Block getCasingBlock() {
        return ModBlocks.blockSpecialMultiCasings;
    }

    protected static Block getCasingBlock2() {
        return ModBlocks.blockSpecialMultiCasings2;
    }

    protected static Block getCasingBlock3() {
        return GregTechAPI.sBlockCasings4;
    }

    protected static Block getCoilBlock() {
        return GregTechAPI.sBlockCasings5;
    }

    protected static int getCasingMeta() {
        return 11;
    }

    protected static int getCasingMeta2() {
        return 12;
    }

    protected static int getCasingMeta3() {
        return 13;
    }

    protected static int getTungstenCasingMeta() {
        return 0;
    }
}
