package com.aem.aem.lab.core.schedulers;

import com.aem.aem.lab.core.config.WeatherSchedulerConfig;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.PageManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.apache.sling.api.resource.*;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

@Slf4j
@Designate(ocd = WeatherSchedulerConfig.class)
@Component(service = Runnable.class, immediate = true)
public class WeatherScheduler implements Runnable {

    //private static final String API_URL_ = "https://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&appid=b28bda5814ae928aef2c0a770cdccc77";
    private static final String CONTENT_RND_LAB = "/content/rnd_lab/";
    private String schedulingExpression;
    private String apiURL;
    private String appID;

        @Reference
    private ResourceResolverFactory resourceResolverFactory;
    @Reference
    private PageManagerFactory pageManagerFactory;
    @Reference
    private Scheduler scheduler;
    private int schedulerID;

    @Activate
    protected void activate(WeatherSchedulerConfig config) {
        this.schedulerID = this.getClass().getName().hashCode();
        try {
            this.schedulingExpression = config.scheduler_expressions();
            this.apiURL = config.apiURL();
            this.appID = config.appId();
            final ScheduleOptions scheduleOptions = this.scheduler.EXPR(this.schedulingExpression);
            scheduleOptions.name(String.valueOf(this.schedulerID));
            this.scheduler.schedule(this, scheduleOptions);
            log.debug("Scheduler added successfully");
        } catch (Exception e) {
            log.error("Error Scheduling the WeatherScheduler", e);
        }
    }

    @Deactivate
    public void deactivate(final WeatherSchedulerConfig config) {
        this.scheduler.unschedule(String.valueOf(this.schedulerID));
    }

    @Override
    public void run() {
        Map<String, Object> param = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, "rndLabServiceUser");

        try (ResourceResolver resolver = resourceResolverFactory.getServiceResourceResolver(param)) {
            Resource parentResource = resolver.getResource(CONTENT_RND_LAB);
            log.info("Root resource found at {}  ", parentResource.getPath());
            if (parentResource == null) {
                log.warn("Root resource {} not found", CONTENT_RND_LAB);
                return;
            }

            PageManager pageManager = pageManagerFactory.getPageManager(resolver);
            if (pageManager == null) {
                log.warn("PageManager could not be obtained");
                return;
            }

            for (Resource childResource : parentResource.getChildren()) {
                Page page = pageManager.getContainingPage(childResource);
                if (page == null) {
                    log.warn("Resource {} is not a page", childResource.getPath());
                    continue;
                }
                String weatherData = fetchWeatherData(page.getName());
                if (weatherData != null) {
                    updateJcr(page, resolver, weatherData);
                }
            }
        } catch (LoginException e) {
            log.error("LoginException in EuroWingWeatherScheduler: ", e);
        } catch (Exception e) {
            log.error("Error in EuroWingWeatherScheduler: ", e);
        }
    }


    private String fetchWeatherData(String city) throws Exception {

        String urlString = String.format(apiURL + "?q=%s&units=metric&appid=%s", city, appID);
        log.info("Weather API URL " + urlString);
        URL url = new URL(urlString);
        log.info("Weather API URL " + urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            JSONObject jsonResponse = new JSONObject(response.toString());
            return jsonResponse.getJSONObject("main").get("temp").toString();
        } else {
            log.error("GET request not worked, Response Code: " + responseCode);
            return null;
        }
    }

    private void updateJcr(Page pageResource, ResourceResolver resourceResolver, String temperature) {
        try {
            Resource contentResource = pageResource.getContentResource();
            Resource resource = resourceResolver.getResource(contentResource.getPath());

            if (resource != null) {
                ModifiableValueMap properties = resource.adaptTo(ModifiableValueMap.class);
                if (properties != null) {
                    properties.put("temperature", temperature);
                    resourceResolver.commit();
                    log.info("Weather data updated successfully in JCR  {} pageResource " + pageResource.getPath());
                }
            }
        } catch (Exception e) {
            log.error("Error while updating JCR: ", e);
        }
    }
}
