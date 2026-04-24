package gtnh_additional_crafts.client.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.config.Property.Type;

import cpw.mods.fml.common.Loader;

public final class ThaumicBootsConfigManager {

    private static final String[] CANDIDATE_FILE_NAMES = new String[] { "thaumicboots.cfg", "ThaumicBoots.cfg",
        "thaumic_boots.cfg" };

    private static Configuration configuration;

    private ThaumicBootsConfigManager() {}

    public static synchronized Configuration getConfiguration() {
        if (configuration == null) {
            configuration = loadConfiguration();
        }
        return configuration;
    }

    public static synchronized void saveIfChanged() {
        if (configuration != null && configuration.hasChanged()) {
            configuration.save();
        }
    }

    private static Configuration loadConfiguration() {
        File configFile = resolveThaumicBootsConfigFile();
        Configuration loadedConfiguration = new Configuration(configFile);
        loadedConfiguration.load();
        normalizePropertyTypes(loadedConfiguration);
        if (loadedConfiguration.hasChanged()) {
            loadedConfiguration.save();
        }
        return loadedConfiguration;
    }

    private static boolean normalizePropertyTypes(Configuration configuration) {
        boolean changed = false;
        for (String categoryName : configuration.getCategoryNames()) {
            ConfigCategory category = configuration.getCategory(categoryName);
            if (category == null || category.isEmpty()) {
                continue;
            }
            changed |= normalizeCategory(category);
        }
        return changed;
    }

    private static boolean normalizeCategory(ConfigCategory category) {
        boolean changed = false;
        List<String> propertyNames = new ArrayList<String>(category.keySet());
        for (String propertyName : propertyNames) {
            Property original = category.get(propertyName);
            if (original == null) {
                continue;
            }
            Property normalized = normalizePropertyType(original);
            if (normalized != original) {
                category.put(propertyName, normalized);
                changed = true;
            }
        }
        List<String> sortedNames = new ArrayList<String>(category.keySet());
        Collections.sort(
            sortedNames,
            Comparator.comparingInt((String name) -> getTypeRank(category.get(name)))
                .thenComparing(String::compareToIgnoreCase));
        List<String> currentOrder = category.getPropertyOrder();
        if (!sortedNames.equals(currentOrder)) {
            category.setPropertyOrder(sortedNames);
            changed = true;
        }
        return changed;
    }

    private static int getTypeRank(Property property) {
        if (property == null) {
            return 3;
        }
        Type type = property.getType();
        if (type == Type.BOOLEAN) {
            return 0;
        }
        if (type == Type.INTEGER || type == Type.DOUBLE) {
            return 1;
        }
        return 2;
    }

    private static Property normalizePropertyType(Property property) {
        if (property.getType() != Type.STRING) {
            return property;
        }

        Type inferredType = inferType(property);
        if (inferredType == Type.STRING) {
            return property;
        }

        Property normalized = property.isList()
            ? new Property(property.getName(), property.getStringList(), inferredType, property.getLanguageKey())
            : new Property(property.getName(), property.getString(), inferredType, property.getLanguageKey());

        copyMetadata(property, normalized, inferredType);
        return normalized;
    }

    private static void copyMetadata(Property source, Property target, Type targetType) {
        target.comment = source.comment;
        target.setRequiresMcRestart(source.requiresMcRestart());
        target.setRequiresWorldRestart(source.requiresWorldRestart());
        target.setShowInGui(source.showInGui());
        target.setMaxListLength(source.getMaxListLength());
        target.setIsListLengthFixed(source.isListLengthFixed());

        Pattern validationPattern = source.getValidationPattern();
        if (validationPattern != null) {
            target.setValidationPattern(validationPattern);
        }

        String[] validValues = source.getValidValues();
        if (validValues != null && validValues.length > 0) {
            target.setValidValues(validValues);
        }

        if (source.getConfigEntryClass() != null) {
            target.setConfigEntryClass(source.getConfigEntryClass());
        }
        if (source.getArrayEntryClass() != null) {
            target.setArrayEntryClass(source.getArrayEntryClass());
        }

        if (source.isList()) {
            String[] defaults = source.getDefaults();
            if (defaults != null && defaults.length > 0) {
                target.setDefaultValues(defaults);
            }
        } else {
            String defaultValue = source.getDefault();
            if (defaultValue != null) {
                target.setDefaultValue(defaultValue);
            }
        }

        applyNumericBounds(source, target, targetType);
    }

