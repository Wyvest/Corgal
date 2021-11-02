package xyz.qalcyo.corgal.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class JsonUtils {
    public static final JsonParser PARSER = new JsonParser();

    public static JsonElement read(String name, File directory) {
        try {
            if (name.endsWith(".json"))
                name = name.substring(0, name.indexOf(".json"));
            if (directory == null)
                directory = new File("./");
            if (!directory.exists())
                directory.mkdirs();
            File file = new File(directory, name + ".json");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder builder = new StringBuilder();
            reader.lines().forEach(builder::append);
            return PARSER.parse(builder.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
