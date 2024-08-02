package com.aem.rnd.lab.core.config;


import org.apache.sling.caconfig.annotation.Configuration;
import org.apache.sling.caconfig.annotation.Property;

@Configuration(label = "RND Lab Territory Context Aware Config", description = "CA configurations for Territory")
public @interface TerritoryContextConfig {
    @Property(label = "City Site", description = "City Site name")
    String siteCity() default "Delhi";
    @Property(label = "Locale Site", description = "Locale Site name")
    String siteLocale() default  "EN";
    @Property(label = "City Site Admin ", description = "Admin Site name")
    String siteAdmin() default  "Admin";
}