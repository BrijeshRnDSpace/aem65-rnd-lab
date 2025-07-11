package com.aem.rnd.lab.core.servlets;

import com.aem.rnd.lab.core.models.UserModel;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.eval.PathPredicateEvaluator;
import com.day.cq.search.eval.TypePredicateEvaluator;
import com.day.cq.search.result.SearchResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.oak.spi.security.user.UserConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.*;

@Slf4j
@Component(service = Servlet.class, property = {
        ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/userlists",
        ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET
})
public class AEMUserServlet extends SlingAllMethodsServlet {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String DEFAULT_USER_LOCATION = "/home/users";


    @Reference
    private QueryBuilder queryBuilder;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (ResourceResolver resourceResolver = request.getResourceResolver()) {
           /* JSONObject requestBody = new JSONObject(request.getReader().lines().collect(Collectors.joining()));
            String filter = requestBody.optString("filter", "");
            int startIndex = requestBody.optInt("startindex", 0);
            int itemsPerPage = requestBody.optInt("itemperpage", -1);*/

            String filter = "groupid eq administrators";
            //String filter = "groupName co 'Admin' ";
            int startIndex = 0;
            String itemsPerPage = "-1";


            UserManager userManager = resourceResolver.adaptTo(UserManager.class);
            if (userManager == null) {
                response.getWriter().write("{\"error\": \"UserManager is unavailable\"}");
                return;
            }
            Session session = request.getResourceResolver().adaptTo(Session.class);
            SearchResult result = getAllUsers(session, String.valueOf(itemsPerPage), String.valueOf(startIndex));
            List<UserModel> users = new ArrayList<>();
            //result.getResources().forEachRemaining(userResource -> users.add(userResource.adaptTo(UserModel.class)));

            result.getResources().forEachRemaining(userResource -> {
                UserModel userModel = userResource.adaptTo(UserModel.class);
                if (isUserInGroup(resourceResolver, userModel.getUsername(), filter)) { // Replace "administrators" with your group name
                    users.add(userModel);
                }
            });

            response.getWriter().write(OBJECT_MAPPER.writeValueAsString(users));
        } catch (Exception e) {
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    public SearchResult getAllUsers(final Session session, String itemsPerPage, String startIndex) {
        final Map<String, String> query = new LinkedHashMap<>();
        query.put(PathPredicateEvaluator.PATH, DEFAULT_USER_LOCATION);
        query.put(TypePredicateEvaluator.TYPE, UserConstants.NT_REP_USER);
        query.put("property", "profile/email");
        query.put("property.operation", "exists");
        query.put("property.value", "");
        query.put("property.not", "true");
        query.put("p.limit", itemsPerPage);
        query.put("p.offset", startIndex);
        return this.queryBuilder.createQuery(PredicateGroup.create(query), session).getResult();
    }

    public boolean isUserInGroup(ResourceResolver resourceResolver, String userId, String filter) {
        try {
            String filterStringArray[] = extractGroupOperation(filter);
            String filterOperation = StringUtils.EMPTY;
            String group = StringUtils.EMPTY;

            if (filterStringArray != null && filterStringArray.length > 1) {
                filterOperation = filterStringArray[1];
                group = filterStringArray[2];
            }
            log.info(" filterOperation {} group {}", filterOperation, group);

            UserManager userManager = resourceResolver.adaptTo(UserManager.class);
            if (userManager == null) {
                log.error("UserManager is unavailable");
                return false;
            }

            Authorizable user = userManager.getAuthorizable(userId);
            Authorizable groupAble = userManager.getAuthorizable(group);

            if (filterOperation.equals("eq")) {
                if (user instanceof User && groupAble instanceof Group) {
                    Iterator<Group> userGroups = ((User) user).memberOf();
                    while (userGroups.hasNext()) {
                        if (userGroups.next().getID().equals(group)) {
                            return true;
                        }
                    }
                }
            }
        } catch (RepositoryException e) {
            log.error("Error checking user-group membership", e);
        }
        return false;
    }

    public static String[] extractGroupOperation(String filter) {
        if (filter == null || filter.isEmpty()) {
            return new String[0];
        }
        return filter.split("\\s+");
    }
}
