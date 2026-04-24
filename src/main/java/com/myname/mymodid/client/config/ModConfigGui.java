package com.myname.mymodid.client.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;

import com.myname.mymodid.Config;
import com.myname.mymodid.MyMod;

import cpw.mods.fml.client.config.DummyConfigElement.DummyCategoryElement;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;

public class ModConfigGui extends GuiConfig {

    public ModConfigGui(GuiScreen parentScreen) {
        super(
            parentScreen,
            buildConfigElements(),
            MyMod.MODID,
            false,
            false,
            "GTNH Additional Crafts + Thaumic Boots Config (typed controls)");
    }

    private static List<IConfigElement> buildConfigElements() {
        List<IConfigElement> topLevelElements = new ArrayList<IConfigElement>();
        topLevelElements.add(
            new DummyCategoryElement(
                "GTNH Additional Crafts",
                "gtnhadditionalcrafts.config",
                getCategoryElements(Config.getConfiguration())));
        topLevelElements.add(
            new DummyCategoryElement(
                "Thaumic Boots",
                "thaumicboots.config",
                getCategoryElements(ThaumicBootsConfigManager.getConfiguration())));
        return topLevelElements;
    }

    private static List<IConfigElement> getCategoryElements(Configuration configuration) {
        if (configuration == null) {
            return Collections.emptyList();
        }
        Collection<String> categoryNames = configuration.getCategoryNames();
        if (categoryNames == null || categoryNames.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> sortedCategoryNames = new ArrayList<String>(categoryNames);
        Collections.sort(sortedCategoryNames);

        List<IConfigElement> categoryElements = new ArrayList<IConfigElement>(sortedCategoryNames.size());
        for (String categoryName : sortedCategoryNames) {
            ConfigCategory category = configuration.getCategory(categoryName);
            if (category.isChild()) {
                continue;
            }
            categoryElements.add(new ConfigElement(category));
        }
        return categoryElements;
    }
}
