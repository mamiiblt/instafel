package me.mamiiblt.instafel.patcher.core.utils;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class Env {

    public static String USER_DIR = System.getProperty("user.dir"); 
    public static String PROJECT_DIR = null;
    public static String[] INSTAFEL_LOCALES = {"tr", "de", "el", "fr", "hi", "hu", "pt", "es", "az"};
    public static String SPERATOR_STR = "---------------------------";

    public class Config {
        public static enum Keys {
            manifest_version,
            source_dir,
            use_external_ifl_source,
            prod_mode,
            manager_token,
            github_pat
        }

        public static String UPDATE_STR = "Updated Config";
        public static File file; 
        public static PropertyManager propertyManager;

        public static void setupConfig() {
            try {
                file = new File(Utils.mergePaths(Env.PROJECT_DIR, "config.properties"));
                propertyManager = new PropertyManager(file);
            } catch (Exception e) {
                e.printStackTrace();
                Log.severe("Error while loading configuration file.");
                System.exit(-1);
            }
        }

        public static void saveProperties() {
            propertyManager.save(UPDATE_STR);
        }

        public static String getString(Keys key, String defaultValue) {
            return propertyManager.getString(key.toString(), defaultValue);
        }

        public static int getInteger(Keys key, int defaultValue) {
            return propertyManager.getInteger(key.toString(), defaultValue);
        }

        public static boolean getBoolean(Keys key, boolean defaultValue) {
            return propertyManager.getBoolean(key.toString(), defaultValue);
        }

        public static void setString(Keys key, String value) {
            propertyManager.addString(key.toString(), value);
        }

        public static void setInteger(Keys key, int value) {
            propertyManager.addInteger(key.toString(), value);
        }

        public static void setBoolean(Keys key, boolean value) {
            propertyManager.addBoolean(key.toString(), value);
        }

        public static void createDefaultConfigFile() throws StreamReadException, DatabindException, IOException {
            propertyManager.addInteger(Keys.manifest_version.toString(), 1);
            propertyManager.addString(Keys.source_dir.toString(), "/sources");
            propertyManager.addBoolean(Keys.use_external_ifl_source.toString(), false);
            propertyManager.addBoolean(Keys.prod_mode.toString(), false);
            propertyManager.addString(Keys.manager_token.toString(), "not_needed");
            propertyManager.addString(Keys.github_pat.toString(), "null");
            saveProperties();
        }
    }

    public class Project {
    
        public static enum Keys {
            API_BASE,
            INSTAGRAM_VERSION,
            INSTAGRAM_VERSION_CODE,
            GENID,
            INSTAFEL_VERSION,
            APPLIED_PATCHES,
            IFL_SOURCES_FOLDER,
            P_VCLASS_PATH
        }
    
        public static String SAVE_STR = "Update Project Environment File";
        public static File file; 
        public static PropertyManager propertyManager;
    
        public static void setupEnv() {
            try {
                file = new File(
                    Utils.mergePaths(Env.PROJECT_DIR, "env.properties")
                );
                propertyManager = new PropertyManager(file);
            } catch (Exception e) {
                e.printStackTrace();
                Log.severe("Error while loading environment file.");
                System.exit(-1);
            }
        }
    
        public static void saveProperties() {
            propertyManager.save(SAVE_STR);
        }
    
        public static String getString(Keys key, String defaultValue) {
            String value = propertyManager.getString(key.toString(), defaultValue);
            return value;
        }
    
        public static int getInteger(Keys key, int defaultValue) {
            try {
                return Integer.parseInt(propertyManager.getString(key.toString(), String.valueOf(defaultValue)));
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
    
        public static boolean getBoolean(Keys key, boolean defaultValue) {
            return Boolean.parseBoolean(propertyManager.getString(key.toString(), String.valueOf(defaultValue)));
        }
    
        public static void setString(Keys key, String value) {
            propertyManager.addString(key.toString(), value);
        }
    
        public static void setInteger(Keys key, int value) {
            propertyManager.addInteger(key.toString(), value);
        }
    
        public static void setBoolean(Keys key, boolean value) {
            propertyManager.addBoolean(key.toString(), value);
        }
    
        public static void createDefaultEnvFile() throws StreamReadException, DatabindException, IOException {
            propertyManager.addString(Keys.API_BASE.toString(), "api.mamiiblt.me/ifl");
            propertyManager.addString(Keys.APPLIED_PATCHES.toString(), "");
            setIgVerCodeAndVersion();
        }
    
        private static void setIgVerCodeAndVersion() throws StreamReadException, DatabindException, IOException {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            
            JsonNode root = mapper.readTree(new File(
                Utils.mergePaths(Env.PROJECT_DIR, "sources", "apktool.yml")
            )).get("versionInfo");
            
            propertyManager.addString(Keys.INSTAGRAM_VERSION.toString(), root.get("versionName").asText());
            propertyManager.addString(Keys.INSTAGRAM_VERSION_CODE.toString(), root.get("versionCode").asText());
        }
    }    
}
