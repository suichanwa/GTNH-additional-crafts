package com.myname.mymodid.compat.gregtech;

import com.myname.mymodid.MyMod;

import gregtech.api.GregTechAPI;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gtPlusPlus.xmod.gregtech.common.tileentities.machines.multi.production.MTELargeSemifluidGenerator;

public final class LargeSemifluidGeneratorPatch {

    private static boolean patchScheduled = false;
    private static boolean patchApplied = false;

    private LargeSemifluidGeneratorPatch() {}

    public static void schedule() {
        if (patchApplied) {
            return;
        }
        if (!patchScheduled) {
            patchScheduled = true;
        }

        if (GregTechAPI.sLoadFinished) {
            apply();
            return;
        }

        GregTechAPI.sAfterGTLoad.add(LargeSemifluidGeneratorPatch::apply);
        MyMod.logInfo("Scheduled Large Semifluid Burner patch for GT after-load phase.");
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
            if (existing instanceof PatchedMTELargeSemifluidGenerator) {
                continue;
            }
            if (!(existing instanceof MTELargeSemifluidGenerator)) {
                continue;
            }

            String metaName = existing.getMetaName();
            String localName = existing.getLocalName();

            GregTechAPI.METATILEENTITIES[i] = null;
            try {
                new PatchedMTELargeSemifluidGenerator(i, metaName, localName);
                patchedCount++;
                MyMod.logInfo("Patched Large Semifluid Burner MetaTileEntity " + i + ".");
            } catch (RuntimeException e) {
                GregTechAPI.METATILEENTITIES[i] = existing;
                throw e;
            }
        }

        if (patchedCount == 0) {
            MyMod.logInfo("Skipped Large Semifluid Burner patch: MTELargeSemifluidGenerator not found.");
            return;
        }

        patchApplied = true;
        MyMod.logInfo("Applied Large Semifluid Burner patch to " + patchedCount + " MetaTileEntity instance(s).");
    }

    public static void applyIfNeeded() {
        if (patchApplied) {
            return;
        }
        apply();
    }
}
