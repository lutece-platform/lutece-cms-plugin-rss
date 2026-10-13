/*
 * Copyright (c) 2002-2014, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.rss.web;

import fr.paris.lutece.plugins.rss.business.RssFeed;
import fr.paris.lutece.plugins.rss.business.RssFeedHome;
import fr.paris.lutece.plugins.rss.service.RssContent;
import fr.paris.lutece.plugins.rss.service.RssContentService;
import fr.paris.lutece.plugins.rss.service.RssFeedParser;
import fr.paris.lutece.plugins.rss.service.RssService;
import fr.paris.lutece.portal.service.content.PageData;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.includes.PageInclude;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.enterprise.inject.spi.CDI;
import jakarta.servlet.http.HttpServletRequest;


/**
 * This class provides the user interface to manage rss features
 */
public class RssFeedInclude implements PageInclude
{
    private static final String MARK_FEED = "feed";
    private static final String MARK_PLUGIN = "plugin";
    private static final String MARK_LOCALE = "locale";

    /**
     * The template used to render a feed in the page
     */
    private static final String TEMPLATE_RSS_INCLUDE = "skin/plugins/rss/rss_feed_include.html";

    private static final String PROPERTY_RSS_MARKER_PREFIX = "rss.include.marker.prefix";

    private static final String MARK_DEFAULT_RSS_MARKER_PREFIX = "rss_";

    private static final int CONSTANTE_RSS_STATUT_OK = 0;

    /**
     * Substitue specific Freemarker markers in the page template.
     * @param rootModel the HashMap containing markers to substitute
     * @param data A PageData object containing applications data
     * @param nMode The current mode
     * @param request The HTTP request
     */
    @Override
    public void fillTemplate( Map<String, Object> rootModel, PageData data, int nMode, HttpServletRequest request )
    {
        Plugin plugin = PluginService.getPlugin( RssService.PLUGIN_NAME );
        Locale locale = getLocale( request );
        List<RssFeed> listRss = RssFeedHome.getRssFeeds(  );

        for ( RssFeed rssFeed : listRss )
        {
            if ( rssFeed.getLastFetchStatus(  ) == CONSTANTE_RSS_STATUT_OK )
            {
                rootModel.put( getRssMarkerPrefix(  ) + String.valueOf( rssFeed.getId(  ) ),
                    getTemplateHtmlForRss( rssFeed, plugin, locale ) );
            }
        }
    }

    /**
     * Get the prefix of the RSS marker
     * @return The prefix of the RSS marker
     */
    public static String getRssMarkerPrefix(  )
    {
        return AppPropertiesService.getProperty( PROPERTY_RSS_MARKER_PREFIX, MARK_DEFAULT_RSS_MARKER_PREFIX );
    }

    /**
     * Renders a feed
     * @param rss The rss feed
     * @param plugin The plugin
     * @param locale The locale
     * @return The HTML of the feed
     */
    private String getTemplateHtmlForRss( RssFeed rss, Plugin plugin, Locale locale )
    {
        Map<String, Object> model = new HashMap<>(  );
        RssContent rssContent = CDI.current( ).select( RssContentService.class ).get( ).getRssContent( String.valueOf( rss.getId(  ) ) );

        if ( rssContent != null )
        {
            model.put( MARK_FEED, RssFeedParser.parse( rssContent.getContent(  ) ) );
        }

        model.put( MARK_PLUGIN, plugin );
        model.put( MARK_LOCALE, locale );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_RSS_INCLUDE, locale, model );

        return templateList.getHtml(  );
    }

    /**
     * Return the locale
     * @param request The HTTP request
     * @return The locale of the request, or the default locale without request
     */
    private Locale getLocale( HttpServletRequest request )
    {
        return ( request != null ) ? request.getLocale(  ) : I18nService.getDefaultLocale(  );
    }
}
