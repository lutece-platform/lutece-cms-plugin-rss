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
package fr.paris.lutece.plugins.rss.business;

import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupResource;

import java.sql.Timestamp;


/**
 *  This class represents business object RssGeneratedFile
 */
public class RssGeneratedFile implements AdminWorkgroupResource
{

    ////////////////////////////////////////////////////////////////////////////////
    // Constants
    private int _nId;
    private int _nPortletId;
    private int _nState;
    private String _strName;
    private String _strDescription;
    private String _strPortletName;
    private Timestamp _dateUpdate;
    private String _strWorkgroup;
    private String _strTypeResourceRss;
    private String _strFeedType;
    private String _strEncoding;
    private int _nMaxItems;

    /**
     * Returns the identifier of this pushRss object
     * @return the identifier of this pushRss object
     */
    public int getId(  )
    {
        return _nId;
    }

    /**
     * Sets the identifier of the pushRss object to the specified int.
     * @param nId the new identifier
     */
    public void setId( int nId )
    {
        _nId = nId;
    }

    /**
     * Returns the identifier of the portlet that corresponds to this pushRss object
     * @return the identifier of the portlet that corresponds to this pushRss object
     */
    public int getPortletId(  )
    {
        return _nPortletId;
    }

    /**
     * Sets the identifier of the portlet for this pushRss object to the specified int.
     * @param nPortletId the new identifier of the portlet
     */
    public void setPortletId( int nPortletId )
    {
        _nPortletId = nPortletId;
    }

    /**
     * Returns the name of this pushRss object
     * @return the name of this pushRss object
     */
    public String getName(  )
    {
        return _strName;
    }

    /**
     * Returns the description of this pushRss object
     * @return the description of this pushRss object
     */
    public String getPortletName(  )
    {
        return _strPortletName;
    }

    /**
     * Returns the description of this pushRss object
     * @return the description of this pushRss object
     */
    public String getDescription(  )
    {
        return _strDescription;
    }

    /**
     * Sets the name of this pushRss object to the specified String
     * @param strName the new name of this pushRss object
     */
    public void setName( String strName )
    {
        _strName = strName;
    }

    /**
     * Sets the portlet name of this pushRss object to the specified String
     * @param strPortletName the new name of this pushRss object
     */
    public void setPortletName( String strPortletName )
    {
        _strPortletName = strPortletName;
    }

    /**
     * Sets the description of this pushRss object to the specified String
     * @param strDescription the new description of this pushRss object
     */
    public void setDescription( String strDescription )
    {
        _strDescription = strDescription;
    }

    /**
     * Returns the state of this pushRss object
     * @return the state of this pushRss object
     */
    public int getState(  )
    {
        return _nState;
    }

    /**
     * Sets the state of this pushRss object to the specified int
     * @param nState the new state of this pushRss object
     */
    public void setState( int nState )
    {
        _nState = nState;
    }

    /**
     * Returns the date of update of this pushRss object
     * @return the date of update of this pushRss object
     */
    public Timestamp getUpdateDate(  )
    {
        return _dateUpdate;
    }

    /**
     * Sets the date of the update of this pushRss object to the specified Date
     * @param dateUpdate the new update date of this pushRss object
     */
    public void setUpdateDate( Timestamp dateUpdate )
    {
        _dateUpdate = dateUpdate;
    }

    /**
    *
    * @return the work group associate to the category
    */
    public String getWorkgroup(  )
    {
        return _strWorkgroup;
    }

    /**
     * set  the work group associate to the category
     * @param workGroup  the work group associate to the category
     */
    public void setWorkgroup( String workGroup )
    {
        _strWorkgroup = workGroup;
    }

    /**
    *
    * @return the type of resource rss
    */
    public String getTypeResourceRss(  )
    {
        return _strTypeResourceRss;
    }

    /**
     * set the type of resource rss
     * @param strTypeResourceRss  type of resource rss
     */
    public void setTypeResourceRss( String strTypeResourceRss )
    {
        _strTypeResourceRss = strTypeResourceRss;
    }

    /**
     * Gets the max items
     * @return the max item
     */
    public int getMaxItems(  )
    {
        return _nMaxItems;
    }

    /**
     * Sets the max items
     * @param nMaxItems the max items
     */
    public void setMaxItems( int nMaxItems )
    {
        this._nMaxItems = nMaxItems;
    }

    /**
     * The feed type
     * @return the feed type
     * @see FeedType#getType()
     */
    public String getFeedType(  )
    {
        return _strFeedType;
    }

    /**
     * The feed type
     * @param strFeedType the feed type
     * @see FeedType#getType()
     */
    public void setFeedType( String strFeedType )
    {
        _strFeedType = strFeedType;
    }

    /**
     * Gets the encoding
     * @return the encoding
     */
    public String getEncoding(  )
    {
        return _strEncoding;
    }

    /**
     * Gets the encoding
     * @param strEncoding the encoding
     */
    public void setEncoding( String strEncoding )
    {
        this._strEncoding = strEncoding;
    }
}
