import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.IssueInputParameters
import com.atlassian.jira.workflow.TransitionOptions
import com.atlassian.jira.bc.issue.IssueService
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.fields.CustomField


IssueService issueService = ComponentAccessor.getIssueService()
IssueInputParameters inputParameters = issueService.newIssueInputParameters();
CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager()

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

if (parentIssue == null) {
  return true
}

// типы задач для входящего документа
//
// Вх документы ЦБ       10400
// Запрос нотариуса      10302
// Заявление             10100
// Исполнительный лист   10104
// Письмо входящее       10101
// Повестка              10103
// Постановление ССП     10300
// Судебная документация 10301
// Телеграмма            10102

List<String> issueTypes = new ArrayList<String>();

issueTypes.add("10400");
issueTypes.add("10302");
issueTypes.add("10100");
issueTypes.add("10104");
issueTypes.add("10101");
issueTypes.add("10103");
issueTypes.add("10300");
issueTypes.add("10301");
issueTypes.add("10102");


// получим имя БП, чтобы ориентироваться на него а не на IssueTypeId
//String parentWorkflowmName = ComponentAccessor.getWorkflowManager().getWorkflow(parentIssue).getName()

// статус "На исполнении" 10107

// id поля Резолюция 10036
CustomField resolCf = customFieldManager.getCustomFieldObject(10036L)
String resolVal = parentIssue.getCustomFieldValue(resolCf)


if ((parentIssue.getStatusId() == "10107") && (issueTypes.contains(parentIssue.getIssueTypeId())) && (resolVal.equals("Для сведения"))) {

  // количество завершенных подзадач
  Integer cntResolved = 0
  
  // получим все субтаски
  Collection<Issue> subtasks = parentIssue.getSubTaskObjects();
  
  for (int i = 0; i < subtasks.size(); i++) {
    //log.warn(" ==== subtask status " + subtasks[i].getStatusId())
    // только в статусе "исполнено"
    if (subtasks[i].getStatusId() == "10110") {
      //log.warn(" ==== subtask ready " + subtasks[i].toString())
      cntResolved = cntResolved + 1;
    }
  }

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

