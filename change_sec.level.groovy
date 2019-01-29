import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.IssueFactory
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.issue.attachment.FileAttachments
import org.slf4j.LoggerFactory

import com.atlassian.jira.event.type.EventDispatchOption;


IssueManager issueManager = ComponentAccessor.getIssueManager()
ApplicationUser curUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

//MutableIssue mutIssue = issue

issue.setSecurityLevelId(10001)

//issueManager.updateIssue(curUser, issue, EventDispatchOption.ISSUE_UPDATED, false);
issue.store()
