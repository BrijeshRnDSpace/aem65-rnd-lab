package com.aem.rnd.lab.core.models;

import com.aem.rnd.lab.core.config.TerritoryContextConfig;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.PageManagerFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.caconfig.ConfigurationBuilder;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import javax.annotation.PostConstruct;

@Slf4j
@Getter
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TerritoryModel {

    private String siteCity;

    private String siteLocale;

    private String siteAdmin;

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
            TerritoryContextConfig territoryContextConfig = getContentAwareConfiguration(pageManager.getContainingPage(resource), resourceResolver);
            siteAdmin = territoryContextConfig.siteAdmin();
            siteLocale = territoryContextConfig.siteLocale();
            siteCity = territoryContextConfig.siteCity();
        } else {
            throw new IllegalStateException("PageManager could not be adapted from ResourceResolver.");
        }

    }

    public TerritoryContextConfig getContentAwareConfiguration(Page currentPage, ResourceResolver resourceResolver) {

        if (currentPage != null) {
            Resource contentResource = resourceResolver.getResource(currentPage.getPath());
            if (contentResource != null) {
                ConfigurationBuilder configurationBuilder = contentResource.adaptTo(ConfigurationBuilder.class);
                if (configurationBuilder != null) {
                    return configurationBuilder.as(TerritoryContextConfig.class);
                }
            }
        }
        return null;
    }
}

