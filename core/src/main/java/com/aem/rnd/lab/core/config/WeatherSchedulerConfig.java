package com.aem.aem.lab.core.config;


import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "RND Lab Weather Scheduler Configuration")
public @interface WeatherSchedulerConfig {
    @AttributeDefinition(
            name = "Expression",
            description = "Cron-job expression. Default: run every day at 23:00. HELP: https://www.freeformatter.com/cron-expression-generator-quartz.html",
            type = AttributeType.STRING
    )
    String scheduler_expressions() default "0 0 */12 ? * *";

    @AttributeDefinition(
            name = "Weather API URL",
            description = "Target url for weather api",
            type = AttributeType.STRING
    )
    String apiURL()
    default "https://api.openweathermap.org/data/2.5/weather";

    @AttributeDefinition(
            name = "Weather App Id",
            description = "App Id for integration",
            type = AttributeType.STRING
    )
    String appId()
    default "b28bda5814ae928aef2c0a770cdccc77";
}