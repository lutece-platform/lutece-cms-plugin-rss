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

import fr.paris.lutece.plugins.rss.business.RssGeneratedFile;
import fr.paris.lutece.plugins.rss.business.RssGeneratedFileHome;
import fr.paris.lutece.portal.service.content.PageData;
import fr.paris.lutece.portal.service.includes.PageInclude;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.io.File;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;


/**
 * This class provides the user interface to manage rss features
 */
public class RssInclude implements PageInclude
{
    /**
     * The bookmark in the page frameset
     */
    private static final String MARK_RSS = "rss";

    /**
     * The list of the rss files
     */
    private static final String MARK_RSS_FILES_LIST = "rss_list";

    /**
     * The template used to generate the rss links in the html code
     */
    private static final String TEMPLATE_RSS_INCLUDE = "skin/plugins/rss/rss_include.html";

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
        Map<String, Object> model = new HashMap<>(  );
        List<RssGeneratedFile> listRssGeneratedFile = RssGeneratedFileHome.getRssFileList(  );
        List<RssGeneratedFile> listRemove = new ArrayList<RssGeneratedFile>(  );

        for ( RssGeneratedFile rssGeneratedFile : listRssGeneratedFile )
        {
            File file = new File( AppPathService.getWebAppPath(  ) + "/plugins/rss/" + rssGeneratedFile.getName(  ) );

            if ( !file.exists(  ) )
            {
                listRemove.add( rssGeneratedFile );
            }
        }

        listRssGeneratedFile.removeAll( listRemove );
        model.put( MARK_RSS_FILES_LIST, listRssGeneratedFile );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_RSS_INCLUDE, Locale.getDefault(  ), model );
        rootModel.put( MARK_RSS, templateList.getHtml(  ) );
    }
}
