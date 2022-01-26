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
package fr.paris.lutece.plugins.rss.service.type.atom;

import com.sun.syndication.feed.atom.Entry;

import fr.paris.lutece.portal.business.rss.IFeedResourceItem;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 *
 * AtomFeedTypeProvider
 *
 */
public class DefaultAtomFeedTypeProvider extends AbstractAtomFeedTypeProvider
{
    private static final String ATOM_PREFIX = "atom";

    /**
     * {@inheritDoc}
     */
    public boolean isInvoked( String strFeedType )
    {
        return StringUtils.isNotBlank( strFeedType ) && strFeedType.startsWith( ATOM_PREFIX );
    }

    /**
    * {@inheritDoc}
    */
    public List<Entry> getATOMEntries( List<IFeedResourceItem> listItems, int nMaxItems )
    {
        List<Entry> listEntries = new ArrayList<Entry>(  );

        boolean bLimit = nMaxItems != MAX_ITEM_UNLIMITED;
        int nIndex = 1;

        for ( IFeedResourceItem item : listItems )
        {
            listEntries.add( getATOMEntry( item ) );

            if ( bLimit )
            {
                if ( nIndex < nMaxItems )
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
    public Entry getATOMEntry( IFeedResourceItem resourceItem )
    {
        Entry entry = new Entry(  );

        entry.setTitle( resourceItem.getTitle(  ) );
        entry.setPublished( resourceItem.getDate(  ) );

        com.sun.syndication.feed.atom.Content summary = new com.sun.syndication.feed.atom.Content(  );
        summary.setValue( resourceItem.getTitle(  ) );
        entry.setSummary( summary );

        com.sun.syndication.feed.atom.Content content = new com.sun.syndication.feed.atom.Content(  );
        content.setValue( resourceItem.getDescription(  ) );
        // we don't handle multi-content
        entry.setContents( Collections.singletonList( content ) );

        return entry;
    }
}
