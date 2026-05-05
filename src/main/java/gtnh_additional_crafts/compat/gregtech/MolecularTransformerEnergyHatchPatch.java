package gtnh_additional_crafts.compat.gregtech;

import gregtech.api.GregTechAPI;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gtPlusPlus.xmod.gregtech.common.tileentities.machines.multi.processing.MTEIndustrialMolecularTransformer;
import gtnh_additional_crafts.MyMod;

public final class MolecularTransformerEnergyHatchPatch {

    private static boolean patchScheduled = false;
    private static boolean patchApplied = false;

    private MolecularTransformerEnergyHatchPatch() {}

    public static void schedule() {
        if (patchScheduled || patchApplied) {
            return;
        }
        patchScheduled = true;

        if (GregTechAPI.sLoadFinished) {
            apply();
            return;
        }

        GregTechAPI.sAfterGTLoad.add(MolecularTransformerEnergyHatchPatch::apply);
        MyMod.logInfo("Scheduled Molecular Transformer multi-energy hatch patch for GT after-load phase.");
    }

    private static void apply() {
        if (patchApplied) {
            return;
        }

        int patchedCount = 0;
        for (int i = 0; i < GregTechAPI.METATILEENTITIES.length; i++) {
            IMetaTileEntity existing = GregTechAPI.METATILEENTITIES[i];
            if (existing == null) {
                continue;
            }
            if (existing instanceof PatchedMTEIndustrialMolecularTransformer) {
                continue;
            }
            if (existing.getClass() != MTEIndustrialMolecularTransformer.class) {
                continue;
            }

            String metaName = existing.getMetaName();
            String localName = existing.getLocalName();

            GregTechAPI.METATILEENTITIES[i] = null;
            try {
                new PatchedMTEIndustrialMolecularTransformer(i, metaName, localName);
                patchedCount++;
                MyMod.logInfo("Patched Molecular Transformer MetaTileEntity " + i + " for multi-energy hatch support.");
            } catch (RuntimeException e) {
                GregTechAPI.METATILEENTITIES[i] = existing;
                throw e;
            }
        }

        patchApplied = true;
        if (patchedCount == 0) {
            MyMod.logInfo("Skipped Molecular Transformer patch: MTEIndustrialMolecularTransformer not found.");
            return;
        }

        MyMod.logInfo(
            "Applied Molecular Transformer multi-energy hatch patch to " + patchedCount
                + " MetaTileEntity instance(s).");
    }
}
