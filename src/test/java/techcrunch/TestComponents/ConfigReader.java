package techcrunch.TestComponents;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private Properties properties;
    private final String propertyFilePath = "src/test/resources/config.properties";

    public ConfigReader() {
        try (FileInputStream fis = new FileInputStream(propertyFilePath)) {
            properties = new Properties();
            properties.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to read config.properties file: " + e.getMessage());
        }
    }

    public String getBrowser() {
        String browser = properties.getProperty("browser");
        if (browser != null) {
            return browser;
        } else {
            throw new RuntimeException("'browser' property not found in config.properties file.");
        }
    }

    public String getBaseUrl() {
        String baseUrl = properties.getProperty("baseUrl");
        if (baseUrl != null) {
            return baseUrl;
        } else {
            System.out.println("Warning: 'baseUrl' property not found in config.properties file. Default URL will be used.");
            return "https://techcrunch.com/";
        }
    }

    public boolean getHeadless() {
        String headless = properties.getProperty("headless");
        if (headless != null) {
            return Boolean.parseBoolean(headless);
        } else {
            System.out.println("Warning: 'headless' property not found in config.properties file. Defaulting to 'false'.");
            return false;
        }
    }
}