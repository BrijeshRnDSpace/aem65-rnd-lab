package com.aem.rnd.lab.core.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.PageManagerFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.*;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;

@Slf4j
@Getter
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class WeatherModel {
    @ValueMapValue
    @Default(values = "Stockholm")
    private String city;

    @ValueMapValue
    @Default(values = "C")
    private String units;

    @SlingObject
    private Resource resource;

    @SlingObject
    private ResourceResolver resourceResolver;

    @OSGiService
    private PageManagerFactory pageManagerFactory;

    @PostConstruct
    protected void init() {
        PageManager pageManager = this.pageManagerFactory.getPageManager(resourceResolver);
        if (pageManager != null) {
            Page currentPage = pageManager.getContainingPage(resource);
            if (currentPage != null) {
                setTempProperty(currentPage);
                city = currentPage.getName().toUpperCase();
            }
        }
    }

    private void setTempProperty(Page currentPage) {
        try {
            Resource contentResource = currentPage.getContentResource();
            if (contentResource != null) {
                ModifiableValueMap properties = contentResource.adaptTo(ModifiableValueMap.class);
                if (properties != null) {
                    properties.put("units", units);
                    resourceResolver.commit();
                } else {
                    log.error("Failed to adapt content resource to ModifiableValueMap");
                }
            }
        } catch (PersistenceException e) {
            log.error("Error while committing changes to the resource", e);
        }
    }
}

