package com.navidad.repo;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class JsonRepository<T> {
    private final String filePath;
    private final Gson gson;
    private final Type listType;

    public JsonRepository(String filePath, Type listType) {
        this.filePath = filePath;
        this.listType = listType;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(java.time.LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(java.time.LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();
        ensureFile();
    }

    private void ensureFile() {
        try {
            File f = new File(filePath);
            if (!f.exists()) {
                Files.createDirectories(Paths.get(f.getParent() == null ? "." : f.getParent()));
                try (Writer w = new FileWriter(f)) {
                    w.write("[]");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized List<T> readAll() {
        try (Reader r = new FileReader(filePath)) {
            return gson.fromJson(r, listType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void writeAll(List<T> items) {
        try (Writer w = new FileWriter(filePath)) {
            gson.toJson(items, listType, w);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Adapters for LocalDate and LocalDateTime
    static class LocalDateAdapter implements JsonSerializer<java.time.LocalDate>, JsonDeserializer<java.time.LocalDate> {
        public JsonElement serialize(java.time.LocalDate src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }
        public java.time.LocalDate deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) {
            return java.time.LocalDate.parse(json.getAsString());
        }
    }
    static class LocalDateTimeAdapter implements JsonSerializer<java.time.LocalDateTime>, JsonDeserializer<java.time.LocalDateTime> {
        public JsonElement serialize(java.time.LocalDateTime src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }
        public java.time.LocalDateTime deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) {
            return java.time.LocalDateTime.parse(json.getAsString());
        }
    }
}
