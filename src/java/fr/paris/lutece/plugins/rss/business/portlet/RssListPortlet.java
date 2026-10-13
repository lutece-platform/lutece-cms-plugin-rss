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
package fr.paris.lutece.plugins.rss.business.portlet;

import fr.paris.lutece.plugins.rss.business.RssFeedHome;
import fr.paris.lutece.portal.business.portlet.PortletHtmlContent;

import java.util.Map;

import jakarta.enterprise.inject.spi.CDI;
import jakarta.servlet.http.HttpServletRequest;


/**
 * This class represents business objects RssListPortlet
 */
public class RssListPortlet extends PortletHtmlContent
{
    private static final String TEMPLATE_DEFAULT = "skin/plugins/rss/portlet/portlet_rss_list.html";
    private static final String MARK_RSS_FEEDS_LIST = "rss_feeds_list";

    /**
     * Sets the identifier of the portlet type to value specified
     */
    public RssListPortlet(  )
    {
        setPortletTypeId( CDI.current( ).select( RssListPortletHome.class ).get( ).getPortletTypeId(  ) );
    }

    /**
     * Renders the list of the active feeds
     *
     * @param request the HTTP request
     * @return the HTML of the portlet
     */
    @Override
    public String getHtmlContent( HttpServletRequest request )
    {
        Map<String, Object> model = createPortletModel( );
        model.put( MARK_RSS_FEEDS_LIST, RssFeedHome.getRssFeeds( ) );

        return renderTemplate( request, TEMPLATE_DEFAULT, model );
    }

    /**
     * Updates the current instance of the RssListPortlet object
     */
    public void update(  )
    {
        CDI.current( ).select( RssListPortletHome.class ).get( ).update( this );
    }

    /**
     * Removes the current instance of the RssListPortlet object
     */
    @Override
    public void remove(  )
    {
        CDI.current( ).select( RssListPortletHome.class ).get( ).remove( this );
    }
}
