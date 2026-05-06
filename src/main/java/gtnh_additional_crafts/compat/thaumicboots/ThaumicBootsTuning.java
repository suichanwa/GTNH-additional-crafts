package gtnh_additional_crafts.compat.thaumicboots;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;

import thaumicboots.api.IBoots;
import thaumicboots.api.ITBootSpeed;
import thaumicboots.mixins.early.minecraft.EntityLivingBaseAccessor;

public final class ThaumicBootsTuning {

    public static final String TAG_AXIS_FORWARD = "gtnhac_axis_forward";
    public static final String TAG_AXIS_STRAFE = "gtnhac_axis_strafe";
    public static final String TAG_VOIDWALKER_TRAVELER_MOVEMENT = "gtnhac_voidwalker_traveler_movement";
    private static final String QUANTUM_VOIDWALKER_CLASS_NAME = "thaumicboots.item.boots.voidwalker.ItemQuantumVoidwalkerBoots";
    private static final String QUANTUM_VOIDWALKER_KEYWORD = "itemquantumvoid";
    private static final double DEFAULT_AXIS_MULTIPLIER = 1.0D;
    private static final double MIN_AXIS_MULTIPLIER = 0.0D;
    private static final double MAX_AXIS_MULTIPLIER = 1.0D;
    private static final float VOIDWALKER_SASH_SPEED_BONUS = 0.4F;
    private static final String ADVANCED_NANO_CHESTPLATE_SUFFIX = "advNanoChestPlate";
    private static final String TAG_NANO_FLY = "isFlyActive";
    private static final String TAG_NANO_HOVER = "isHoverActive";

    private ThaumicBootsTuning() {}

    public static double getForwardMultiplier(ItemStack bootsStack) {
        return getAxisMultiplier(bootsStack, TAG_AXIS_FORWARD);
    }

    public static double getStrafeMultiplier(ItemStack bootsStack) {
        return getAxisMultiplier(bootsStack, TAG_AXIS_STRAFE);
    }

    public static void setForwardMultiplier(ItemStack bootsStack, double value) {
        setAxisMultiplier(bootsStack, TAG_AXIS_FORWARD, value);
    }

    public static void setStrafeMultiplier(ItemStack bootsStack, double value) {
        setAxisMultiplier(bootsStack, TAG_AXIS_STRAFE, value);
    }

    public static boolean isVoidwalkerTravelerMovementEnabled(ItemStack bootsStack) {
        if (!isQuantumVoidwalkerBoots(bootsStack)) {
            return false;
        }
        NBTTagCompound tag = bootsStack.getTagCompound();
        return tag != null && tag.getBoolean(TAG_VOIDWALKER_TRAVELER_MOVEMENT);
    }

