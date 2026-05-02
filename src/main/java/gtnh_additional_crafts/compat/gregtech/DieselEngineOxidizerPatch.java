package gtnh_additional_crafts.compat.gregtech;

import gregtech.api.GregTechAPI;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.common.tileentities.machines.multi.MTEDieselEngine;
import gregtech.common.tileentities.machines.multi.MTEExtremeDieselEngine;
import gtnh_additional_crafts.MyMod;

public final class DieselEngineOxidizerPatch {

    private static boolean patchScheduled = false;
    private static boolean patchApplied = false;

    private DieselEngineOxidizerPatch() {}

    public static void schedule() {
        if (patchScheduled || patchApplied) {
            return;
        }
        patchScheduled = true;

        if (GregTechAPI.sLoadFinished) {
            apply();
            return;
        }

        GregTechAPI.sAfterGTLoad.add(DieselEngineOxidizerPatch::apply);
        MyMod.logInfo("Scheduled LCE oxidizer patch for GT after-load phase.");
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
            if (existing instanceof PatchedMTEDieselEngine) {
                continue;
            }
            if (existing instanceof PatchedMTEExtremeDieselEngine) {
                continue;
            }
            if (existing.getClass() != MTEDieselEngine.class && existing.getClass() != MTEExtremeDieselEngine.class) {
                continue;
            }

            String metaName = existing.getMetaName();
            String localName = existing.getLocalName();

            GregTechAPI.METATILEENTITIES[i] = null;
            try {
                if (existing.getClass() == MTEExtremeDieselEngine.class) {
                    new PatchedMTEExtremeDieselEngine(i, metaName, localName);
                    MyMod.logInfo("Patched ECE MetaTileEntity " + i + " for Cryonitrox/Liquid Oxygen boost support.");
                } else {
                    new PatchedMTEDieselEngine(i, metaName, localName);
                    MyMod.logInfo("Patched LCE MetaTileEntity " + i + " for Cryonitrox/Oxygen/N2O4 boost support.");
                }
                patchedCount++;
            } catch (RuntimeException e) {
                GregTechAPI.METATILEENTITIES[i] = existing;
                throw e;
            }
        }

        patchApplied = true;
        if (patchedCount == 0) {
            MyMod.logInfo("Skipped LCE oxidizer patch: MTEDieselEngine not found.");
            return;
        }

        MyMod.logInfo("Applied LCE oxidizer patch to " + patchedCount + " MetaTileEntity instance(s).");
    }
}
