import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.IssueInputParameters
import com.atlassian.jira.workflow.TransitionOptions
import com.atlassian.jira.bc.issue.IssueService
import com.atlassian.jira.user.ApplicationUser

IssueService issueService = ComponentAccessor.getIssueService()
IssueInputParameters inputParameters = issueService.newIssueInputParameters();


// получим родительскую задачу
Issue parentIssue = issue.getParentObject()

// пользователь для подстановки в переход
ApplicationUser userTr = parentIssue.getAssignee()
if (userTr == null) {
  userTr = parentIssue.getReporter()
}
if (userTr == null) {
  userTr = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
}

//log.warn(" ==== issue " + issue.toString())
//log.warn(" ==== parent issue " + parentIssue.toString())
//log.warn(" ==== parent issue id " + parentIssue.getId().toString())

if (parentIssue == null) {
  return true
}

//if (parentIssue == null) {
//  return true
//}

//log.warn(" ==== parentIssue.getStatusId() " + parentIssue.getStatusId())
//log.warn(" ==== parentIssue.getIssueTypeId() " + parentIssue.getIssueTypeId())

//log.warn(" ==== issue.getStatusId() " + issue.getStatusId())
//log.warn(" ==== issue.getIssueTypeId() " + issue.getIssueTypeId())

//def transitionValidationResult 

// проверка статуса - "Отправлено на регистрацию" 10119 и тип задачи "Письмо входящее" 10101
// проверка статуса - "Отправлено на регистрацию" 10107 b тип задачи "Письмо входящее" 10101
//if ((parentIssue.getStatusId() == "10119") && (parentIssue.getIssueTypeId() == "10101")) {
if ((parentIssue.getStatusId() == "10107") && (parentIssue.getIssueTypeId() == "10101")) {
  
  // количество завершенных подзадач
  Integer cntResolved = 0
  
  // получим все субтаски
  Collection<Issue> subtasks = parentIssue.getSubTaskObjects();
  
  //log.warn(" ==== subtasks cnt " + subtasks.size().toString())
  
  for (int i = 0; i < subtasks.size(); i++) {
    //log.warn(" ==== subtask status " + subtasks[i].getStatusId())
    // только в статусе "исполнено"
    if (subtasks[i].getStatusId() == "10110") {
      //log.warn(" ==== subtask ready " + subtasks[i].toString())
      cntResolved = cntResolved + 1;
    }
  }

  //log.warn(" ==== cntResolved " + cntResolved.toString())
  
  
  // если все подзадачи закрыты то попробуем закрыть родительскую задачу
  if ((cntResolved != 0) && (cntResolved == subtasks.size())) {
    //log.warn(" ==== userTr " + userTr.toString())
    // проверка перехода (301 - код перехода "Исполнено подзадачами")
    IssueService.TransitionValidationResult transitionValidationResult = issueService.validateTransition(userTr, parentIssue.getId(), 301, inputParameters)
    //log.warn(" ==== transition validation result " + transitionValidationResult.isValid().toString())
    if (transitionValidationResult.isValid()) {
      //log.warn(" ==== transition ready is valid ")
      IssueService.IssueResult issueResult = issueService.transition(userTr, transitionValidationResult)
    } else {
      //log.warn(" ==== transition ready is not valid ")
      log.warn(transitionValidationResult.errorCollection.toString())
    }
  }
  
}