    private static void applyNumericBounds(Property source, Property target, Type targetType) {
        String min = source.getMinValue();
        String max = source.getMaxValue();
        if (targetType == Type.INTEGER) {
            Integer minInt = tryParseInt(min);
            Integer maxInt = tryParseInt(max);
            if (minInt != null) {
                target.setMinValue(minInt.intValue());
            }
            if (maxInt != null) {
                target.setMaxValue(maxInt.intValue());
            }
        } else if (targetType == Type.DOUBLE) {
            Double minDouble = tryParseDouble(min);
            Double maxDouble = tryParseDouble(max);
            if (minDouble != null) {
                target.setMinValue(minDouble.doubleValue());
            }
            if (maxDouble != null) {
                target.setMaxValue(maxDouble.doubleValue());
            }
        }
    }

    private static Type inferType(Property property) {
        return property.isList() ? inferListType(property) : inferScalarType(property);
    }

    private static Type inferScalarType(Property property) {
        String value = property.getString();
        if (isBooleanCandidate(value, property.getValidValues())) {
            return Type.BOOLEAN;
        }
        if (tryParseInt(value) != null) {
            return Type.INTEGER;
        }
        if (tryParseDouble(value) != null) {
            return Type.DOUBLE;
        }
        return Type.STRING;
    }

    private static Type inferListType(Property property) {
        String[] values = property.getStringList();
        if (values == null || values.length == 0) {
            return Type.STRING;
        }
        if (isBooleanList(values, property.getValidValues())) {
            return Type.BOOLEAN;
        }
        if (isIntegerList(values)) {
            return Type.INTEGER;
        }
        if (isDoubleList(values)) {
            return Type.DOUBLE;
        }
        return Type.STRING;
    }

    private static boolean isBooleanList(String[] values, String[] validValues) {
        for (String value : values) {
            if (!isBooleanCandidate(value, validValues)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isIntegerList(String[] values) {
        for (String value : values) {
            if (tryParseInt(value) == null) {
                return false;
            }
        }
        return true;
    }

    private static boolean isDoubleList(String[] values) {
        for (String value : values) {
            if (tryParseDouble(value) == null) {
                return false;
            }
        }
        return true;
    }

    private static boolean isBooleanCandidate(String value, String[] validValues) {
        if (value == null) {
            return false;
        }
        if (!"true".equalsIgnoreCase(value) && !"false".equalsIgnoreCase(value)) {
            return false;
        }
        if (validValues == null || validValues.length == 0) {
            return true;
        }
        for (String validValue : validValues) {
            if ("true".equalsIgnoreCase(validValue) || "false".equalsIgnoreCase(validValue)) {
                return true;
            }
        }
        return false;
    }

    private static Integer tryParseInt(String value) {
        if (value == null) {
            return null;
        }
        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static Double tryParseDouble(String value) {
        if (value == null) {
            return null;
        }
        try {
            return Double.valueOf(value.trim());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static File resolveThaumicBootsConfigFile() {
        File configDir = Loader.instance()
            .getConfigDir();
        for (String fileName : CANDIDATE_FILE_NAMES) {
            File candidate = new File(configDir, fileName);
            if (candidate.exists()) {
                return candidate;
            }
        }

        File[] matchingFiles = configDir.listFiles((directory, fileName) -> {
            String lowerCaseName = fileName.toLowerCase(Locale.ROOT);
            return lowerCaseName.endsWith(".cfg") && lowerCaseName.contains("thaumic")
                && lowerCaseName.contains("boot");
        });
        if (matchingFiles != null && matchingFiles.length > 0) {
            Arrays.sort(matchingFiles, Comparator.comparing(File::getName));
            return matchingFiles[0];
        }
        return new File(configDir, CANDIDATE_FILE_NAMES[0]);
    }
}
