package gtnh_additional_crafts.client.config;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import cpw.mods.fml.client.config.GuiSlider;
import gtnh_additional_crafts.MyMod;
import gtnh_additional_crafts.network.BootsControlMessage;
import gtnh_additional_crafts.patches.thaumicboots.ThaumicBootsTuningPatch;
import gtnh_additional_crafts.patches.thaumicboots.VoidwalkerSashTuningPatch;
import thaumicboots.api.IBoots;
import thaumicboots.api.ITBootJumpable;
import thaumicboots.api.ITBootSpeed;

public class GuiThaumicBootsControls extends GuiScreen implements GuiSlider.ISlider {

    private static final int ID_SPEED = 1;
    private static final int ID_JUMP = 2;
    private static final int ID_TOGGLE_OMNI = 10;
    private static final int ID_TOGGLE_STEP = 11;
    private static final int ID_TOGGLE_INERTIA = 12;
    private static final int ID_DEFAULTS = 13;
    private static final int ID_DONE = 14;
    private static final int ID_TOGGLE_SASH_SPEED = 15;
    private static final int ID_TOGGLE_VOIDWALKER_TRAVELER = 16;
    private static final int ID_AXIS_FORWARD = 20;
    private static final int ID_AXIS_STRAFE = 21;
    private static final long SEND_COOLDOWN_MS = 75L;

    private GuiSlider speedSlider;
    private GuiSlider jumpSlider;
    private GuiSlider forwardAxisSlider;
    private GuiSlider strafeAxisSlider;
    private GuiButton omniButton;
    private GuiButton stepButton;
    private GuiButton inertiaButton;
    private GuiButton sashSpeedButton;
    private GuiButton voidwalkerTravelerButton;
    private GuiButton defaultsButton;
    private boolean omniEnabled;
    private boolean stepEnabled;
    private boolean inertiaEnabled;
    private boolean hasBootsEquipped;
    private boolean hasQuantumVoidwalkerBootsEquipped;
    private boolean hasVoidwalkerSashEquipped;
    private String equippedBootsName = "";
    private String equippedSashName = "";
    private double baseBootSpeedModifier;
    private double baseBootJumpModifier;
    private double speedModeMultiplier = 1.0D;
    private double jumpModeMultiplier = 1.0D;
    private boolean voidwalkerSashSpeedEnabled = true;
    private boolean voidwalkerTravelerMovementEnabled;
    private long lastSentAtMs;