    public static void setVoidwalkerTravelerMovementEnabled(ItemStack bootsStack, boolean enabled) {
        if (!isQuantumVoidwalkerBoots(bootsStack)) {
            return;
        }
        NBTTagCompound tag = bootsStack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            bootsStack.setTagCompound(tag);
        }
        tag.setBoolean(TAG_VOIDWALKER_TRAVELER_MOVEMENT, enabled);
    }

    public static boolean isQuantumVoidwalkerBoots(ItemStack stack) {
        if (stack == null || stack.getItem() == null) {
            return false;
        }

        Item item = stack.getItem();
        if (QUANTUM_VOIDWALKER_CLASS_NAME.equals(
            item.getClass()
                .getName())) {
            return true;
        }

        String unlocalizedName = item.getUnlocalizedName(stack);
        return unlocalizedName != null && unlocalizedName.toLowerCase()
            .contains(QUANTUM_VOIDWALKER_KEYWORD);
    }

    public static void applyVoidwalkerEmtTravelerMovement(EntityPlayer player, ItemStack bootsStack) {
        if (player == null || !isVoidwalkerTravelerMovementEnabled(bootsStack)) {
            return;
        }

        Item item = bootsStack.getItem();
        if (!(item instanceof IBoots) || !(item instanceof ITBootSpeed)) {
            return;
        }

        double speedMode = ((IBoots) item).isSpeedEnabled(bootsStack);
        if (speedMode <= 0.0D) {
            return;
        }

        boolean activeNanoFlight = hasActiveAdvancedNanoFlight(player);
        float movement = ((ITBootSpeed) item).getSpeedModifier() + getVoidwalkerSashSpeedBonus(player);
        if (player.capabilities.isFlying || activeNanoFlight) {
            movement *= 0.75F;
        }
        movement *= (float) speedMode;

        boolean omniEnabled = ((IBoots) item).isOmniEnabled(bootsStack);
        if (omniEnabled) {
            undoThaumicBootsVerticalOmni(player, movement);
        }

        applyEmtHorizontalDampening(player, movement, omniEnabled, activeNanoFlight);
    }

    public static void applyAxisMultipliers(EntityPlayer player, ItemStack bootsStack) {
        double forwardMultiplier = getForwardMultiplier(bootsStack);
        double strafeMultiplier = getStrafeMultiplier(bootsStack);

        if (isNearly(forwardMultiplier, DEFAULT_AXIS_MULTIPLIER)
            && isNearly(strafeMultiplier, DEFAULT_AXIS_MULTIPLIER)) {
            return;
        }

        if (player.moveForward != 0.0F || player.moveStrafing != 0.0F) {
            applyHorizontalMultipliers(player, forwardMultiplier, strafeMultiplier);
        }
    }

    private static void applyHorizontalMultipliers(EntityPlayer player, double forwardMultiplier,
        double strafeMultiplier) {
        float yawRadians = player.rotationYaw * (float) Math.PI / 180.0F;
        double sin = Math.sin(yawRadians);
        double cos = Math.cos(yawRadians);

        double forwardComponent = -sin * player.motionX + cos * player.motionZ;
        double strafeComponent = cos * player.motionX + sin * player.motionZ;

        forwardComponent *= forwardMultiplier;
        strafeComponent *= strafeMultiplier;

        player.motionX = -sin * forwardComponent + cos * strafeComponent;
        player.motionZ = cos * forwardComponent + sin * strafeComponent;
    }

    private static double getAxisMultiplier(ItemStack bootsStack, String key) {
        if (bootsStack == null) {
            return DEFAULT_AXIS_MULTIPLIER;
        }
        NBTTagCompound tag = bootsStack.getTagCompound();
        if (tag == null || !tag.hasKey(key)) {
            return DEFAULT_AXIS_MULTIPLIER;
        }
        return clampAxisMultiplier(tag.getDouble(key));
    }

    private static void setAxisMultiplier(ItemStack bootsStack, String key, double value) {
        if (bootsStack == null) {
            return;
        }
        NBTTagCompound tag = bootsStack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            bootsStack.setTagCompound(tag);
        }
        tag.setDouble(key, clampAxisMultiplier(value));
    }

    public static double clampAxisMultiplier(double value) {
        if (value < MIN_AXIS_MULTIPLIER) {
            return MIN_AXIS_MULTIPLIER;
        }
        if (value > MAX_AXIS_MULTIPLIER) {
            return MAX_AXIS_MULTIPLIER;
        }
        return value;
    }

    private static boolean isNearly(double left, double right) {
        return Math.abs(left - right) <= 0.00001D;
    }

    private static float getVoidwalkerSashSpeedBonus(EntityPlayer player) {
        ItemStack sashStack = VoidwalkerSashTuning.getEquippedVoidwalkerSash(player);
        return VoidwalkerSashTuning.hasSpeedBoostEnabled(sashStack) ? VOIDWALKER_SASH_SPEED_BONUS : 0.0F;
    }

    private static void undoThaumicBootsVerticalOmni(EntityPlayer player, float movement) {
        if (player.motionY == 0.0D) {
            return;
        }

        boolean jumping = ((EntityLivingBaseAccessor) player).getIsJumping();
        boolean sneaking = player.isSneaking();
        if (sneaking && !jumping && !player.onGround) {
            player.motionY += movement;
        } else if (jumping && !sneaking) {
            player.motionY -= movement;
        }
    }

    private static void applyEmtHorizontalDampening(EntityPlayer player, float movement, boolean omniEnabled,
        boolean activeNanoFlight) {
        if (!canApplyHorizontalMovement(player, activeNanoFlight)) {
            return;
        }

        float emtMovement = movement;
        if (player.isInWater()) {
            emtMovement *= 0.25F;
        }
        if (player.isSneaking()) {
            emtMovement *= 0.5F;
        }

        float excessMovement = movement - emtMovement;
        if (excessMovement <= 0.0001F) {
            return;
        }

        if (player.moveForward != 0.0F) {
            subtractMoveFlying(player, 0.0F, player.moveForward, excessMovement);
        }
        if (omniEnabled && player.moveStrafing != 0.0F) {
            subtractMoveFlying(player, player.moveStrafing, 0.0F, excessMovement);
        }
    }

    private static boolean canApplyHorizontalMovement(EntityPlayer player, boolean activeNanoFlight) {
        return player.onGround || player.isOnLadder() || player.capabilities.isFlying || activeNanoFlight;
    }

    private static void subtractMoveFlying(EntityPlayer player, float strafe, float forward, float movement) {
        float magnitude = strafe * strafe + forward * forward;
        if (magnitude < 0.0001F) {
            return;
        }

        magnitude = MathHelper.sqrt_float(magnitude);
        if (magnitude < 1.0F) {
            magnitude = 1.0F;
        }

        float scale = -movement / magnitude;
        strafe *= scale;
        forward *= scale;
        float yawRadians = player.rotationYaw * (float) Math.PI / 180.0F;
        float sin = MathHelper.sin(yawRadians);
        float cos = MathHelper.cos(yawRadians);
        player.motionX += strafe * cos - forward * sin;
        player.motionZ += forward * cos + strafe * sin;
    }

    private static boolean hasActiveAdvancedNanoFlight(EntityPlayer player) {
        ItemStack chestStack = player.inventory.armorItemInSlot(2);
        if (chestStack == null || chestStack.getItem() == null) {
            return false;
        }
        String name = chestStack.getItem()
            .getUnlocalizedName();
        if (name == null || !name.endsWith(ADVANCED_NANO_CHESTPLATE_SUFFIX)) {
            return false;
        }
        NBTTagCompound tag = chestStack.getTagCompound();
        return tag != null && tag.getBoolean(TAG_NANO_FLY) && tag.getBoolean(TAG_NANO_HOVER);
    }
}
