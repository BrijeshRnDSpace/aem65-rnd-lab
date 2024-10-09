package com.aem.rnd.lab.core.services.impl;

import com.adobe.granite.oauth.server.Scope;
import com.adobe.granite.oauth.server.ScopeWithPrivileges;
import org.apache.jackrabbit.api.security.user.User;
import org.osgi.service.component.annotations.Component;
import javax.servlet.http.HttpServletRequest;

@Component(service = Scope.class)
public class DAMReadScope implements ScopeWithPrivileges {

    private static final String DAM_RESOURCE_URI = "/content/dam/digital";
    private static final String DAM_RESOURCE_READ_SCOPE_NAME = "dam_read";

    public DAMReadScope() {

    }

    @Override
    public String getDescription(HttpServletRequest arg0) {
        return "Read DAM Assets";
    }

    @Override
    public String getEndpoint() {
        return null;
    }

    @Override
    public String getName() {
        return DAM_RESOURCE_READ_SCOPE_NAME;
    }

    @Override
    public String getResourcePath(User user) {
        return DAM_RESOURCE_URI;
    }

    @Override
    public String[] getPrivileges() {
        return new String[]{"jcr:read"};
    }
}