    @Override
    public void initGui() {
        buttonList.clear();
        loadStateFromEquippedBoots();

        int centerX = width / 2;
        int left = centerX - 110;
        int y = height / 2 - 110;

        speedSlider = new GuiSlider(
            ID_SPEED,
            left,
            y,
            220,
            20,
            "Speed Boost: ",
            "%",
            0.0D,
            100.0D,
            getSpeedPercent(),
            false,
            true,
            this);
        jumpSlider = new GuiSlider(
            ID_JUMP,
            left,
            y + 24,
            220,
            20,
            "Jump Boost: ",
            "%",
            0.0D,
            100.0D,
            getJumpPercent(),
            false,
            true,
            this);
        forwardAxisSlider = new GuiSlider(
            ID_AXIS_FORWARD,
            left,
            y + 52,
            220,
            20,
            "Forward Axis: ",
            "%",
            0.0D,
            100.0D,
            getForwardAxisPercent(),
            false,
            true,
            this);
        strafeAxisSlider = new GuiSlider(
            ID_AXIS_STRAFE,
            left,
            y + 76,
            220,
            20,
            "Strafe Axis: ",
            "%",
            0.0D,
            100.0D,
            getStrafeAxisPercent(),
            false,
            true,
            this);
        buttonList.add(speedSlider);
        buttonList.add(jumpSlider);
        buttonList.add(forwardAxisSlider);
        buttonList.add(strafeAxisSlider);

        omniButton = new GuiButton(ID_TOGGLE_OMNI, left, y + 128, 106, 20, "");
        stepButton = new GuiButton(ID_TOGGLE_STEP, left + 114, y + 128, 106, 20, "");
        inertiaButton = new GuiButton(ID_TOGGLE_INERTIA, left, y + 152, 220, 20, "");
        sashSpeedButton = new GuiButton(ID_TOGGLE_SASH_SPEED, left, y + 176, 220, 20, "");
        voidwalkerTravelerButton = new GuiButton(ID_TOGGLE_VOIDWALKER_TRAVELER, left, y + 200, 220, 20, "");
        defaultsButton = new GuiButton(ID_DEFAULTS, left, y + 230, 106, 20, "Defaults");
        GuiButton doneButton = new GuiButton(ID_DONE, left + 114, y + 230, 106, 20, "Done");
        buttonList.add(omniButton);
        buttonList.add(stepButton);
        buttonList.add(inertiaButton);
        buttonList.add(sashSpeedButton);
        buttonList.add(voidwalkerTravelerButton);
        buttonList.add(defaultsButton);
        buttonList.add(doneButton);

        updateToggleButtonLabels();
        updateControlState();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case ID_TOGGLE_OMNI:
                omniEnabled = !omniEnabled;
                updateToggleButtonLabels();
                pushStateToServer(true);
                break;
            case ID_TOGGLE_STEP:
                stepEnabled = !stepEnabled;
                updateToggleButtonLabels();
                pushStateToServer(true);
                break;
            case ID_TOGGLE_INERTIA:
                inertiaEnabled = !inertiaEnabled;
                updateToggleButtonLabels();
                pushStateToServer(true);
                break;
            case ID_DEFAULTS:
                speedSlider.setValue(100.0D);
                jumpSlider.setValue(100.0D);
                forwardAxisSlider.setValue(100.0D);
                strafeAxisSlider.setValue(100.0D);
                omniEnabled = true;
                stepEnabled = true;
                inertiaEnabled = false;
                voidwalkerSashSpeedEnabled = true;
                voidwalkerTravelerMovementEnabled = false;
                updateToggleButtonLabels();
                pushStateToServer(true);
                break;
            case ID_TOGGLE_SASH_SPEED:
                voidwalkerSashSpeedEnabled = !voidwalkerSashSpeedEnabled;
                updateToggleButtonLabels();
                pushStateToServer(true);
                break;
            case ID_TOGGLE_VOIDWALKER_TRAVELER:
                voidwalkerTravelerMovementEnabled = !voidwalkerTravelerMovementEnabled;
                updateToggleButtonLabels();
                pushStateToServer(true);
                break;
            case ID_DONE:
                mc.displayGuiScreen(null);
                break;
            default:
                break;
        }
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider) {
        pushStateToServer(false);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(fontRendererObj, "Thaumic Boots Live Controls", width / 2, 16, 0xFFFFFF);

        String equippedText = hasBootsEquipped
            ? EnumChatFormatting.GREEN + "Equipped: " + EnumChatFormatting.RESET + equippedBootsName
            : EnumChatFormatting.RED + "No Thaumic Boots equipped";
        drawCenteredString(fontRendererObj, equippedText, width / 2, 30, 0xFFFFFF);

        String bootsStats = hasBootsEquipped ? String
            .format("Base pair stats: Speed +%.1f%%, Jump +%.2f", baseBootSpeedModifier * 100.0D, baseBootJumpModifier)
            : "Base pair stats: n/a";
        drawCenteredString(fontRendererObj, bootsStats, width / 2, 42, 0xA0A0A0);

        String modeStats = hasBootsEquipped
            ? String.format("Current modes: Speed x%.2f, Jump x%.2f", speedModeMultiplier, jumpModeMultiplier)
            : "Current modes: n/a";
        drawCenteredString(fontRendererObj, modeStats, width / 2, 52, 0xA0A0A0);

        String sashStats = hasVoidwalkerSashEquipped
            ? EnumChatFormatting.LIGHT_PURPLE + "Sash: "
                + EnumChatFormatting.RESET
                + equippedSashName
                + " | Speed Boost "
                + (voidwalkerSashSpeedEnabled ? "ON" : "OFF")
            : "Sash: not equipped";
        drawCenteredString(fontRendererObj, sashStats, width / 2, 62, 0xA0A0A0);

        String hint = "Controls affect only currently equipped Thaumic Boots and Voidwalker Sash";
        drawCenteredString(fontRendererObj, hint, width / 2, 72, 0x909090);

        String voidwalkerModeStats = hasQuantumVoidwalkerBootsEquipped
            ? "Voidwalker movement: " + (voidwalkerTravelerMovementEnabled ? "EMT Traveler" : "Voidwalker")
            : "Voidwalker movement: requires Quantum Boots of the Voidwalker";
        drawCenteredString(fontRendererObj, voidwalkerModeStats, width / 2, 82, 0x909090);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void loadStateFromEquippedBoots() {
        ItemStack bootsStack = getEquippedBoots();
        hasBootsEquipped = bootsStack != null;
        if (!hasBootsEquipped) {
            equippedBootsName = "";
            equippedSashName = "";
            omniEnabled = false;
            stepEnabled = false;
            inertiaEnabled = false;
            baseBootSpeedModifier = 0.0D;
            baseBootJumpModifier = 0.0D;
            speedModeMultiplier = 1.0D;
            jumpModeMultiplier = 1.0D;
            hasQuantumVoidwalkerBootsEquipped = false;
            voidwalkerTravelerMovementEnabled = false;
        } else {

            equippedBootsName = bootsStack.getDisplayName();
            hasQuantumVoidwalkerBootsEquipped = ThaumicBootsTuningPatch.isQuantumVoidwalkerBoots(bootsStack);
            Item bootsItemRaw = bootsStack.getItem();
            IBoots bootsItem = (IBoots) bootsItemRaw;
            baseBootSpeedModifier = bootsItemRaw instanceof ITBootSpeed
                ? ((ITBootSpeed) bootsItemRaw).getSpeedModifier()
                : 0.0D;
            baseBootJumpModifier = bootsItemRaw instanceof ITBootJumpable
                ? ((ITBootJumpable) bootsItemRaw).getJumpModifier()
                : 0.0D;
            speedModeMultiplier = bootsItem.isSpeedEnabled(bootsStack);
            jumpModeMultiplier = IBoots.isJumpEnabled(bootsStack);
            omniEnabled = bootsItem.isOmniEnabled(bootsStack);
            stepEnabled = bootsItem.isStepEnabled(bootsStack);
            inertiaEnabled = bootsItem.isInertiaCanceled(bootsStack);
            voidwalkerTravelerMovementEnabled = ThaumicBootsTuningPatch.isVoidwalkerTravelerMovementEnabled(bootsStack);
        }

        ItemStack sashStack = mc != null && mc.thePlayer != null
            ? VoidwalkerSashTuningPatch.getEquippedVoidwalkerSash(mc.thePlayer)
            : null;
        hasVoidwalkerSashEquipped = sashStack != null;
        if (hasVoidwalkerSashEquipped) {
            equippedSashName = sashStack.getDisplayName();
            voidwalkerSashSpeedEnabled = VoidwalkerSashTuningPatch.hasSpeedBoostEnabled(sashStack);
        } else {
            equippedSashName = "";
            voidwalkerSashSpeedEnabled = true;
        }
    }

    private void updateToggleButtonLabels() {
        omniButton.displayString = labelForToggle("Omni Move", omniEnabled);
        stepButton.displayString = labelForToggle("Step Assist", stepEnabled);
        inertiaButton.displayString = labelForToggle("Inertia Cancel", inertiaEnabled);
        sashSpeedButton.displayString = labelForToggle("Sash Speed Boost", voidwalkerSashSpeedEnabled);
        voidwalkerTravelerButton.displayString = voidwalkerTravelerMovementEnabled ? "Voidwalker Movement: EMT TRAVELER"
            : "Voidwalker Movement: VOIDWALKER";
    }

    private void updateControlState() {
        speedSlider.enabled = hasBootsEquipped;
        jumpSlider.enabled = hasBootsEquipped;
        forwardAxisSlider.enabled = hasBootsEquipped;
        strafeAxisSlider.enabled = hasBootsEquipped;
        omniButton.enabled = hasBootsEquipped;
        stepButton.enabled = hasBootsEquipped;
        inertiaButton.enabled = hasBootsEquipped;
        sashSpeedButton.enabled = hasVoidwalkerSashEquipped;
        voidwalkerTravelerButton.enabled = hasQuantumVoidwalkerBootsEquipped;
        defaultsButton.enabled = hasBootsEquipped;
    }

    private String labelForToggle(String name, boolean enabled) {
        return enabled ? name + ": ON" : name + ": OFF";
    }

    private int getSpeedPercent() {
        ItemStack bootsStack = getEquippedBoots();
        if (bootsStack == null) {
            return 100;
        }
        IBoots bootsItem = (IBoots) bootsStack.getItem();
        return (int) Math.round(clamp01(bootsItem.isSpeedEnabled(bootsStack)) * 100.0D);
    }

    private int getJumpPercent() {
        ItemStack bootsStack = getEquippedBoots();
        if (bootsStack == null) {
            return 100;
        }
        return (int) Math.round(clamp01(IBoots.isJumpEnabled(bootsStack)) * 100.0D);
    }

    private int getForwardAxisPercent() {
        ItemStack bootsStack = getEquippedBoots();
        if (bootsStack == null) {
            return 100;
        }
        return (int) Math.round(ThaumicBootsTuningPatch.getForwardMultiplier(bootsStack) * 100.0D);
    }

    private int getStrafeAxisPercent() {
        ItemStack bootsStack = getEquippedBoots();
        if (bootsStack == null) {
            return 100;
        }
        return (int) Math.round(ThaumicBootsTuningPatch.getStrafeMultiplier(bootsStack) * 100.0D);
    }

    private ItemStack getEquippedBoots() {
        if (mc == null || mc.thePlayer == null) {
            return null;
        }
        return IBoots.getBoots(mc.thePlayer);
    }

    private void pushStateToServer(boolean force) {
        if (!hasBootsEquipped && !hasVoidwalkerSashEquipped) {
            return;
        }
        long now = System.currentTimeMillis();
        if (!force && now - lastSentAtMs < SEND_COOLDOWN_MS) {
            return;
        }
        lastSentAtMs = now;

        double speed = clamp01(speedSlider.getValue() / 100.0D);
        double jump = clamp01(jumpSlider.getValue() / 100.0D);
        double forwardAxis = ThaumicBootsTuningPatch.clampAxisMultiplier(forwardAxisSlider.getValue() / 100.0D);
        double strafeAxis = ThaumicBootsTuningPatch.clampAxisMultiplier(strafeAxisSlider.getValue() / 100.0D);
        applyStateToLocalBoots(speed, jump, forwardAxis, strafeAxis);
        speedModeMultiplier = speed;
        jumpModeMultiplier = jump;
        MyMod.NETWORK.sendToServer(
            new BootsControlMessage(
                speed,
                jump,
                omniEnabled,
                stepEnabled,
                inertiaEnabled,
                forwardAxis,
                strafeAxis,
                hasVoidwalkerSashEquipped,
                voidwalkerSashSpeedEnabled,
                hasQuantumVoidwalkerBootsEquipped,
                voidwalkerTravelerMovementEnabled));
    }

    private void applyStateToLocalBoots(double speed, double jump, double forwardAxis, double strafeAxis) {
        ItemStack bootsStack = getEquippedBoots();
        if (bootsStack != null) {
            Item bootsItemRaw = bootsStack.getItem();
            if (bootsItemRaw instanceof IBoots) {
                IBoots bootsItem = (IBoots) bootsItemRaw;
                bootsItem.setModeSpeed(bootsStack, speed);
                bootsItem.setModeJump(bootsStack, jump);
                bootsItem.setModeOmni(bootsStack, omniEnabled);
                bootsItem.setModeStep(bootsStack, stepEnabled);
                bootsItem.setIsInertiaCanceling(bootsStack, inertiaEnabled);
                ThaumicBootsTuningPatch.setForwardMultiplier(bootsStack, forwardAxis);
                ThaumicBootsTuningPatch.setStrafeMultiplier(bootsStack, strafeAxis);
                ThaumicBootsTuningPatch
                    .setVoidwalkerTravelerMovementEnabled(bootsStack, voidwalkerTravelerMovementEnabled);
            }
        }

        if (mc == null || mc.thePlayer == null) {
            return;
        }
        ItemStack sashStack = VoidwalkerSashTuningPatch.getEquippedVoidwalkerSash(mc.thePlayer);
        if (sashStack != null) {
            VoidwalkerSashTuningPatch.setSpeedBoostEnabled(sashStack, voidwalkerSashSpeedEnabled);
        }
    }

    private static double clamp01(double value) {
        if (value < 0.0D) {
            return 0.0D;
        }
        if (value > 1.0D) {
            return 1.0D;
        }
        return value;
    }
}
