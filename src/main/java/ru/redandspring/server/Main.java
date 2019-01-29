package ru.redandspring.server;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.redandspring.exception.ServiceException;
import ru.redandspring.model.Config;
import ru.redandspring.services.ParsingPageAdv;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static ru.redandspring.model.Config.TIMEOUT_EACH_ROW;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private static final int SERVER_PORT = 8724;

    private static final ParsingPageAdv parsingPageAdv = new ParsingPageAdv();

    public static void main(String[] args) {

        try
        {
            final Main main = new Main();
            main.schedule();
            main.server();
        }
        catch (Exception e)
        {
            log.error("Что-то пошло не так", e);
        }
    }

    private void schedule() {

        Thread run = new Thread(() -> {
            while(true){
                try {
                    for (int i = 0; i < Config.COUNT_PAGES; i++) {
                        parsingPageAdv.parsePosts(i);
                        Thread.sleep(TIMEOUT_EACH_ROW);
                    }
                    final int sleepTime = parsingPageAdv.setNewStart();
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    log.error("App Interrupted", e);
                } catch (ServiceException e) {
                    log.error("Что-то пошло не так", e);
                }
            }
        });
        run.start();
    }

    private void server() throws Exception
    {

        Server server = new Server(SERVER_PORT);
        /// -----------------------------------------------

        ResourceHandler rh0 = new ResourceHandler();
        ContextHandler context0 = new ContextHandler();
        context0.setContextPath("/static");
        context0.setBaseResource(getWebRootUri());
        context0.setHandler(rh0);

        ContextHandler context1 = new ContextHandler();
        context1.setContextPath("/");
        context1.setHandler(new MyHandler());

        ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.setHandlers(new Handler[] { context0, context1 });

        server.setHandler(contexts);

        server.start();

        log.info("Server started at {} port", SERVER_PORT);
        log.info("Target page http://localhost:{}/stat", SERVER_PORT);

        server.join();
    }

    private static Resource getWebRootUri() throws URISyntaxException, MalformedURLException {

        URL webRootLocation = Main.class.getResource("/static/index.html");
        if (webRootLocation == null)
        {
            throw new IllegalStateException("Unable to determine static URL location");
        }

        URI webRootUri = URI.create(webRootLocation.toURI().toASCIIString().replaceFirst("/index.html$","/"));
        log.info("webRootUri: {}", webRootUri);


        return Resource.newResource(webRootUri);
    }

}
