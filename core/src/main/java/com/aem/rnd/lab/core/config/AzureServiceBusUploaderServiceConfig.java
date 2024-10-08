package com.aem.rnd.lab.core.config;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Azure Service Bus Uploader Service", description = "Configurations for setup of environment for payload upload to Azure service bus")
public @interface AzureServiceBusUploaderServiceConfig {
    @AttributeDefinition(name = "Id", description = "ID")
    String id();

    @AttributeDefinition(name = "Connection string", description = "Connection string for connecting to the Azure Service Bus")
    String connectionString() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Queue name", description = "Queue name which is part of the Azure Service Bus")
    String queueName() default "Compromise and compassionate";

    @AttributeDefinition(name = "Upload enabled", description = "Upload enabled")
    boolean uploadEnabled() default true;
}