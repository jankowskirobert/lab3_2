package edu.iis.mto.staticmock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import static org.mockito.Mockito.times;
import edu.iis.mto.staticmock.reader.NewsReader;
import edu.iis.mto.staticmock.reader.WebServiceNewsReader;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.*;

import java.util.List;

import org.codehaus.jackson.map.DeserializerFactory.Config;
import static org.hamcrest.core.Is.*;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@PrepareForTest({ConfigurationLoader.class, NewsReaderFactory.class, PublishableNews.class})
@RunWith(PowerMockRunner.class)
public class TestNewsLoader {

    private ConfigurationLoader loader;
    private NewsReaderFactory factory;
    private final String configType = "WS";
    private final IncomingNews news = new IncomingNews();
    private WebServiceNewsReader web;
    private Configuration configuration;
    private PublishableNews publishedNews;

    private void initConfiguration() {
        configuration = mock(Configuration.class);
        when(configuration.getReaderType()).thenReturn("WS");
    }

    private IncomingNews prepareMessage(String message, SubsciptionType type) {
        IncomingNews news = new IncomingNews();
        IncomingInfo info = new IncomingInfo(message, type);
        news.add(info);
        return news;
    }

    private void initLoader() {
        mockStatic(ConfigurationLoader.class);
        loader = mock(ConfigurationLoader.class);
        when(ConfigurationLoader.getInstance()).thenReturn(loader);
        when(loader.loadConfiguration()).thenReturn(configuration);
    }

    @Before
    public void setUp() {
        initConfiguration();
        initLoader();
        web = mock(WebServiceNewsReader.class);
        mockStatic(NewsReaderFactory.class);
        factory = mock(NewsReaderFactory.class);
        when(web.read()).thenReturn(news);
        when(NewsReaderFactory.getReader(configuration.getReaderType())).thenReturn(web);
        mockStatic((PublishableNews.class));
        publishedNews = spy(new PublishableNews());
        when(PublishableNews.create()).thenReturn(publishedNews);
    }

    @Test
    public void testLoadNews() {
        NewsLoader newsLoader = new NewsLoader();
        publishedNews = newsLoader.loadNews();
        verify(loader, times(1)).loadConfiguration();

    }

    @Test
    public void testIfNewsForPublicIsAdded() {
        when(web.read()).thenReturn(prepareMessage("", SubsciptionType.NONE));
        NewsLoader newsLoader = new NewsLoader();
        publishedNews = newsLoader.loadNews();
        List<String> boxedNewsPublic = (List<String>) Whitebox.getInternalState(publishedNews, "publicContent");
        List<String> boxedNewsSubs = (List<String>) Whitebox.getInternalState(publishedNews, "subscribentContent");
        assertThat(boxedNewsPublic.size(), is(1));
        assertThat(boxedNewsSubs.size(), is(0));

    }

    @Test
    public void testIfNewsForSubscriberIsAdded() {
        when(web.read()).thenReturn(prepareMessage("", SubsciptionType.A));
        NewsLoader newsLoader = new NewsLoader();
        publishedNews = newsLoader.loadNews();
        List<String> boxedNewsPublic = (List<String>) Whitebox.getInternalState(publishedNews, "publicContent");
        List<String> boxedNewsSubs = (List<String>) Whitebox.getInternalState(publishedNews, "subscribentContent");
        assertThat(boxedNewsPublic.size(), is(0));
        assertThat(boxedNewsSubs.size(), is(1));

    }

    @Test
    public void testIfNewsForSubscribersIsAdded_callVerify() {
        when(web.read()).thenReturn(prepareMessage("", SubsciptionType.A));
        NewsLoader newsLoader = new NewsLoader();
        publishedNews = newsLoader.loadNews();
        verify(publishedNews, times(1)).addForSubscription("", SubsciptionType.A);
    }

    @Test
    public void testIfNewsForPublicIsAdded_callVerify() {
        when(web.read()).thenReturn(prepareMessage("", SubsciptionType.NONE));
        NewsLoader newsLoader = new NewsLoader();
        publishedNews = newsLoader.loadNews();
        verify(publishedNews, times(1)).addPublicInfo("");
    }

}
