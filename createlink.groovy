import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.link.IssueLinkManager
import com.atlassian.jira.security.JiraAuthenticationContext
import com.atlassian.crowd.embedded.api.User

Issue issue  = issue;
def parentIssue = issue.getParentObject();
issueManager = ComponentManager.getInstance().getIssueManager()
subTaskManager = ComponentManager.getInstance().getSubTaskManager();
subTasks = subTaskManager.getSubTaskObjects(parentIssue)
IssueLinkManager issueLinkManager = ComponentAccessor.getIssueLinkManager();
JiraAuthenticationContext authContext = ComponentAccessor.getJiraAuthenticationContext();
User user = authContext.getLoggedInUser();
String issueKey = issue.key;
 
subTasks.each() {
    String issueTypeId = it.issueTypeObject.id
    if (issueTypeId == "42") {
        issueLinkManager.createIssueLink(issue.getId(), it.getId(), Long.parseLong("10003"),Long.valueOf(1), user);
        issueManager.updateIssue(user, it, EventDispatchOption.ISSUE_UPDATED, false)
    }
}
