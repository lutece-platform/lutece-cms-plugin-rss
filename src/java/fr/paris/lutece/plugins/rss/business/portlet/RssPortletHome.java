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

import fr.paris.lutece.portal.business.portlet.IPortletInterfaceDAO;
import fr.paris.lutece.portal.business.portlet.PortletHome;
import fr.paris.lutece.portal.business.portlet.PortletTypeHome;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.CDI;

import java.util.List;


/**
 * This class provides instances management methods for Rss objects
 */
@ApplicationScoped
public class RssPortletHome extends PortletHome
{
    // Static variable pointed at the DAO instance
    private static IRssPortletDAO _dao = CDI.current( ).select( IRssPortletDAO.class ).get( );


    /**
     * Constructor
     */
    public RssPortletHome(  )
    {
    }

    /**
     * Returns the identifier of the portlet type
     *
     * @return the portlet type identifier
     */
    public String getPortletTypeId(  )
    {
        String strCurrentClassName = this.getClass(  ).getName(  );
        String strPortletTypeId = PortletTypeHome.getPortletTypeId( strCurrentClassName );

        return strPortletTypeId;
    }

    /**
     * Returns the instance of the portlet DAO singleton
     *
     * @return the instance of the DAO singleton
     */
    public IPortletInterfaceDAO getDAO(  )
    {
        return _dao;
    }

    /**
     * Checks whether the rss feed is linked to a portlet
     * @return The boolean result
     * @param nIdRssFeed The identifier of the rss feed
     */
    public static boolean checkNoPortletLinked( int nIdRssFeed )
    {
        return _dao.checkLinkedPortlet( nIdRssFeed ).isEmpty(  );
    }

    /**
     * Checks whether the rss feed is linked to a portlet
     * @return The linked portlet if found, <code>null</code> otherwise
     * @param nIdRssFeed The identifier of the rss feed
     */
    public static List<RssPortlet> checkLinkedPortlet( int nIdRssFeed )
    {
        return _dao.checkLinkedPortlet( nIdRssFeed );
    }

    /**
     * Tells whether a portlet of this type has the given identifier
     *
     * @param nIdPortlet the identifier of the portlet
     * @return true when the portlet exists
     */
    public static boolean checkPortletExists( int nIdPortlet )
    {
        return ( nIdPortlet > 0 ) && ( _dao.load( nIdPortlet ).getId(  ) == nIdPortlet );
    }
}
