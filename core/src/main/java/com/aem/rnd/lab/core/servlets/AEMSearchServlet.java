package com.aem.rnd.lab.core.servlets;

import com.day.cq.dam.api.DamConstants;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.eval.PathPredicateEvaluator;
import com.day.cq.search.eval.TypePredicateEvaluator;
import com.day.cq.search.result.SearchResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component(service = Servlet.class, property = {
        ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/searchResult",
        ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET
})
public class AEMSearchServlet extends SlingAllMethodsServlet {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    @Reference
    private QueryBuilder queryBuilder;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (ResourceResolver resourceResolver = request.getResourceResolver()) {
            QueryBuilder queryBuilder = resourceResolver.adaptTo(QueryBuilder.class);
            Map<String, String> queryMap = new HashMap<>();

            queryMap.put(PathPredicateEvaluator.PATH, "/content/dam/digital");
            queryMap.put(TypePredicateEvaluator.TYPE, DamConstants.NT_DAM_ASSET);
            queryMap.put("p.limit", "-1");
            queryMap.put("p.offset", "0");
            Query query = queryBuilder.createQuery(PredicateGroup.create(queryMap), resourceResolver.adaptTo(Session.class));
            SearchResult result = query.getResult();

            response.getWriter().write(OBJECT_MAPPER.writeValueAsString(result));
        } catch (Exception e) {
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

}
