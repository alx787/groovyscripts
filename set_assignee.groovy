import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.util.UserManager
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.bc.issue.IssueService

//log.warn(issue.getIssueType().getId())

if (issue.getIssueType().getId() == "10300") {
	MutableIssue mutIssue = issue
	UserManager userMan = ComponentAccessor.getUserManager()

	ApplicationUser hramova = userMan.getUserByName("oakarp")
	ApplicationUser currentUser = ComponentAccessor.jiraAuthenticationContext.getLoggedInUser()

	mutIssue.setAssignee(hramova) 

	IssueManager issueManager = ComponentAccessor.getIssueManager()
	issueManager.updateIssue(currentUser, mutIssue, EventDispatchOption.ISSUE_ASSIGNED, true)  
}
