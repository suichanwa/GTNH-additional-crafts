package gtnh_additional_crafts.client.nei;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.api.API;
import codechicken.nei.recipe.TemplateRecipeHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTUtility;
import gtnh_additional_crafts.MyMod;
import gtnh_additional_crafts.machine.MTEBoilerLiquidBase;
import gtnh_additional_crafts.machine.ModMachines;
import gtnh_additional_crafts.recipe.util.FluidLookup;

@SideOnly(Side.CLIENT)
public class NEILiquidBoilerHandler extends TemplateRecipeHandler {

    public class CachedLiquidBoilerRecipe extends CachedRecipe {

        public final FluidStack fuel;
        public final int burnTimeBronze;
        public final int burnTimeSteel;
        public final PositionedStack fuelStack;
        public final PositionedStack waterStack;
        public final PositionedStack boilerStack;
        public final PositionedStack steamStack;

        public CachedLiquidBoilerRecipe(FluidStack fuel, int burnTimeBronze, int burnTimeSteel) {
            this.fuel = fuel;
            this.burnTimeBronze = burnTimeBronze;
            this.burnTimeSteel = burnTimeSteel;

            List<ItemStack> fuels = new ArrayList<>();
            ItemStack display = GTUtility.getFluidDisplayStack(fuel, false);
            if (display != null) {
                fuels.add(display);
            }
            ItemStack cell = GTUtility.fillFluidContainer(fuel, ItemList.Cell_Empty.get(1L), false, true);
            if (cell != null) {
                fuels.add(cell);
            }
            FluidContainerData[] containers = FluidContainerRegistry.getRegisteredFluidContainerData();
            if (containers != null) {
                for (FluidContainerData cd : containers) {
                    if (cd.fluid != null && cd.fluid.isFluidEqual(fuel) && cd.filledContainer != null) {
                        fuels.add(cd.filledContainer.copy());
                    }
                }
            }
            this.fuelStack = new PositionedStack(fuels, 31, 37);

            List<ItemStack> waters = new ArrayList<>();
            ItemStack waterDisplay = GTUtility.getFluidDisplayStack(Materials.Water.getFluid(1000L), false);
            if (waterDisplay != null) {
                waters.add(waterDisplay);
            }
            waters.add(new ItemStack(Items.water_bucket));
            this.waterStack = new PositionedStack(waters, 31, 11);

            List<ItemStack> boilers = new ArrayList<>();
            ItemStack small = ModMachines.getSmallLiquidBoiler(1L);
            ItemStack large = ModMachines.getLargeLiquidBoiler(1L);
            if (small != null) {
                boilers.add(small);
            }
            if (large != null) {
                boilers.add(large);
            }
            this.boilerStack = new PositionedStack(boilers, 75, 9);

            ItemStack steamDisplay = GTUtility.getFluidDisplayStack(Materials.Steam.getGas(1000L), false);
            this.steamStack = new PositionedStack(steamDisplay, 117, 32);
        }

        @Override
        public PositionedStack getResult() {
            return this.steamStack;
        }

        @Override
        public List<PositionedStack> getIngredients() {
            return this.getCycledIngredients(
                NEILiquidBoilerHandler.this.cycleticks / 20,
                Arrays.asList(this.fuelStack, this.waterStack));
        }

        @Override
        public List<PositionedStack> getOtherStacks() {
            return this.getCycledIngredients(
                NEILiquidBoilerHandler.this.cycleticks / 20,
                Collections.singletonList(this.boilerStack));
        }
    }

    private final List<CachedLiquidBoilerRecipe> allRecipes = new ArrayList<>();

    public NEILiquidBoilerHandler() {
        loadAllRecipes();
    }

    private void loadAllRecipes() {
        this.allRecipes.clear();
        addRecipeIfValid(Materials.Creosote, 160, 40, "creosote", "creosoteoil");
        addRecipeIfValid(Materials.Biomass, 120, 30, "biomass");
        addRecipeIfValid(Materials.Oil, 100, 25, "oil", "crudeoil", "lightoil");
        addRecipeIfValid(Materials.FishOil, 80, 20, "fishoil", "fish.oil");
        addRecipeIfValid(Materials.SeedOil, 60, 15, "seedoil", "seed.oil");
    }

    private void addRecipeIfValid(Materials mat, int bronzeTime, int steelTime, String... fallbacks) {
        FluidStack stack = FluidLookup.getMaterialFluidOrFallback(mat, 1000, fallbacks);
        if (stack != null) {
            this.allRecipes.add(new CachedLiquidBoilerRecipe(stack, bronzeTime, steelTime));
        }
    }

    public void register() {
        API.registerRecipeHandler(this);
        API.registerUsageHandler(this);
        ItemStack small = ModMachines.getSmallLiquidBoiler(1L);
        ItemStack large = ModMachines.getLargeLiquidBoiler(1L);
        if (small != null) {
            API.addRecipeCatalyst(small, this);
        }
        if (large != null) {
            API.addRecipeCatalyst(large, this);
        }
    }

    @Override
    public TemplateRecipeHandler newInstance() {
        return new NEILiquidBoilerHandler();
    }

    @Override
    public String getGuiTexture() {
        return "gtnh_additional_crafts:textures/gui/nei_liquid_boiler.png";
    }

