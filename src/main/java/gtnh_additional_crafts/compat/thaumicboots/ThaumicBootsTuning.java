package gtnh_additional_crafts.compat.thaumicboots;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public final class ThaumicBootsTuning {

    public static final String TAG_AXIS_FORWARD = "gtnhac_axis_forward";
    public static final String TAG_AXIS_STRAFE = "gtnhac_axis_strafe";
    private static final double DEFAULT_AXIS_MULTIPLIER = 1.0D;
    private static final double MIN_AXIS_MULTIPLIER = 0.0D;
    private static final double MAX_AXIS_MULTIPLIER = 1.0D;

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
}
