package gtnh_additional_crafts.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.GTValues;
import gregtech.api.util.GTUtility;
import gtnh_additional_crafts.MyMod;
import ic2.api.item.IElectricItemManager;
import ic2.api.item.ISpecialElectricItem;

public class SodiumBatteryX16Item extends Item implements ISpecialElectricItem, IElectricItemManager {

    public static final String NAME = "sodium_battery_x16_iv";
    private static final String CHARGE_NBT_KEY = "GT.ItemCharge";
    private static final long MAX_CHARGE = 12_800_000L;
    private static final int TIER = 5;
    private static final long TRANSFER_LIMIT = GTValues.V[TIER];

    public SodiumBatteryX16Item() {
        setUnlocalizedName(MyMod.MODID + "." + NAME);
        setTextureName(MyMod.MODID + ":" + NAME);
        setCreativeTab(GregTechAPI.TAB_GREGTECH);
        setMaxStackSize(1);
        setNoRepair();
    }

    @Override
    public boolean canProvideEnergy(ItemStack stack) {
        return stack != null && stack.stackSize == 1;
    }

    @Override
    public Item getChargedItem(ItemStack stack) {
        return this;
    }

    @Override
    public Item getEmptyItem(ItemStack stack) {
        return this;
    }

    @Override
    public double getMaxCharge(ItemStack stack) {
        return MAX_CHARGE;
    }

    @Override
    public int getTier(ItemStack stack) {
        return TIER;
    }

    @Override
    public double getTransferLimit(ItemStack stack) {
        return TRANSFER_LIMIT;
    }

    @Override
    public IElectricItemManager getManager(ItemStack stack) {
        return this;
    }

    @Override
    public double charge(ItemStack stack, double charge, int tier, boolean ignoreTransferLimit, boolean simulate) {
        if (stack == null || stack.stackSize != 1 || tier < TIER || charge <= 0) {
            return 0;
        }
        long accepted = Math.min(MAX_CHARGE - getStoredCharge(stack), transferAmount(charge, ignoreTransferLimit));
        if (accepted <= 0) {
            return 0;
        }
        if (!simulate) {
            setStoredCharge(stack, getStoredCharge(stack) + accepted);
        }
        return accepted;
    }

    @Override
    public double discharge(ItemStack stack, double charge, int tier, boolean ignoreTransferLimit, boolean batteryAlike,
        boolean simulate) {
        if (stack == null || tier < TIER || charge <= 0 || batteryAlike && !canProvideEnergy(stack)) {
            return 0;
        }
        long extracted = Math.min(getStoredCharge(stack), transferAmount(charge, ignoreTransferLimit));
        if (extracted <= 0) {
            return 0;
        }
        if (!simulate) {
            setStoredCharge(stack, getStoredCharge(stack) - extracted);
        }
        return extracted;
    }

    @Override
    public double getCharge(ItemStack stack) {
        return getStoredCharge(stack);
    }

    @Override
    public boolean canUse(ItemStack stack, double amount) {
        return getStoredCharge(stack) >= amount;
    }

    @Override
    public boolean use(ItemStack stack, double amount, EntityLivingBase entity) {
        if (entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isCreativeMode) {
            return true;
        }
        if (discharge(stack, amount, Integer.MAX_VALUE, true, false, true) == amount) {
            discharge(stack, amount, Integer.MAX_VALUE, true, false, false);
            return true;
        }
        return false;
    }

    @Override
    public void chargeFromArmor(ItemStack stack, EntityLivingBase entity) {}

    @Override
    public String getToolTip(ItemStack stack) {
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        itemIcon = iconRegister.registerIcon(MyMod.MODID + ":" + NAME);
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("item.gtnh_additional_crafts.reusable"));
        tooltip.add(
            EnumChatFormatting.AQUA + StatCollector.translateToLocalFormatted(
                "gt.item.desc.eu_info",
                GTUtility.formatNumbers(getStoredCharge(stack)),
                GTUtility.formatNumbers(MAX_CHARGE),
                GTUtility.formatNumbers(TRANSFER_LIMIT)) + EnumChatFormatting.GRAY);
        tooltip.add(EnumChatFormatting.GREEN + "IV-Tier" + EnumChatFormatting.GRAY);
    }

    private static long transferAmount(double amount, boolean ignoreTransferLimit) {
        return Math
            .max(0, (long) Math.min(ignoreTransferLimit ? amount : Math.min(amount, TRANSFER_LIMIT), Long.MAX_VALUE));
    }

    private static long getStoredCharge(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound()) {
            return 0;
        }
        return Math.max(
            0,
            Math.min(
                MAX_CHARGE,
                stack.getTagCompound()
                    .getLong(CHARGE_NBT_KEY)));
    }

    private static void setStoredCharge(ItemStack stack, long charge) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
        }
        tag.removeTag(CHARGE_NBT_KEY);
        long clampedCharge = Math.max(0, Math.min(MAX_CHARGE, charge));
        if (clampedCharge > 0) {
            tag.setLong(CHARGE_NBT_KEY, clampedCharge);
        }
        if (tag.hasNoTags()) {
            stack.setTagCompound(null);
        } else {
            stack.setTagCompound(tag);
        }
    }
}
