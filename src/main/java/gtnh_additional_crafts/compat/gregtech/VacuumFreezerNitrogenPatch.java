package gtnh_additional_crafts.compat.gregtech;

import gregtech.api.GregTechAPI;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.common.tileentities.machines.multi.MTEVacuumFreezer;
import gtnh_additional_crafts.MyMod;

public final class VacuumFreezerNitrogenPatch {

    private static boolean patchScheduled = false;
    private static boolean patchApplied = false;

    private VacuumFreezerNitrogenPatch() {}

    public static void schedule() {
        if (patchScheduled || patchApplied) {
            return;
        }
        patchScheduled = true;

        if (GregTechAPI.sLoadFinished) {
            apply();
            return;
        }

        GregTechAPI.sAfterGTLoad.add(VacuumFreezerNitrogenPatch::apply);
        MyMod.logInfo("Scheduled Vacuum Freezer nitrogen patch for GT after-load phase.");
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
            if (existing instanceof PatchedMTEVacuumFreezer) {
                continue;
            }
            if (existing.getClass() != MTEVacuumFreezer.class) {
                continue;
            }

            String metaName = existing.getMetaName();
            String localName = existing.getLocalName();

            GregTechAPI.METATILEENTITIES[i] = null;
            try {
                new PatchedMTEVacuumFreezer(i, metaName, localName);
                patchedCount++;
                MyMod.logInfo("Patched Vacuum Freezer MetaTileEntity " + i + " with nitrogen boost behavior.");
            } catch (RuntimeException e) {
                GregTechAPI.METATILEENTITIES[i] = existing;
                throw e;
            }
        }

        patchApplied = true;
        if (patchedCount == 0) {
            MyMod.logInfo("Skipped Vacuum Freezer nitrogen patch: MTEVacuumFreezer not found.");
            return;
        }

        MyMod.logInfo("Applied Vacuum Freezer nitrogen patch to " + patchedCount + " MetaTileEntity instance(s).");
    }
}
