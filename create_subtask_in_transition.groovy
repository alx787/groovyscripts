import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.IssueFactory
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.issue.attachment.FileAttachments
import org.slf4j.LoggerFactory

import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.event.type.*
//import com.onresolve.scriptrunner.runner.util.UserMessageUtil

def log = LoggerFactory.getLogger(this.getClass()) 

IssueFactory issueFactory = ComponentAccessor.getIssueFactory()
IssueManager issueManager = ComponentAccessor.getIssueManager()

CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager()
def subTaskManager = ComponentAccessor.getSubTaskManager()
def attachmentManager = ComponentAccessor.getAttachmentManager()
def pathManager = ComponentAccessor.getAttachmentPathManager()

// исполнители
//def ispCstFld = customFieldManager.getCustomFieldObject(10500L)
def ispCstFld = customFieldManager.getCustomFieldObject(10301L)

//originalIssue
//if (issue.getIssueTypeObject().getName() != "Поручение") {
//	return
//}

// текущий пользователь
ApplicationUser curUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

List<ApplicationUser> ispolniteli = (List<ApplicationUser>)issue.getCustomFieldValue(ispCstFld)
//def ispolniteli = (List<ApplicationUser>)issue.getCustomFieldValue(ispCstFld)

if (ispolniteli == null) {
	return
}

if (ispolniteli.size() == 0) {
	return
}

//if (issue.assignee == null) {
//	issue.setAssignee(ispolniteli.get(0))
//  	ispolniteli.remove(0)
//}

//if (issue.assignee != null) {
//  if (ispolniteli.contains(issue.assignee)) {
//  	ispolniteli.remove(issue.assignee)
//  }
//}

//String st = ""

//log.warn(" ================= before ")


// копируем поле из родительской задачи в подзадачи
// срок исполнения - кастомное поле, это системное
//def customFieldDate = customFieldManager.getCustomFieldObject(10019L); 
// получаем значение
//Object customFieldDateValue = issue.getCustomFieldValue(customFieldDate); 
//def customFieldDateValue = issue.getCustomFieldValue(customFieldDate); 


ispolniteli.each { oneUser ->
  

  //log.warn(" ================= step ")
  
  
  MutableIssue newSubTask = issueFactory.getIssue()
  // поручение
  newSubTask.setIssueTypeId("10109")
  
  
  
  newSubTask.setAssignee(oneUser)
  
  newSubTask.setComponent(issue.getComponents())
  newSubTask.setReporter(issue.getAssignee())
  newSubTask.setDescription(issue.getDescription())
  
  newSubTask.setSummary(issue.getSummary())
  newSubTask.setParentObject(issue)
  newSubTask.setProjectObject(issue.getProjectObject())
  newSubTask.setPriority(issue.getPriority())

  newSubTask.setDueDate(issue.getDueDate())

  Map<String,Object> newIssueParams = ["issue" : newSubTask] as Map<String,Object>
  Issue newIssue = issueManager.createIssueObject(curUser, newIssueParams)
  subTaskManager.createSubTaskIssueLink(issue, newSubTask, curUser)

  
  // присвоим значение кастомного поля
//  newIssue.setCustomFieldValue(customFieldDate, customFieldDateValue);
//  issueManager.updateIssue(curUser, newIssue, EventDispatchOption.ISSUE_UPDATED, false)  
  
  
  attachmentManager.getAttachments(issue).each {attachment ->
	// копируем вложения из родительской задачи в подзадачу    
	//log.debug("key : "+ attachment.getId())
    
	File rootDir = new File(pathManager.attachmentPath)
	File filePath = FileAttachments.getAttachmentDirectoryForIssue(rootDir, issue.projectObject.getKey(), issue.getKey())
	String filePaths = filePath.toString()
	filePaths = filePaths + "//" + attachment.getId()
	File atFile = new File(filePaths)  

	//log.debug("file path : " + filePaths)
    
    if (atFile.exists()) {
		attachmentManager.createAttachmentCopySourceFile(atFile, attachment.filename, attachment.mimetype, attachment.author, newIssue, [:], attachment.created)
    }
  }
  
  
  
  //  log.warn(oneUser.getDisplayName())
  //UserMessageUtil.warning("создана подзадача " + newIssue.getKey() + " исполнитель " + oneUser.getDisplayName())
  //  UserMessageUtil.warning("создана подзадача исполнитель " + oneUser.getDisplayName())
  
  
}
