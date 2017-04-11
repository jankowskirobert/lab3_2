package edu.iis.mto.staticmock;

import org.powermock.core.classloader.annotations.PrepareForTest;

import edu.iis.mto.staticmock.reader.NewsReader;


public class NewsLoader {

    public PublishableNews loadNews() {
        Configuration config = ConfigurationLoader.getInstance().loadConfiguration();
        NewsReader reader = NewsReaderFactory.getReader(config.getReaderType());
        IncomingNews news = reader.read();
        PublishableNews publishable = prepareForPublish(news);
        return publishable;

    }

    private PublishableNews prepareForPublish(IncomingNews news) {
        PublishableNews publishable = PublishableNews.create();
        for (IncomingInfo snew : news.elems()) {
            if (!snew.requiresSubsciption()) {
                publishable.addPublicInfo(snew.getContent());
            } else {
                publishable.addForSubscription(snew.getContent(), snew.getSubscriptionType());
            }
        }

        return publishable;
    }
}
