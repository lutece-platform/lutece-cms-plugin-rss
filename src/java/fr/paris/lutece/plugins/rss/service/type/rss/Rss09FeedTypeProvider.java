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
package fr.paris.lutece.plugins.rss.service.type.rss;

import com.sun.syndication.feed.rss.Content;
import com.sun.syndication.feed.rss.Guid;
import com.sun.syndication.feed.rss.Item;

import fr.paris.lutece.portal.business.rss.IFeedResourceItem;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import jakarta.enterprise.context.ApplicationScoped;


/**
 *
 * RssFeedTypeProvider
 *
 */
@ApplicationScoped
public class Rss09FeedTypeProvider extends AbstractRssFeedTypeProvider
{
    private static final String RSS_0_9_PREFIX = "rss_0.9";
    private static final int ITEM_TITLE_MAX_LENGTH = 100;
    private static final int MAX_NUMBER_ITEMS = 15;

    /**
     * {@inheritDoc}
     */
    public boolean isInvoked( String strFeedType )
    {
        return RSS_0_9_PREFIX.equals( strFeedType );
    }

    /**
     * {@inheritDoc}
     */
    public List<Item> getRSSItems( List<IFeedResourceItem> listItems, int nMaxItems )
    {
        List<Item> listEntries = new ArrayList<Item>(  );

        boolean bLimit = MAX_NUMBER_ITEMS != MAX_ITEM_UNLIMITED;
        int nIndex = 1;

        for ( IFeedResourceItem item : listItems )
        {
            listEntries.add( getRSSItem( item ) );

            if ( bLimit )
            {
                if ( nIndex < MAX_NUMBER_ITEMS )
                {
                    nIndex++;
                }
                else
                {
                    break;
                }
            }
        }

        return listEntries;
    }

    /**
     * {@inheritDoc}
     */
    public Item getRSSItem( IFeedResourceItem resourceItem )
    {
        Item item = new Item(  );

        String strTitle = StringUtils.EMPTY;

        if ( StringUtils.isNotBlank( resourceItem.getTitle(  ) ) &&
                ( resourceItem.getTitle(  ).length(  ) >= ITEM_TITLE_MAX_LENGTH ) )
        {
            strTitle = resourceItem.getTitle(  ).substring( 0, ITEM_TITLE_MAX_LENGTH - 1 );
        }
        else
        {
            strTitle = resourceItem.getTitle(  );
        }

        item.setTitle( strTitle );
        item.setPubDate( resourceItem.getDate(  ) );
        item.setLink( resourceItem.getLink(  ) );

        Guid guid = new Guid(  );
        guid.setValue( resourceItem.getGUID(  ) );
        guid.setPermaLink( false );
        item.setGuid( guid );

        Content content = new Content(  );
        content.setValue( resourceItem.getDescription(  ) );
        item.setContent( content );

        return item;
    }
}
