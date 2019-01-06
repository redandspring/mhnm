package ru.redandspring.model;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

public class Config {

    static long START_ID;

    public static final String HOME_TEMP = "c:/_PROJECT/temp/";

    public static String SITE_POSTS;

    public static final List<Long> SENT_ADV = new CopyOnWriteArrayList<>();

    public static final int COUNT_PAGES = 18;

    public static final int TIMEOUT_EACH_ROW = 2 * 1000;
    public static final int TIMEOUT_BATCH = 30 * 1000;
    public static final int TIMEOUT_BATCH_LONG = 300 * 1000;

    static {
        try (final InputStream fis = new FileInputStream(HOME_TEMP + "config.properties")){
            final Properties property = new Properties();
            property.load(fis);
            SITE_POSTS = property.getProperty("site.posts");
            START_ID = Long.parseLong(property.getProperty("site.posts.start.id"));
        }
        catch (IOException e) {
            System.err.println("ОШИБКА: Файл свойств отсуствует!");
            throw new RuntimeException(e);
        }
    }
}
