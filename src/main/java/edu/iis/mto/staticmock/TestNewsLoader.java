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

    private void initConfiguration() {
        configuration = mock(Configuration.class);
        when(configuration.getReaderType()).thenReturn("WS");
    }

    private IncomingNews prepareMessage(SubsciptionType type) {
        IncomingNews news = new IncomingNews();
        IncomingInfo info = new IncomingInfo("TEST PURPOSE", type);
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
    }

    @Test
    public void testLoadNews() {
        NewsLoader newsLoader = new NewsLoader();
        PublishableNews news = newsLoader.loadNews();
        verify(loader, times(1)).loadConfiguration();

    }

    @Test
    public void testIfNewsForPublicIsAdded() {
        NewsLoader newsLoader = new NewsLoader();
        PublishableNews news = newsLoader.loadNews();
        when(web.read()).thenReturn(prepareMessage(SubsciptionType.NONE));
        List<String> boxedNewsPublic = (List<String>) Whitebox.getInternalState(news, "publicContent");
        List<String> boxedNewsSubs = (List<String>) Whitebox.getInternalState(news, "subscribentContent");
        assertThat(boxedNewsPublic.size(), is(1));
        assertThat(boxedNewsSubs.size(), is(0));

    }

    
    
}
