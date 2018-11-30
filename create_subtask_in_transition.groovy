import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.IssueFactory
import com.atlassian.jira.issue.IssueManager
import org.slf4j.LoggerFactory

import com.onresolve.scriptrunner.runner.util.UserMessageUtil

def log = LoggerFactory.getLogger(this.getClass()) 

IssueFactory issueFactory = ComponentAccessor.getIssueFactory()
IssueManager issueManager = ComponentAccessor.getIssueManager()

def customFieldManager = ComponentAccessor.getCustomFieldManager()
def subTaskManager = ComponentAccessor.getSubTaskManager()
def ispCstFld = customFieldManager.getCustomFieldObject(10500L)

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


ispolniteli.each { oneUser ->
  
  
  MutableIssue newSubTask = issueFactory.getIssue()
  // поручение - тип задачи
  newSubTask.setIssueTypeId("10109")
  
  newSubTask.setAssignee(oneUser)
  
  newSubTask.setComponent(issue.getComponents())
  newSubTask.setReporter(issue.getAssignee())
  newSubTask.setDescription(issue.getDescription())
  
  newSubTask.setSummary(issue.getSummary())
  newSubTask.setParentObject(issue)
  newSubTask.setProjectObject(issue.getProjectObject())
  newSubTask.setPriority(issue.getPriority())
  
  Map<String,Object> newIssueParams = ["issue" : newSubTask] as Map<String,Object>
  Issue newIssue = issueManager.createIssueObject(curUser, newIssueParams)
  subTaskManager.createSubTaskIssueLink(issue, newSubTask, curUser)

  
//  log.warn("создана подзадача " + newIssue.getKey() + " исполнитель " + oneUser.getDisplayName())

  UserMessageUtil.warning("создана подзадача " + newIssue.getKey() + " исполнитель " + oneUser.getDisplayName())

  
}
