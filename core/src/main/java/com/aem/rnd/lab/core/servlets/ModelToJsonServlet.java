package com.aem.rnd.lab.core.servlets;


import com.aem.rnd.lab.core.models.ErrorModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;

import javax.servlet.Servlet;
import java.io.IOException;

@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.methods=" + HttpConstants.METHOD_GET,
                "sling.servlet.paths=/bin/example/modeljson"
        }
)
@ServiceDescription("Servlet to convert Sling Model to JSON")
public class ModelToJsonServlet extends SlingAllMethodsServlet {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        String[] schemas = {"this is value of schemas"};
        ErrorModel model = new ErrorModel("Status happy Just Be Calm", "If its a error, no issue  ");

        if (model != null) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(model));

        } else {
            response.setStatus(SlingHttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\":\"Model not found\"}");
        }
    }
}
