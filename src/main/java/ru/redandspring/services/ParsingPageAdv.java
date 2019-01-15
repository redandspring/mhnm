package ru.redandspring.services;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.redandspring.model.Adv;
import ru.redandspring.model.Config;

import java.io.IOException;

public class ParsingPageAdv {

    private static final Logger log = LoggerFactory.getLogger(ParsingPageAdv.class);

    private Box box = Box.getInstance();

    public void parsePosts(int i) throws ServiceException {
        // log.info("parsePosts(): i={}", i);

        final long id = box.getStorageCurrentId() + i;
        if (Config.SENT_ADV.contains(id)){
            return;
        }

        if (box.size() > 100){
            return;
        }

        final String url = String.format(Config.SITE_POSTS, id);

        try {
            Connection connect = Jsoup.connect(url);
            connect.followRedirects(false);
            final Document doc  = connect.get();
            log.info("connect id={} size={} i={}", id, box.size(), i);

            final String advText = findAdvText(doc);

            if (advText != null){
                /*if (advText.contains("Такой страницы нет")){
                    log.info("parsePosts(): i={}", i);
                }*/

                if (advText.contains("ico_arrow")) {
                    if (advText.contains("Москва") && !advText.contains("спонсорство")) {
                        box.add(new Adv(id, advText, url));
                    }
                    box.setStorageLastSuccessId(id);
                    Config.SENT_ADV.add(id);
                }
            }

        } catch (IOException e) {
            throw new ServiceException("parsePosts(): get url is error", e);
        }
    }

    public boolean setNewStart() {
        log.info("setNewStart():");

        long successId = box.getStorageLastSuccessId() - Config.COUNT_PAGES / 2;
        if (successId > box.getStorageCurrentId()){
            box.setStorageCurrentId(successId);
            box.saveStorage();
            return true;
        }

        if (box.getStorageLastSuccessId() > box.getStorageCurrentId()){
            box.setStorageCurrentId(box.getStorageCurrentId() + 2);
            box.saveStorage();
            log.info("setNewStart(): +2");
            return false;
        }

        log.info("setNewStart(): no new applications");
        return false;
    }

    private static String findAdvText(Element element) {

        Elements finds = element.select("table.pagew tr td.pagew div table.t td");
        if (finds.size() > 0){
            return finds.get(0).html();
        }
        return null;
    }
}
