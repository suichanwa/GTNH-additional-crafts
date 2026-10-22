package gtnh_additional_crafts.patches.gregtech;

import gtnh_additional_crafts.MyMod;

public final class MolecularTransformerEnergyHatchPatch {

    private static boolean patchApplied = false;

    private MolecularTransformerEnergyHatchPatch() {}

    public static void schedule() {
        if (patchApplied) {
            return;
        }
        patchApplied = true;
        MyMod.logInfo("Industrial Molecular Transformer already natively supports multiple energy hatches in GTNH 2.9.");
    }

    public static void applyIfNeeded() {
        if (patchApplied) {
            return;
        }
        patchApplied = true;
        MyMod.logInfo("Industrial Molecular Transformer already natively supports multiple energy hatches in GTNH 2.9.");
    }
}
