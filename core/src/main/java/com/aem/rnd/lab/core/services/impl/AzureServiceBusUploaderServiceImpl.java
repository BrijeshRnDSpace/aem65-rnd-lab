package com.aem.rnd.lab.core.services.impl;

import com.aem.rnd.lab.core.config.AzureServiceBusUploaderServiceConfig;
import com.aem.rnd.lab.core.services.AzureServiceBusUploaderService;
import lombok.extern.slf4j.Slf4j;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;

@Slf4j
@Component(service = AzureServiceBusUploaderService.class, immediate = true)
@Designate(ocd = AzureServiceBusUploaderServiceConfig.class)
public class AzureServiceBusUploaderServiceImpl implements AzureServiceBusUploaderService {
    private AzureServiceBusUploaderServiceConfig config;
    String id;
    String connectionString;
    String queueName;
    boolean uploadEnabled;

    @Activate
    protected void activate(final AzureServiceBusUploaderServiceConfig config) {
        this.config = config;
        this.id = config.id();
        this.connectionString = config.connectionString();
        this.queueName = config.queueName();

        log.info("Azure Service Bus Uploader Service is activated."/* + config.connectionString()*/);
    }

    @Modified
    protected void modify(final AzureServiceBusUploaderServiceConfig config) {
        this.config = config;
        log.info("Azure Service Bus Uploader Service is modified.");
    }

    @Deactivate
    protected void deactivate() {
        log.info("Azure Service Bus Uploader Service is deactivated.");
    }

    @Override
    public String processPayloadUpload(final String haalFile) {
        return "\n File  uploaded to shares  " + haalFile + "\n ID config" + id + "\n connectionString" + connectionString + "\nqueueName " + queueName;
    }
}