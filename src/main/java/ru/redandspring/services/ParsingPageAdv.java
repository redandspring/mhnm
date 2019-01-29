package ru.redandspring.services;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.redandspring.exception.NotFindElementException;
import ru.redandspring.exception.ServiceException;
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

        if (box.size() > 200){
            return;
        }

        final String url = String.format(Config.SITE_POSTS, id);

        try {
            Connection connect = Jsoup.connect(url);
            connect.followRedirects(false);
            final Document doc  = connect.get();
            log.info("connect id={} size={} i={}", id, box.size(), i);

            final String advText = findAdvText(doc);

            if (advText.contains("ico_arrow")) {

                boolean isAllInclude = Config.WORDS_INCLUDE.stream().allMatch(advText::contains);
                boolean isAnyExclude = Config.WORDS_EXCLUDE.stream().anyMatch(advText::contains);
                if (isAllInclude && !isAnyExclude){
                    box.add(new Adv(id, advText, url));
                }

                box.setStorageLastSuccessId(id);
                Config.SENT_ADV.add(id);
            }


        } catch (NotFindElementException ignore) {
        } catch (IOException e) {
            throw new ServiceException("parsePosts(): get url is error", e);
        }
    }

    public int setNewStart() {
        log.info("setNewStart():");

        long successId = box.getStorageLastSuccessId() - Config.COUNT_PAGES / 2;
        if (successId > box.getStorageCurrentId()){
            box.setStorageCurrentId(successId);
            box.saveStorage();
            return Config.TIMEOUT_BATCH_SHORT;
        }

        if (box.getStorageLastSuccessId() > box.getStorageCurrentId()){
            final long currentIdBefore = box.getStorageCurrentId();
            box.setStorageCurrentId(currentIdBefore + 2);
            box.saveStorage();
            log.info("setNewStart(): LastSuccessId={}, current={}->{}",
                    box.getStorageLastSuccessId(),
                    currentIdBefore,
                    box.getStorageCurrentId());
            return Config.TIMEOUT_BATCH_MEDIUM;
        }

        log.info("setNewStart(): no new applications");
        return Config.TIMEOUT_BATCH_BIG;
    }

    private static String findAdvText(Element element) throws NotFindElementException {

        Elements finds = element.select(Config.SITE_POSTS_ELEMENT_CSS);
        if (finds.size() > 0){
            final Element el = finds.get(0);
            return (Config.SITE_POSTS_ELEMENT_IS_OUTER) ? el.outerHtml() : el.html();
        }

        throw new NotFindElementException();
    }
}
