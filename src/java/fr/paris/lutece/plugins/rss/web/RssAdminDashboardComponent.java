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

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.rss.business.parameter.RssFeedParameterHome;
import fr.paris.lutece.plugins.rss.service.RssService;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.dashboard.admin.AdminDashboardComponent;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.security.ISecurityTokenService;
import fr.paris.lutece.portal.service.security.SecurityTokenHandler;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.user.AdminUserResourceIdService;
import fr.paris.lutece.util.ReferenceItem;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import jakarta.enterprise.inject.spi.CDI;
import jakarta.servlet.http.HttpServletRequest;


/**
 * The advanced parameters of the rss feeds in the admin dashboard
 */
public class RssAdminDashboardComponent extends AdminDashboardComponent
{
    private static final String TEMPLATE_ADMIN_DASHBOARD = "admin/plugins/rss/rss_admindashboard.html";
    private static final String ACTION_MODIFY_ADVANCED_PARAMETERS = "modifyAdvancedParameters";

    /**
     * Renders the advanced parameters for a user who may manage them
     *
     * @param user the admin user
     * @param request the HTTP request
     * @return the HTML of the component, empty for any other user
     */
    @Override
    public String getDashboardData( AdminUser user, HttpServletRequest request )
    {
        if ( !RBACService.isAuthorized( AdminUser.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                    AdminUserResourceIdService.PERMISSION_MANAGE_ADVANCED_PARAMETERS, (User) user ) )
        {
            return StringUtils.EMPTY;
        }

        Map<String, Object> model = new HashMap<>(  );

        for ( ReferenceItem param : RssFeedParameterHome.findAll( PluginService.getPlugin( RssService.PLUGIN_NAME ) ) )
        {
            model.put( param.getCode(  ), param.getName(  ) );
        }

        model.put( SecurityTokenHandler.MARK_CSRF_TOKEN,
            CDI.current( ).select( ISecurityTokenService.class ).get( ).getToken( request, ACTION_MODIFY_ADVANCED_PARAMETERS ) );

        return AppTemplateService.getTemplate( TEMPLATE_ADMIN_DASHBOARD, user.getLocale(  ), model ).getHtml(  );
    }
}
