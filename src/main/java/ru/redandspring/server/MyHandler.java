package ru.redandspring.server;

import org.apache.commons.lang3.math.NumberUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.redandspring.services.Box;
import ru.redandspring.services.ServiceException;
import ru.redandspring.services.StatisticPage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MyHandler extends AbstractHandler {

    private static final Logger log = LoggerFactory.getLogger(MyHandler.class);

    private Box box = Box.getInstance();

    private final StatisticPage page = new StatisticPage();

    @Override
    public void handle(final String target, final Request request, final HttpServletRequest sr, final HttpServletResponse response)
            throws IOException {
        log.info("handle {}", target);

        try {
            switch (target){ // NOSONAR
                case "/stat": openHandler(request, response); break;
                case "/reject": rejectHandler(request, response); break;
                default: response.setStatus(HttpServletResponse.SC_FORBIDDEN); break;
            }
        } catch (ServiceException e) {
            showError(response, e);
        }
    }

    private void rejectHandler(final Request request, final HttpServletResponse response) throws ServiceException {

        String query = request.getMetaData().getURI().getQuery();

        if(NumberUtils.isDigits(query)){
            boolean remove = box.remove(NumberUtils.toLong(query));
            if (remove){
                log.info("rejectHandler(): remove adv id={}", query);
            }
        }

        try {
            response.sendRedirect("/stat");
        } catch (IOException e) {
            throw new ServiceException("rejectHandler(): Redirect is error", e);
        }

    }

    private void openHandler(final Request request, final HttpServletResponse response) throws ServiceException {

        response.setContentType("text/html; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        request.setHandled(true);

        try {
            final String html = page.resultPage();
            response.getWriter().println(html);
        } catch (IOException e) {
            throw new ServiceException("openHandler(): open handler is error", e);
        }
    }

    private void showError(final HttpServletResponse response, final Throwable e) throws IOException {
        log.error("Error:", e);
        response.getWriter().println("<h1>Что-то пошло не так</h1>" + e.getMessage());
    }
}
