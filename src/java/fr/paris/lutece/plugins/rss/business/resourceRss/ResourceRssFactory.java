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
package fr.paris.lutece.plugins.rss.business.resourceRss;

import fr.paris.lutece.portal.business.rss.IResourceRss;
import fr.paris.lutece.portal.business.rss.IResourceRssType;
import fr.paris.lutece.portal.service.util.AppLogService;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;


/**
 * ResourceRssFactory
 */
@ApplicationScoped
public class ResourceRssFactory implements IResourceRssFactory
{
    @Inject
    @Any
    private Instance<IResourceRssType> _resourceRssTypes;

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public IResourceRss getResourceRss( String strKey )
    {
        if ( strKey == null )
        {
            return null;
        }

        IResourceRss resourceRss = null;
        IResourceRssType resourceRssType = _resourceRssTypes.stream( ).filter( rssType -> strKey.equals( rssType.getKey( ) ) ).findFirst( )
                .orElse( null );

        if ( resourceRssType != null )
        {
            try
            {
                resourceRss = (IResourceRss) Class.forName( resourceRssType.getClassName( ) ).getDeclaredConstructor( ).newInstance( );
                resourceRss.setResourceRssType( resourceRssType );
            }
            catch( ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e )
            {
                AppLogService.error( e.getMessage( ), e );
            }
        }

        return resourceRss;
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public Collection<IResourceRssType> getAllResourceRssType( )
    {
        return _resourceRssTypes.stream( ).toList( );
    }
}
