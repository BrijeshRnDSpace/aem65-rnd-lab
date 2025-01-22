package com.aem.rnd.lab.core.jobs;

import lombok.extern.slf4j.Slf4j;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A JobConsumer Service implementation to demonstrate how to
 * create a JobConsumer to help schedule Jobs.
 * A JOb is a special event that will be processed exactly once. When a job is scheduled , it will be
 * available in /var/eventing/jobs . Even when the system fails, the jobs int he queue will retry to execute
 * till it fails
 */
@Slf4j
@Component(service = JobConsumer.class,
        immediate = true,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Demo to listen on changes in the resource tree",
                JobConsumer.PROPERTY_TOPICS + "=sample/replication/job"  //topic names like sample.replication.job will NOT WORK
        })
public class SampleSlingJobConsumer implements JobConsumer {

    @Override
    public JobResult process(Job job) {
        try {
            log.debug("Processing the JOB *******");

            //A Property map will be passed on so we can fetch the values we need here to//Process the request

            String path = (String) job.getProperty("path");
            log.debug("The path in which the replication is triggered and passed to the Job is " +
                    "{}", path);

            //TODO : Write your business logic here . Any properties you need to execute the job can be passed//TODO: on via the  Map which is treated as the properties for the JOB.


            /**
             * Return the proper JobResult based on the work done...
             *
             * > OK : Processed successfully
             * > FAILED: Processed unsuccessfully and reschedule --> This will keep the JOB up for next retry
             * > CANCEL: Processed unsuccessfully and do NOT reschedule
             * > ASYNC: Process through the JobConsumer.AsyncHandler interface
             */return JobConsumer.JobResult.OK;
        } catch (Exception e) {
            log.error("Exception is ", e);
            return JobResult.FAILED;
        }
    }
}