
////////////////////////////////
// постфункция - создает подзадачи, если в экране перехода заполнено поле Исполнители
//
//
//
// переход НА РЕЗОЛЮЦИИ -> НА ИСПОЛНЕНИИ
//
//
// все делаем по сценарию боба свифта

//    Create Issue
//    Summary: %original_summary%
//            Description: %parent_description%
// Issue type: Письмо исходящее (10105)
// Priority: Parent's priority
// Reporter: Current user
// Assignee: Specific user (%entry%)
// Affected versions: None
// Fixed versions: None
// Components: Parent issue's components
// Link: %original_key%, Link type: Подготовка ответа, Link direction: From issue key to new issue
// Create multiple issues: %customfield_10301%,


////////////////////////////////


//import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.link.IssueLinkManager
import com.atlassian.jira.security.JiraAuthenticationContext
import com.atlassian.crowd.embedded.api.User
import com.atlassian.jira.user.ApplicationUser

import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.fields.CustomField

import com.atlassian.jira.bc.issue.IssueService
import com.atlassian.jira.issue.IssueInputParameters


CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager()
IssueService issueService = ComponentAccessor.getIssueService()


// id поля исполнители 10301
CustomField ispolnCf = customFieldManager.getCustomFieldObject(10301L)
List<ApplicationUser> ispolnVal = issue.getCustomFieldValue(ispolnCf)

log.warn(" ==================== begin step")

log.warn(ispolnVal.toString())


//Issue issue;

if (ispolnVal == null) {
    log.warn(" ==================== task not create")
    return
}


ApplicationUser curUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
MutableIssue mutableIssue


for (ApplicationUser oneUser : ispolnVal) {

    IssueInputParameters issueInputParameters = issueService.newIssueInputParameters();
    issueInputParameters
            .setProjectId(issue.getProjectId())
            .setSummary(issue.getSummary())
            .setDescription(issue.getDescription())
            .setIssueTypeId("10105")
            .setPriorityId(issue.getPriority().getId())
//            .setReporterId(issue.getReporterId())
            .setReporterId(String.valueOf(curUser.getId()))
            .setAssigneeId(String.valueOf(oneUser.getId()))


    ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

    IssueService.CreateValidationResult createValidationResult = issueService.validateCreate(user, issueInputParameters)

    if (createValidationResult.isValid())
    {
        log.error("entrou no createValidationResult")
        IssueService.IssueResult createResult = issueService.create(user, createValidationResult)
        if (!createResult.isValid())
        {
            log.error("Error while creating the issue.")
        } else {
            mutableIssue = createResult.getIssue();
            log.warn(" ==================== new issue  ")
            log.warn(mutableIssue)

        }
    } else {
        log.warn(" ==================== create result is not valid  ")
    }
}





log.warn(" ==================== end step")






//import com.atlassian.jira.component.ComponentAccessor

//def userManager = ComponentAccessor.userManager
//def user = userManager.getUserByKey("admin")
//// get by username below, above gets user by key (incase user is renamed)
////userManager.getUserByName("admin")
//
//issueLinkManager.createIssueLink(issue.getId(), issueObject.getId(), 10003, 1, user);


//Issue issue  = issue;
//def parentIssue = issue.getParentObject();
//issueManager = ComponentManager.getInstance().getIssueManager()
//subTaskManager = ComponentManager.getInstance().getSubTaskManager();
//subTasks = subTaskManager.getSubTaskObjects(parentIssue)
//IssueLinkManager issueLinkManager = ComponentAccessor.getIssueLinkManager();
//JiraAuthenticationContext authContext = ComponentAccessor.getJiraAuthenticationContext();
//User user = authContext.getLoggedInUser();
//String issueKey = issue.key;
//
//
//ApplicationUser
//
//
//subTasks.each() {
//    String issueTypeId = it.issueTypeObject.id
//    if (issueTypeId == "42") {
//        issueLinkManager.createIssueLink(issue.getId(), it.getId(), Long.parseLong("10003"),Long.valueOf(1), user);
//        issueManager.updateIssue(user, it, EventDispatchOption.ISSUE_UPDATED, false)
//    }
//}
