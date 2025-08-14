package com.aem.rnd.lab.core.workflow;


import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.*;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
        service = WorkflowProcess.class,
        property = { "process.label=Send Email Notification to Workflow Participants" }
)
public class SendEmailWorkflowProcess implements WorkflowProcess {

    private static final Logger LOG = LoggerFactory.getLogger(SendEmailWorkflowProcess.class);

    @Reference
    private MessageGatewayService messageGatewayService;

    @Override
    public void execute(WorkItem item, WorkflowSession session, MetaDataMap args) throws WorkflowException {
        try {
            String payloadPath = String.valueOf(item.getWorkflowData().getPayload());
            String emailId = args.get("PROCESS_ARGS", String.class);
            if (StringUtils.isBlank(emailId)) {
                LOG.warn("No participant email configured for Send Email step.");
                return;
            }

            Email email = new HtmlEmail();
            email.setSubject("AEM Workflow Task Assigned");
            email.setMsg("<p>You have a new workflow task for: " + payloadPath + "</p>");
            email.addTo(emailId);

            MessageGateway<Email> gateway = messageGatewayService.getGateway(HtmlEmail.class);
            if (gateway != null) {
                gateway.send(email);
                LOG.info("Email sent to {}", emailId);
            } else {
                LOG.error("MessageGateway is null, cannot send email");
            }
        } catch (EmailException e) {
            LOG.error("Email sending failed", e);
            throw new WorkflowException(e);
        }
    }
}
