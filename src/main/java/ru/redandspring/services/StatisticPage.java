package ru.redandspring.services;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

public class StatisticPage {

    private static final Logger log = LoggerFactory.getLogger(StatisticPage.class);

    private Box box = Box.getInstance();

    public String resultPage() throws IOException {
        log.info("resultPage(): ");

        final String page = IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("stat.template.html")),"UTF-8");
        final String wrap = IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("wrap.template.html")),"UTF-8");
        final Document doc = Jsoup.parse(page);
        final Elements content = doc.select("#my-table").empty();
        box.forEach(adv -> content.append(String.format(wrap, adv.id, adv.link, adv.id, adv.text)));

        return doc.html();
    }
}
