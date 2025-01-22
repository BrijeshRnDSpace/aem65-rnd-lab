package com.aem.rnd.lab.core.servlets;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.Session;
import javax.servlet.Servlet;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component(service = Servlet.class, property = {
        ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/user/updateDisabled",
        ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET
})
public class     UpdateUserPropertyServlet extends SlingAllMethodsServlet {

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Reference
    private SlingRepository repository;


    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        String userId = request.getParameter("userId");

        if (StringUtils.isBlank(userId)) {
            response.setStatus(SlingHttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("User ID is required.");
            return;
        }

        try {
            if (StringUtils.isNotBlank(userId)) {
                Map<String, Object> serviceParams = new ConcurrentHashMap<>();
                serviceParams.put(ResourceResolverFactory.SUBSERVICE, "rndLabServiceUser");
                ResourceResolver resolver = resolverFactory.getServiceResourceResolver(serviceParams);
                final Session session = resolver.adaptTo(Session.class);
                final UserManager userManager = resolver.adaptTo(UserManager.class);
                if (ObjectUtils.allNotNull(session, userManager)) {
                    Authorizable user = userManager.getAuthorizable(userId);
                    if (Objects.nonNull(user)) {
                        user.setProperty("usersynchtest1", session.getValueFactory().createValue("DummyValue "));
                        response.getWriter().write("User property updated successfully." + user.getProperty("usersynchtest1"));
                        session.save();
                    }
                    session.logout();
                }

            } else {
                response.setStatus(SlingHttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("User not found.");
            }
        } catch (Exception e) {
            log.error("Error in CustomAuthenticationInfoPostProcessor.", e);
            log.error("Error updating user property", e);
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Failed to update user property.");
        }
    }
}