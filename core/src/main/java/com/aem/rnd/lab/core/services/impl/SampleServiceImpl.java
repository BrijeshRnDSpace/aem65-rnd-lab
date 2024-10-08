package com.aem.rnd.lab.core.services.impl;

import com.aem.rnd.lab.core.config.SampleServiceConfig;
import com.aem.rnd.lab.core.services.SampleService;
import lombok.extern.slf4j.Slf4j;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.*;

import java.util.Random;

@Slf4j
@Component(service = {SampleService.class})
@Designate(ocd = SampleServiceConfig.class)
public class SampleServiceImpl implements SampleService {
    private SampleServiceConfig configuration;
    private static final String[] ACTIVITIES = new String[]{"Camping", "Skiing", "Skateboarding"};
    private final Random random = new Random();

    public String getRandomActivity() {
        int randomIndex = random.nextInt(ACTIVITIES.length);
        return ACTIVITIES[randomIndex] + " \n Blog name: " + configuration.blog_name() + " \n Blog URL: " + configuration.blog_URL() + " \n Blog Count111: " + configuration.blogCount() + " \n Blog Password1111: " + configuration.password() + " \n Blog Is Active? " + configuration.blogIsActive() + "\n Blog is hosted at? " + configuration.hostedAt();
    }

    @Activate
    protected void activateedthemethod(final SampleServiceConfig configuration) {
        log.info("===========================Activate SampleServiceConfig  again = " + configuration.hostedAt());
        log.info("===========================Blog name: " + configuration.blog_name());
        log.info("===========================Blog URL: " + configuration.blog_URL());
        for (String topic : configuration.blog_Topics()) {
            log.info("===========================Blog Topics: " + topic);
        }
        log.info("===========================Blog Count111: " + configuration.blogCount());
        log.info("===========================Blog Password1111: " + configuration.password());
        log.info("===========================Blog Is Active? " + configuration.blogIsActive());
        log.info("===========================Blog is hosted at? " + configuration.hostedAt());
        this.configuration = configuration;
    }

    @Deactivate
    protected void deactivateedalsodsf() {
        log.info("deactivateedalsodsf Deregistering: SampleServiceImpl");
    }
}