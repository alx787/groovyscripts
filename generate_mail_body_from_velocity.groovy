import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.project.Project
import com.atlassian.jira.project.ProjectManager
import com.atlassian.jira.security.roles.ProjectRole
import com.atlassian.jira.security.roles.ProjectRoleActors
import com.atlassian.jira.security.roles.ProjectRoleManager
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.watchers.WatcherManager
import com.atlassian.jira.config.properties.ApplicationProperties
import com.atlassian.jira.config.properties.APKeys
import com.atlassian.velocity.VelocityManager
import com.atlassian.jira.util.VelocityParamFactory

import com.atlassian.mail.Email
import com.atlassian.mail.server.SMTPMailServer

import com.atlassian.jira.util.I18nHelper
import com.atlassian.jira.mail.util.MailAttachmentsManager

import com.atlassian.jira.security.JiraAuthenticationContext

import com.atlassian.jira.mail.TemplateIssueFactory

// процедура отсылки сообщения 
// Create an email
def sendEmail(String emailAddr, String subject, String body) {
    SMTPMailServer mailServer = ComponentAccessor.getMailServerManager().getDefaultSMTPMailServer()
    if (mailServer) {
        Email email = new Email(emailAddr)
      	email.setMimeType("text/html");
        email.setSubject(subject)
        email.setBody(body)
        mailServer.send(email)
        log.debug("Mail sent")
    } else {
        log.warn("Please make sure that a valid mailServer is configured")
    }
}


ProjectManager projectManager = ComponentAccessor.getProjectManager()
ProjectRoleManager projectRoleManager = ComponentAccessor.getComponentOfType(ProjectRoleManager.class) as ProjectRoleManager
WatcherManager watcherManager = ComponentAccessor.getWatcherManager()
ApplicationProperties applicationPropertiesManager = ComponentAccessor.getApplicationProperties()

VelocityManager velocityManager = ComponentAccessor.getVelocityManager()
VelocityParamFactory velocityParamFactory = ComponentAccessor.getVelocityParamFactory()

JiraAuthenticationContext jiraAuthenticationContext = ComponentAccessor.getJiraAuthenticationContext()

I18nHelper i18nHelper = jiraAuthenticationContext.getI18nHelper()

//MailAttachmentsManager mailAttachmentsManager = ComponentAccesor.getComponent(MailAttachmentsManager.class)
//TemplateIssueFactory templateIssueFactory = ComponentAccesor.getOSGiComponentInstanceOfType(TemplateIssueFactory.class)


String baseUrl = applicationPropertiesManager.getString(APKeys.JIRA_BASEURL)
String webworkEncoding = applicationPropertiesManager.getString(APKeys.JIRA_WEBWORK_ENCODING)


///////////////////////////////////////////////////////////////////////
// render text test
///////////////////////////////////////////////////////////////////////


Map<String, Object> context = velocityParamFactory.getDefaultVelocityParams()
context.put("issue", issue)
context.put("baseurl", baseUrl)
context.put("i18n", i18nHelper)
//context.put("issueType", i18nHelper)

//context.put("attachmentsManager", mailAttachmentsManager)


//renderedText = vm.getEncodedBody("C:/atlassian-jira/WEB-INF/classes/templates/email/html/", "issuecreated.vm", baseUrl, webworkEncoding, context);
renderedText = velocityManager.getEncodedBody("templates/email/html/", "issueassigned.vm", baseUrl, webworkEncoding, context);
log.info("-----")
log.info(renderedText)
log.info("-----")

sendEmail("email.email.ru", "задача ожидает исполнения", renderedText)

///////////////////////////////////////////////////////////////////////





Project proj = projectManager.getProjectObjByName("ЭДО")

// name of role here
ProjectRole devsRole = projectRoleManager.getProjectRole("Делопроизводитель ОДОУ")
ProjectRoleActors actors = projectRoleManager.getProjectRoleActors(devsRole, proj)
  	
// List<ApplicationUser>
List<ApplicationUser> roleUsers = actors.getUsers().toList()

for (ApplicationUser oneUser : roleUsers) {
  log.warn(oneUser.getName())
  
  watcherManager.startWatching(oneUser, issue)
}
