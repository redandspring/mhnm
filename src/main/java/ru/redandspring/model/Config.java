package ru.redandspring.model;

import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

public class Config {

    static long START_ID;

    public static final String HOME_TEMP = "c:/_PROJECT/temp/";

    public static String SITE_POSTS;
    public static String SITE_POSTS_ELEMENT_CSS;
    public static boolean SITE_POSTS_ELEMENT_IS_OUTER;

    public static final List<Long> SENT_ADV = new CopyOnWriteArrayList<>();

    public static final int COUNT_PAGES = 18;

    public static int TIMEOUT_EACH_ROW;
    public static int TIMEOUT_BATCH_SHORT;
    public static int TIMEOUT_BATCH_MEDIUM;
    public static int TIMEOUT_BATCH_BIG;

    public static List<String> WORDS_INCLUDE;
    public static List<String> WORDS_EXCLUDE;

    static {
        try (final InputStream fis = new FileInputStream(HOME_TEMP + "config.txt");
             final InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8)){
            final Properties property = new Properties();
            property.load(isr);
            SITE_POSTS = property.getProperty("site.posts");
            if (StringUtils.trimToNull(SITE_POSTS) == null) {
                throw new RuntimeException("site.posts is not exists");
            }
            SITE_POSTS_ELEMENT_CSS = property.getProperty("site.posts.element.css");
            if (StringUtils.trimToNull(SITE_POSTS_ELEMENT_CSS) == null) {
                throw new RuntimeException("site.posts.element.css is not exists");
            }
            START_ID = Long.parseLong(property.getProperty("site.posts.start.id"));
            if (START_ID < 800_000){
                throw new RuntimeException("site.posts.start.id is wrong");
            }
            SITE_POSTS_ELEMENT_IS_OUTER = Boolean.parseBoolean(property.getProperty("site.posts.element.outer.is", "false"));
            final boolean isFast = Boolean.parseBoolean(property.getProperty("site.posts.timeout.fast.is", "false"));
            final String prefixTimeoutProp = (isFast) ? "site.posts.timeout.fast." : "site.posts.timeout.slow.";
            TIMEOUT_EACH_ROW = propInt(property, prefixTimeoutProp + "row", "2") * 1000;
            TIMEOUT_BATCH_SHORT = propInt(property, prefixTimeoutProp + "batch.short", "30") * 1000;
            TIMEOUT_BATCH_MEDIUM = propInt(property, prefixTimeoutProp + "batch.medium", "100") * 1000;
            TIMEOUT_BATCH_BIG = propInt(property, prefixTimeoutProp + "batch.big", "300") * 1000;

            WORDS_INCLUDE = propList(property, "site.posts.words.include");
            WORDS_EXCLUDE = propList(property, "site.posts.words.exclude");
         }
        catch (IOException e) {
            System.err.println("ОШИБКА: Файл свойств отсуствует!");
            throw new RuntimeException(e);
        }
    }

    private static  List<String> propList(final Properties property, final String key) {
        String[] words = StringUtils.trimToEmpty(property.getProperty(key, "")).split(";");
        return Arrays.asList(words);
    }

    private static int propInt(final Properties property, final String key, final String df){
        return Integer.parseInt(StringUtils.trimToEmpty(property.getProperty(key, df)));
    }
}
