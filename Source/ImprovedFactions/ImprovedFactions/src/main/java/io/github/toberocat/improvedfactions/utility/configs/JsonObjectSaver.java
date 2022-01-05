package io.github.toberocat.improvedfactions.utility.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.toberocat.improvedfactions.language.LangMessage;

import java.io.File;
import java.io.IOException;

public class JsonObjectSaver {

    public static void SaveToJson(File file, Object object, boolean usePrettyPrinter) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        if (usePrettyPrinter) {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, object);
        } else {
            mapper.writeValue(file, object);
        }
    }
}
