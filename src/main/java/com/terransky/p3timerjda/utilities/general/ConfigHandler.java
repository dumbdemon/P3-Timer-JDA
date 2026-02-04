package com.terransky.p3timerjda.utilities.general;

import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigHandler {

    public static final Path configPath = Paths.get("./config.yml");
    private static final Object LOCK = new Object();
    private static ConfigHandler configHandler;
    Config config;

    private ConfigHandler(Path configPath) throws FileNotFoundException {
        this.config = loadConfig(configPath);
    }

    public static ConfigHandler getInstance() throws FileNotFoundException {
        return getInstance(configPath);
    }

    public static ConfigHandler getInstance(Path configPath) throws FileNotFoundException {
        synchronized (LOCK) {
            if (configHandler == null) {
                configHandler = new ConfigHandler(configPath);
            }
            return configHandler;
        }
    }

    public Config loadConfig(@NotNull Path configPath) throws FileNotFoundException {
        LoaderOptions loaderOptions = new LoaderOptions();
        Constructor constructor = new Constructor(Config.class, loaderOptions);
        Yaml yaml = new Yaml(constructor);
        return yaml.load(new FileInputStream(configPath.toFile()));
    }

    public void dumpConfig() throws IllegalArgumentException, IOException {
        dumpConfig(this.config, configPath);
    }

    public void dumpConfig(Config config, @NotNull Path configPath) throws IllegalArgumentException, IOException {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        Yaml yml = new Yaml(options);
        yml.dump(config, new FileWriter(configPath.toFile()));
    }

    public Config getConfig() {
        return this.config;
    }
}
