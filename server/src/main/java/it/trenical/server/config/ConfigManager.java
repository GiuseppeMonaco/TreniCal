package it.trenical.server.config;

import com.fasterxml.jackson.dataformat.toml.TomlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public enum ConfigManager {
    INSTANCE;

    public final Config config;

    ConfigManager() {
        Logger logger = LoggerFactory.getLogger(ConfigManager.class);
        String CONFIG_PATH = "./TreniCalServerConfig.toml";

        if (!Files.exists(Paths.get(CONFIG_PATH))) {
            logger.info("Config file not found... using default configs");
            this.config = new Config();
            return;
        }

        Config config;
        try {
            TomlMapper mapper = new TomlMapper();
            config = mapper.readValue(new File(CONFIG_PATH), Config.class);
        } catch (IOException e) {
            logger.error("Error parsing config file: {}", e.getMessage());
            logger.info("Using default configs");
            config = new Config();
        }
        this.config = config;
    }
}
