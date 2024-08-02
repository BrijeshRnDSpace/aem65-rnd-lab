package com.aem.aem.lab.core.servlets;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Component(service = {Servlet.class},
        property = {
                ServletResolverConstants.SLING_SERVLET_PATHS + "=" + FetchTemperature.SERVLET_PATH,
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
        })
@ServiceDescription("FetchTemperature Servlet")
public class FetchTemperature extends SlingSafeMethodsServlet {
    private static final long serialVersionUID = 1L;
    static final String SERVLET_PATH = "/bin/practiceweather";

    static final String CITY = "city";
    private static final String APPLICATION_JSON = "application/json";
    private static final String CONTENT_EURO_WING = "/content/rnd_lab/";

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException {

        response.setContentType(APPLICATION_JSON);
        PrintWriter writer = response.getWriter();

        String city = request.getParameter(CITY) != null ? request.getParameter(CITY) : "Munich";
        log.info("City {} from query parameter", city);
        Resource pageResource = request.getResourceResolver().getResource(CONTENT_EURO_WING + city.toLowerCase());

        PageManager pageManager = request.getResourceResolver().adaptTo(PageManager.class);
        if (pageResource != null) {
            Page currentPage = pageManager.getContainingPage(pageResource);
            if (currentPage != null) {
                Resource contentResource = currentPage.getContentResource();
                log.info("Landing page path " + contentResource.getPath());
                ValueMap properties = contentResource.adaptTo(ValueMap.class);
                String units = properties.get("units", String.class);

                String temperature = properties.get("temperature", String.class);
                if ("F".equalsIgnoreCase(units)) {
                    temperature = convertCelsiusToFahrenheit(temperature);
                }
                if (temperature != null) {
                    writer.write("{\"temperature\":\"" + temperature + "\"}");
                } else {
                    writer.write("{\"error\":\"Temperature property not found\"}");
                }
            }
        }
    }

    public static String convertCelsiusToFahrenheit(String celsiusString) {
        try {
            double celsius = Double.parseDouble(celsiusString);
            double fahrenheit = (celsius * 9 / 5) + 32;
            return String.format("%.2f", fahrenheit);
        } catch (NumberFormatException e) {
            return "Invalid Temperature input";
        }
    }
}