package edu.iis.mto.staticmock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import static org.mockito.Mockito.times;
import edu.iis.mto.staticmock.reader.NewsReader;
import edu.iis.mto.staticmock.reader.WebServiceNewsReader;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.*;

import org.codehaus.jackson.map.DeserializerFactory.Config;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@PrepareForTest({ConfigurationLoader.class,NewsReaderFactory.class,PublishableNews.class})
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

}
