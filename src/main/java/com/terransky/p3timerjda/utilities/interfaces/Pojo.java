package com.terransky.p3timerjda.utilities.interfaces;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.terransky.p3timerjda.utilities.general.FileOperations;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * The interface for all Jackson POJOs
 */
public interface Pojo {

    String JSON_PATH = "json/";

    /**
     * Get a file safe name to save
     *
     * @param name A name to encode
     * @return A file safe name.
     */
    static String toSafeFileName(String name) {
        return URLEncoder.encode(name, StandardCharsets.UTF_8);
    }

    /**
     * Get a configured object mapper for saving.
     *
     * @return A configured {@link ObjectMapper}.
     */
    static ObjectMapper getMapperObject() {
        return JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .disable(
                SerializationFeature.FAIL_ON_EMPTY_BEANS
            )
            .build();
    }

    /**
     * Create a JSON file of this object with the class name as the file name.<br />
     * The file is saved to the <b>jsons</b> folder in the root directory.
     *
     * @throws IOException Is thrown if the file could not be saved or if {@link #getAsJsonString()} failed to parse.
     */
    @SuppressWarnings("unused")
    default void saveAsJsonFile() throws IOException {
        String[] className = this.getClass().getName().split("\\.");
        saveAsJsonFile(className[className.length - 1]);
    }

    /**
     * Create a JSON file of this object with a custom file name.<br />
     * The file is saved to the <b>jsons</b> folder in the root directory.
     *
     * @param name The name of the file
     * @throws IOException Is thrown if the file could not be saved or if {@link #getAsJsonString()} failed to parse.
     */
    default void saveAsJsonFile(String name) throws IOException {
        if (FileOperations.makeDirectory(JSON_PATH)) {
            saveAsJsonFile(new File(JSON_PATH + toSafeFileName(name) + ".json"));
            return;
        }
        LoggerFactory.getLogger(Pojo.class).error("Unable to create directory.");
    }

    /**
     * Create a JSON file of this object.<br />
     * The file is saved to the <b>jsons</b> folder in the root directory.
     *
     * @param file A {@link File} to save to.
     * @throws IOException Is thrown if the file could not be saved or if {@link #getAsJsonString()} failed to parse.
     */
    default void saveAsJsonFile(@NotNull final File file) throws IOException {
        LoggerFactory.getLogger(this.getClass()).info("Saving to -> \"{}\"", file.getAbsolutePath());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(getAsJsonString());
        }
    }

    /**
     * Convert this object into a JSON string.
     *
     * @return A JSON string of all non-null values
     * @throws JsonProcessingException Thrown if it failed to parse
     */
    @JsonIgnore
    default String getAsJsonString() throws JsonProcessingException {
        return getMapperObject()
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(this);
    }
}
