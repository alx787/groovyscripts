////////////////////////////////
// листенер, реагирующий на смену исполнителя в подзадаче
// добавляет предыдущего исполнителя в поле ФИО наблюдателей
//
// Custom listener
// Projects: All projects
// Events: Issue Assigned
// issue reassigned
//
////////////////////////////////

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.event.issue.AbstractIssueEventListener
import com.atlassian.jira.event.issue.IssueEvent
import com.atlassian.jira.event.type.EventType
import com.atlassian.jira.event.type.EventTypeManager
import com.atlassian.jira.issue.Issue

import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.changehistory.ChangeHistoryManager
import com.atlassian.jira.issue.history.ChangeItemBean
import com.atlassian.jira.user.util.UserManager;

import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.CustomFieldManager

import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.IssueManager

import org.slf4j.Logger
import org.slf4j.LoggerFactory


def copyPrevAssigneToFIONabludateley(Issue issue) {

    IssueManager issueManager = ComponentAccessor.getIssueManager()
    UserManager userManager = ComponentAccessor.getUserManager();

    //////////////////////////////////////
    // текущий пользователь
    ApplicationUser curUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

    //////////////////////////////////////
    // назначенный исполнитель
    ApplicationUser newAssignee = issue.getAssignee()
    String prevAssigneeName = ""

    //    log.warn(" ============== it is event ============== " + newAssignee.getUsername())

    //////////////////////////////////////
    // ищем имя предыдущего исполнителя через историю
    ChangeHistoryManager changeHistoryManager = ComponentAccessor.getChangeHistoryManager()
    List<ChangeItemBean> assigneeChHistory = changeHistoryManager.getChangeItemsForField(issue, "assignee");
    //List<ChangeItemBean> assigneeChHistory = changeHistoryManager.getChangeItemsForField(mutableIssue, "assignee");

    if (!assigneeChHistory.isEmpty()) {
        ChangeItemBean assigneeChItemBean = assigneeChHistory.get(assigneeChHistory.size() - 1);
        prevAssigneeName = assigneeChItemBean.getFrom();
    }


//    log.warn(" ============== it is event from ============== " + prevAssigneeName)

    //////////////////////////////////////
    // если имя исполнителя не пустое то ищем его по спискам пользователей
    ApplicationUser prevAssignee = null

    if (prevAssigneeName != null) {
        prevAssignee = userManager.getUserByName(prevAssigneeName);
    }

    //////////////////////////////////////
    // если исполнитель найден то добавим его в поле ФИО наблюдателей
    if ((prevAssignee != null) && (prevAssignee != newAssignee)) {
        CustomFieldManager cFManager = ComponentAccessor.getCustomFieldManager()
        CustomField fioNablCf = cFManager.getCustomFieldObject(10400L)

        List<ApplicationUser> fioNablVal = (List<ApplicationUser>)issue.getCustomFieldValue(fioNablCf)
        if (fioNablVal == null) {
            fioNablVal = new ArrayList<ApplicationUser>()
        }
        // проверим что он уже не присутствует в списке
        if (!fioNablVal.contains(prevAssignee)) {
            fioNablVal.add(prevAssignee)
            issue.setCustomFieldValue(fioNablCf, fioNablVal)

            issueManager.updateIssue(curUser, issue, EventDispatchOption.DO_NOT_DISPATCH, false);
        }
        
        // всех кто есть в списке добавляем в наблюдатели
        for (ApplicationUser oneUser : fioNablVal) {
            watcherManager.startWatching(oneUser, issue)
        }
        
        
    }

}


/////////////////////////////////
// для поручения 10109
//
//log.warn(" ============== event catched ============== ")

if (issue.getIssueTypeId().equals("10109")) {
//    log.warn(" ============== do for subtask ============== ")
    copyPrevAssigneToFIONabludateley(issue)
}
