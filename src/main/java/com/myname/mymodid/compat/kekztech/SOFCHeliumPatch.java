package com.myname.mymodid.compat.kekztech;

import com.myname.mymodid.MyMod;

import gregtech.api.GregTechAPI;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import kekztech.common.tileentities.MTESOFuelCellMK1;

public final class SOFCHeliumPatch {

    private static final int SOFC_MK1_META_TILE_ID = 13101;

    private static boolean patchScheduled = false;
    private static boolean patchApplied = false;

    private SOFCHeliumPatch() {}

    public static void schedule() {
        if (patchScheduled || patchApplied) {
            return;
        }
        patchScheduled = true;

        if (GregTechAPI.sLoadFinished) {
            apply();
            return;
        }

        GregTechAPI.sAfterGTLoad.add(SOFCHeliumPatch::apply);
        MyMod.logInfo("Scheduled SOFC MK1 helium patch for GT after-load phase.");
    }

    private static void apply() {
        if (patchApplied) {
            return;
        }

        IMetaTileEntity existing = GregTechAPI.METATILEENTITIES[SOFC_MK1_META_TILE_ID];
        if (!(existing instanceof MTESOFuelCellMK1)) {
            MyMod.logInfo(
                "Skipped SOFC MK1 helium patch: MetaTileEntity "
                    + SOFC_MK1_META_TILE_ID + " is not MTESOFuelCellMK1.");
            return;
        }

        String metaName = existing.getMetaName();
        String localName = existing.getLocalName();

        GregTechAPI.METATILEENTITIES[SOFC_MK1_META_TILE_ID] = null;
        try {
            new PatchedMTESOFuelCellMK1(SOFC_MK1_META_TILE_ID, metaName, localName);
            patchApplied = true;
            MyMod.logInfo("Applied SOFC MK1 helium patch to MetaTileEntity " + SOFC_MK1_META_TILE_ID + ".");
        } catch (RuntimeException e) {
            GregTechAPI.METATILEENTITIES[SOFC_MK1_META_TILE_ID] = existing;
            throw e;
        }
    }
}
