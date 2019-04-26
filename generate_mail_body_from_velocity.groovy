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
import com.atlassian.jira.mail.util.MailAttachmentsManagerImpl

import com.atlassian.jira.security.JiraAuthenticationContext

import com.atlassian.jira.mail.TemplateIssueFactory


import com.atlassian.jira.avatar.AvatarService
import com.atlassian.jira.avatar.AvatarManager
import com.atlassian.jira.avatar.AvatarTranscoder
import com.atlassian.jira.user.util.UserManager

import com.opensymphony.util.TextUtils

// https://metainf.atlassian.net/wiki/spaces/PLUG/pages/66093146/Velocity+Context+in+Email+Templates


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

def getMessageBodyTemplate(Issue issue, String baseurl) {
  	//String issueKey = issue.get
  	String linkText = "<a href=\"" + baseurl + "/browse/" + issue.getKey() + "\" target=\"_blank\">" + issue.getKey() + "</a>"
  
  
  	String a = 
      """<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
	<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
    	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    	<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0"/>
    	<base href="https://workflowtest.bank-hlynov.ru">
    	<title>Message Title</title>
	</head>
	<body class="jira">

	<h1>ЗАДАЧА ОЖИДАЕТ ИСПОЛНЕНИЯ</h1>

	<div style="margin: 20px 0">В проекте __issueProject__ в очереди ожидания находится задача __issueLink__</div>
	

	<table>
		<tr>
			<td>автор</td>
			<td>__issueAuthor__</td>
		</tr>
		<tr>
			<td>тема</td>
			<td>__issueSummary__</td>
		</tr>
		<tr>
			<td>дата исполнения</td>
			<td>__issueDate__</td>
		</tr>
	</table>


</body>
</html>"""
  
  a = a.replace("__issueLink__", linkText)
  a = a.replace("__issueAuthor__", issue.getReporter().getDisplayName())
  a = a.replace("__issueSummary__", issue.getSummary())
  
  if (issue.getDueDate() == null) {
	  a = a.replace("__issueDate__", "")
  } else {
	  a = a.replace("__issueDate__", issue.getDueDate().format("YYYY-MM-DD HH:mm:ss.Ms"))
  }

  a = a.replace("__issueProject__", issue.getProjectObject().getName())
  
  
  return a
}



ProjectManager projectManager = ComponentAccessor.getProjectManager()
ProjectRoleManager projectRoleManager = ComponentAccessor.getComponentOfType(ProjectRoleManager.class) as ProjectRoleManager
WatcherManager watcherManager = ComponentAccessor.getWatcherManager()
ApplicationProperties applicationPropertiesManager = ComponentAccessor.getApplicationProperties()

//AvatarManager avatarManager = ComponentAccessor.getComponent(AvatarManager.class);
//AvatarService avatarService = ComponentAccessor.getComponent(AvatarService.class);
//AvatarTranscoder avatarTranscoder = ComponentAccessor.getComponent(AvatarTranscoder.class);
//UserManager userManager = ComponentAccessor.getComponent(UserManager.class);
//ApplicationProperties applicationProperties = ComponentAccessor.getComponent(ApplicationProperties.class);


//VelocityManager velocityManager = ComponentAccessor.getVelocityManager()
//VelocityParamFactory velocityParamFactory = ComponentAccessor.getVelocityParamFactory()

//JiraAuthenticationContext jiraAuthenticationContext = ComponentAccessor.getJiraAuthenticationContext()

//I18nHelper i18nHelper = jiraAuthenticationContext.getI18nHelper()

//MailAttachmentsManager mailAttachmentsManager = new  MailAttachmentsManagerImpl(avatarService, avatarTranscoder, userManager, avatarManager, applicationProperties)
//MailAttachmentsManager mailAttachmentsManager = ComponentAccesor.getComponent(MailAttachmentsManager.class)
//TemplateIssueFactory templateIssueFactory = ComponentAccesor.getOSGiComponentInstanceOfType(TemplateIssueFactory.class)


String baseUrl = applicationPropertiesManager.getString(APKeys.JIRA_BASEURL)
String webworkEncoding = applicationPropertiesManager.getString(APKeys.JIRA_WEBWORK_ENCODING)


///////////////////////////////////////////////////////////////////////
// render text test
///////////////////////////////////////////////////////////////////////


//Map<String, Object> context = velocityParamFactory.getDefaultVelocityParams()
//context.put("issue", issue)
//context.put("baseurl", baseUrl)
//context.put("i18n", i18nHelper)
//context.put("textutils",  new TextUtils())

//context.put("attachmentsManager", mailAttachmentsManager)


//renderedText = velocityManager.getEncodedBody("templates/email/html/", "issueassigned.vm", baseUrl, webworkEncoding, context);
// текст письма
renderedText = getMessageBodyTemplate(issue, baseUrl)
//log.info("-----")
//log.info(renderedText)
//log.info("-----")

//sendEmail("aakon@bank-hlynov.ru", "задача ожидает исполнения", renderedText)


//renderedText



///////////////////////////////////////////////////////////////////////

Project proj = projectManager.getProjectObjByName("ЭДО")

// name of role here
ProjectRole devsRole = projectRoleManager.getProjectRole("Делопроизводитель отдела")
ProjectRoleActors actors = projectRoleManager.getProjectRoleActors(devsRole, proj)
  	
// List<ApplicationUser>
List<ApplicationUser> roleUsers = actors.getUsers().toList()

for (ApplicationUser oneUser : roleUsers) {
  //log.warn(oneUser.getName())
  watcherManager.startWatching(oneUser, issue)
  sendEmail(oneUser.getEmailAddress(), "задача ожидает исполнения", renderedText)

}

