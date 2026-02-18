package com.myname.mymodid.compat.gregtech;

import com.myname.mymodid.MyMod;

import gregtech.api.GregTechAPI;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.common.tileentities.machines.multi.MTEDieselEngine;

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
            if (existing.getClass() != MTEDieselEngine.class) {
                continue;
            }

            String metaName = existing.getMetaName();
            String localName = existing.getLocalName();

            GregTechAPI.METATILEENTITIES[i] = null;
            try {
                new PatchedMTEDieselEngine(i, metaName, localName);
                patchedCount++;
                MyMod.logInfo("Patched LCE MetaTileEntity " + i + " to allow Dinitrogen Tetroxide oxidizer.");
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
