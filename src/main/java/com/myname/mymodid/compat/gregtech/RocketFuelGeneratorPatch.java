package com.myname.mymodid.compat.gregtech;

import com.myname.mymodid.MyMod;

import gregtech.api.GregTechAPI;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gtPlusPlus.xmod.gregtech.common.tileentities.generators.MTERocketFuelGenerator;

public final class RocketFuelGeneratorPatch {

    private static boolean patchScheduled = false;
    private static boolean patchApplied = false;

    private RocketFuelGeneratorPatch() {}

    public static void schedule() {
        if (patchScheduled || patchApplied) {
            return;
        }
        patchScheduled = true;

        if (GregTechAPI.sLoadFinished) {
            apply();
            return;
        }

        GregTechAPI.sAfterGTLoad.add(RocketFuelGeneratorPatch::apply);
        MyMod.logInfo("Scheduled Rocket Fuel Generator patch for GT after-load phase.");
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
            if (existing instanceof PatchedMTERocketFuelGenerator) {
                continue;
            }
            if (existing.getClass() != MTERocketFuelGenerator.class) {
                continue;
            }

            String metaName = existing.getMetaName();
            String localName = existing.getLocalName();
            int tier = resolveTier(existing);
            if (tier < 0) {
                MyMod.logInfo("Skipped Rocket Fuel Generator patch at MetaTileEntity " + i + ": tier unavailable.");
                continue;
            }

            GregTechAPI.METATILEENTITIES[i] = null;
            try {
                new PatchedMTERocketFuelGenerator(i, metaName, localName, tier);
                patchedCount++;
                MyMod.logInfo("Patched Rocket Fuel Generator MetaTileEntity " + i + " with overclock toggle.");
            } catch (RuntimeException e) {
                GregTechAPI.METATILEENTITIES[i] = existing;
                throw e;
            }
        }

        patchApplied = true;
        if (patchedCount == 0) {
            MyMod.logInfo("Skipped Rocket Fuel Generator patch: MTERocketFuelGenerator not found.");
            return;
        }

        MyMod.logInfo("Applied Rocket Fuel Generator patch to " + patchedCount + " MetaTileEntity instance(s).");
    }

    private static int resolveTier(IMetaTileEntity metaTileEntity) {
        if (metaTileEntity == null) {
            return -1;
        }
        if (metaTileEntity instanceof MetaTileEntity) {
            try {
                java.lang.reflect.Field tierField = MetaTileEntity.class.getDeclaredField("mTier");
                tierField.setAccessible(true);
                return ((Number) tierField.get(metaTileEntity)).intValue();
            } catch (ReflectiveOperationException | ClassCastException e) {
                return -1;
            }
        }
        return -1;
    }
}