    @Override
    public String getRecipeName() {
        return "Liquid Boiler Fuels";
    }

    @Override
    public String getOverlayIdentifier() {
        return "liquid_boiler_fuels";
    }

    @Override
    public int recipiesPerPage() {
        return 1;
    }

    @Override
    public int getRecipeHeight(int recipe) {
        return 130;
    }

    @Override
    public void loadTransferRects() {
        this.transferRects.add(new RecipeTransferRect(new Rectangle(71, 32, 24, 17), "liquid_boiler_fuels"));
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if ("liquid_boiler_fuels".equals(outputId) || "crafting".equals(outputId)) {
            this.arecipes.addAll(this.allRecipes);
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        if (result == null) {
            return;
        }
        FluidStack fluid = GTUtility.getFluidFromDisplayStack(result);
        if (fluid == null) {
            fluid = GTUtility.getFluidForFilledItem(result, true);
        }
        if (fluid != null && GTModHandler.isSteam(fluid)) {
            this.arecipes.addAll(this.allRecipes);
            return;
        }
        ItemStack small = ModMachines.getSmallLiquidBoiler(1L);
        ItemStack large = ModMachines.getLargeLiquidBoiler(1L);
        if ((small != null && GTUtility.areStacksEqual(small, result, true))
            || (large != null && GTUtility.areStacksEqual(large, result, true))) {
            this.arecipes.addAll(this.allRecipes);
        }
    }

    @Override
    public void loadUsageRecipes(String inputId, Object... ingredients) {
        if ("liquid_boiler_fuels".equals(inputId)) {
            this.arecipes.addAll(this.allRecipes);
        } else {
            super.loadUsageRecipes(inputId, ingredients);
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        if (ingredient == null) {
            return;
        }
        ItemStack small = ModMachines.getSmallLiquidBoiler(1L);
        ItemStack large = ModMachines.getLargeLiquidBoiler(1L);
        if ((small != null && GTUtility.areStacksEqual(small, ingredient, true))
            || (large != null && GTUtility.areStacksEqual(large, ingredient, true))) {
            this.arecipes.addAll(this.allRecipes);
            return;
        }
        FluidStack fluid = GTUtility.getFluidFromDisplayStack(ingredient);
        if (fluid == null) {
            fluid = GTUtility.getFluidForFilledItem(ingredient, true);
        }
        if (fluid != null && GTModHandler.isWater(fluid)) {
            this.arecipes.addAll(this.allRecipes);
            return;
        }
        if (fluid != null && MTEBoilerLiquidBase.isFuelAllowed(fluid)) {
            for (CachedLiquidBoilerRecipe rec : this.allRecipes) {
                if (rec.fuel.isFluidEqual(fluid)) {
                    this.arecipes.add(rec);
                }
            }
            return;
        }
        for (CachedLiquidBoilerRecipe rec : this.allRecipes) {
            if (rec.contains(rec.getIngredients(), ingredient) || rec.contains(rec.getOtherStacks(), ingredient)) {
                this.arecipes.add(rec);
            }
        }
    }

    @Override
    public void drawBackground(int recipe) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GuiDraw.changeTexture(this.getGuiTexture());
        GuiDraw.drawTexturedModalRect(0, 0, 0, 0, 166, 130);
    }

    @Override
    public void drawExtras(int recipe) {
        if (recipe < 0 || recipe >= this.arecipes.size()) {
            return;
        }
        CachedLiquidBoilerRecipe rec = (CachedLiquidBoilerRecipe) this.arecipes.get(recipe);

        drawProgressBar(71, 32, 176, 14, 24, 17, 40, 0);
        drawProgressBar(52, 38, 176, 0, 14, 14, 40, 3);

        int y = 66;
        GuiDraw.drawString("Fuel: " + EnumChatFormatting.DARK_GREEN + rec.fuel.getLocalizedName(), 11, y, 0x202020, false);
        y += 10;
        GuiDraw.drawString("Burn Time (1000L):", 11, y, 0x404040, false);
        y += 10;
        GuiDraw.drawString(
            " Small: " + EnumChatFormatting.GOLD + rec.burnTimeBronze + "s"
                + EnumChatFormatting.DARK_GRAY + " (" + (rec.burnTimeBronze / 10) + "s / 100L)",
            13,
            y,
            0x202020,
            false);
        y += 10;
        GuiDraw.drawString(
            " Steel: " + EnumChatFormatting.BLUE + rec.burnTimeSteel + "s"
                + EnumChatFormatting.DARK_GRAY + " (" + (rec.burnTimeSteel / 10) + "s / 100L)",
            13,
            y,
            0x202020,
            false);
        y += 10;
        GuiDraw.drawString(
            "Steam: " + EnumChatFormatting.GOLD + "160" + EnumChatFormatting.DARK_GRAY + " | "
                + EnumChatFormatting.BLUE + "320" + EnumChatFormatting.DARK_GRAY + " L/s",
            11,
            y,
            0x202020,
            false);
        y += 10;
        GuiDraw.drawString(
            "Pollution: " + EnumChatFormatting.GOLD + "20" + EnumChatFormatting.DARK_GRAY + " | "
                + EnumChatFormatting.BLUE + "30" + EnumChatFormatting.DARK_GRAY + " /s",
            11,
            y,
            0x202020,
            false);
    }
}
