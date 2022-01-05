package io.github.toberocat.improvedfactions.utility.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.toberocat.improvedfactions.language.LangMessage;

import java.io.File;
import java.io.IOException;

public class JsonObjectLoader {

    public static <C> Object LoadFromJson(File file, Class<C> clazz) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(file, clazz);
    }

}
