package io.github.toberocat.core.utility.language;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import io.github.toberocat.MainIF;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Language {
    private static final Pattern pattern = Pattern.compile("(#[a-fA-F0-9]{6})");
    private static boolean gotInitialized = false;

    private static Map<String, LangMessage> langFiles;


    public static boolean init(MainIF plugin, File datatDir) {
        File langDir = new File(datatDir.getPath() + "/lang");
        langDir.mkdir();

        LangMessage defaultMessages = new LangMessage();
        try {
            File en_us = new File(langDir.getPath() + "/en_us.lang");

            if (en_us.createNewFile()) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.writerWithDefaultPrettyPrinter().writeValue(en_us, defaultMessages);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        langFiles = new HashMap<>();

        for (File file : langDir.listFiles()) {
            String[] split = file.getName().split("\\.");
            if (split.length > 1 && split[1].equals("lang")) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    LangMessage langMessage = mapper.readValue(file, LangMessage.class);
                    langFiles.put(split[0], langMessage);

                    // Add missing files
                    MapDifference<String, String> diff = Maps.difference(langMessage.getMessages(), defaultMessages.getMessages());
                    Set<String> keysOnlyInSource = diff.entriesOnlyOnRight().keySet();

                    for (String key : keysOnlyInSource) {
                        langMessage.getMessages().put(key, defaultMessages.getMessages().get(key));
                        MainIF.LogMessage(Level.INFO, "&cAdded &6" + key + "&c from default to &6" + file.getName() + "&c, &cbecause it was missing");
                    }

                    ObjectMapper saveMapper = new ObjectMapper();
                    saveMapper.writerWithDefaultPrettyPrinter().writeValue(file, langMessage);
                } catch (IOException e) {
                    MainIF.getIF().SaveShutdown("&cCouldn't read language file " + file.getName() + ". Error: &6" + e.getMessage());
                    return false;
                }
            }
        }
        gotInitialized = true;
        return true;
    }

    public static String getMessage(String msgKey, Player player) {
        String locale = player.getLocale();
        if (langFiles.containsKey(locale)) {
            LangMessage langMessage = langFiles.get(locale);

           return format(langMessage.getMessages().get(msgKey));
        } else {
            LangMessage langMessage = langFiles.get("en_us");

            return format(langMessage.getMessages().get(msgKey));
        }
    }

    public static String getMessage(String msgKey, String file) {
        if (langFiles.containsKey(file)) {
            LangMessage langMessage = langFiles.get(file);

            return format(langMessage.getMessages().get(msgKey));
        } else {
            LangMessage langMessage = langFiles.get("en_us");

            return format(langMessage.getMessages().get(msgKey));
        }
    }

    public static void sendMessage(String msgKey, Player player, Parseable... parseables) {
        String locale = player.getLocale();
        LangMessage langMessage = null;

        if (langFiles.containsKey(locale)) {
            langMessage = langFiles.get(locale);
        } else {
            langMessage = langFiles.get("en_us");
        }

        if (langMessage.getMessages().containsKey(msgKey)) {
            player.sendMessage(getPrefix() + format(parse(langMessage.getMessages().get(msgKey), parseables)));
        } else {
            player.sendMessage(getPrefix() + format("&cThere went something wrong. If this happens more than two times, please report it to an admin. Error: NoLocalizationFound"));
        }
    }

    /**
     * This function will convert any text with numbers instead of letters (e.g: e => 3) back into normal language.
     * Note that this will also replace numbers not meant as leeters. E.g: I'm number 1 => I'm number i
     * @param leetString The string with the numbers (e.g: 4bcd3f6h1jklmn0pqr57uvwxyz)
     * @return The string without the numbers (e.g: abcdefghijklmnopqrstuvwxyz)
     */
    public static String simpleLeetToEnglish(String leetString) {
        return leetString
                .replace("4", "a")
                .replace("3", "e")
                .replace("6", "g")
                .replace("1", "i")
                .replace("0", "o")
                .replace("5", "s")
                .replace("7", "t");
    }

    /**
     * Does the same as @see {@link #similarity(String, String)}, but now also removes stylised text and leetspeak
     * @param _s1 first string for comparison
     * @param _s2 second string for comparison
     * @return The difference between the two string as double
     */
    public static float similarityExtended(String _s1, String _s2) {
        String s1 = simpleLeetToEnglish(_s1);
        String s2 = simpleLeetToEnglish(_s2);

        return similarity(s1, s2);
    }

    /**
     * Calculates the similarity (a number within 0 and 1) between two strings.
     * It won't do anything beside
     * @param s1 first string for comparison
     * @param s2 second string for comparison
     * @return The difference between the two string as double
     */
    public static float similarity(String s1, String s2) {
        String longer = Normalizer.normalize(s1, Normalizer.Form.NFKC), shorter = Normalizer.normalize(s2, Normalizer.Form.NFKC);
        if (s1.length() < s2.length()) {
            longer = s2; shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) { return 1.0f;}

        return (longerLength - editDistance(longer, shorter)) / (float) longerLength;

    }

    public static int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0)
                    costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue),
                                    costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0)
                costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }

    public static String parse(String message, Parseable[] parables) {
        if (parables == null) return message;

        for (Parseable parseable : parables) {
            if (parseable != null) {
                message = message.replaceAll(escape(parseable.getParse()), escape(parseable.getTo()));
            }
        }
        return message;
    }

    public static String getPrefix() {
        return gotInitialized ? format(MainIF.getConfigManager().getValue("general.prefix") + ": &f") : format("&e&lImprovedFactions: &f");
    }

    public static String format(String _msg) {
        String msg = _msg;
        if (Bukkit.getVersion().contains("1.16") || Bukkit.getVersion().contains("1.17")) {
            Matcher matcher = pattern.matcher(msg);
            while (matcher.find()) {
                String color = msg.substring(matcher.start(), matcher.end());
                msg = msg.replace(color, ChatColor.of(color) + "");
                matcher = pattern.matcher(msg);
            }
        }
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static String escape(String s){
        if (s == null) return null;
        return s.replace("\\", "\\\\")
                .replace("\t", "\\t")
                .replace("\b", "\\b")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\f", "\\f")
                .replace("\'", "\\'")
                .replace("\"", "\\\"")
                .replace("{", "\\{")
                .replace("}", "\\}");
    }
}
