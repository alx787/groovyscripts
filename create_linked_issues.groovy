
////////////////////////////////
// постфункция - создает подзадачи, если в экране перехода заполнено поле Исполнители
//
//
//
// переход НА ИСПОЛНЕНИИ -> ПОДГОТОВКА ОТВЕТА
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
import com.atlassian.jira.bc.issue.IssueService.IssueResult
import com.atlassian.jira.bc.issue.IssueService.CreateValidationResult
import com.atlassian.jira.issue.IssueInputParameters

import com.atlassian.jira.issue.fields.config.FieldConfig
import com.atlassian.jira.issue.customfields.option.Options
import com.atlassian.jira.issue.customfields.option.Option

//import com.atlassian.jira.issue.customfields.view.CustomFieldParams
//import com.atlassian.jira.issue.customfields.impl.CalculatedCFType

def getValueFromStringField(long fieldId) {
    CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager()
    CustomField managerCf = customFieldManager.getCustomFieldObject(fieldId)
    String valueCf = (String)issue.getCustomFieldValue(managerCf)

    if ((valueCf == null) || (valueCf.isEmpty())) {
        valueCf = "_";
    }

    return valueCf
}










CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager()
IssueService issueService = ComponentAccessor.getIssueService()



//log.warn("field config")
//log.warn(sposobOtprPossibleVal.toString())
//
//
//for (Option oneOpt : sposobOtprPossibleVal) {
//    log.warn("id " + String.valueOf(oneOpt.optionId) + " value " + oneOpt.value)
//}





// id поля исполнители 10301
CustomField ispolnCf = customFieldManager.getCustomFieldObject(10301L)
List<ApplicationUser> ispolnVal = (List<ApplicationUser>)issue.getCustomFieldValue(ispolnCf)

log.warn(" ==================== begin step")

log.warn(ispolnVal.toString())


//Issue issue

if (ispolnVal == null) {
    log.warn(" ==================== task not create")
    return
}

// текущий пользователь
ApplicationUser curUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
MutableIssue mutableIssue


for (ApplicationUser oneUser : ispolnVal) {

    IssueInputParameters issueInputParameters = issueService.newIssueInputParameters()

    issueInputParameters.setSkipScreenCheck(true)
//    issueInputParameters.setSkipLicenceCheck(true)

    issueInputParameters.setProjectId(issue.getProjectId())
    issueInputParameters.setIssueTypeId("10105")
    issueInputParameters.setStatusId("1")

    issueInputParameters.setSummary(issue.getSummary())

//    issueInputParameters.setDescription(issue.getDescription())
    issueInputParameters.setDescription("121212")

    issueInputParameters.setPriorityId(issue.getPriority().getId())
    issueInputParameters.setReporterId(issue.getReporterId())
    issueInputParameters.setAssigneeId(issue.getAssigneeId())


    //////////////////////////////////////////////////
    // Адрес организации
    issueInputParameters.addCustomFieldValue(10060L, getValueFromStringField(10060L))


    //////////////////////////////////////////////////
    // Должность подписанта

//    def valPart_0 = issue.getCustomFieldValue(dolgnField)[0].toString()
//    def valPart_1 = issue.getCustomFieldValue(dolgnField)[1].toString()
//    def valPart_2 = issue.getCustomFieldValue(dolgnField)[2].toString()

    CustomField dolgnField = customFieldManager.getCustomFieldObject(10600L)
    FieldConfig dolgnConfig = dolgnField.getRelevantConfig(issue)
    List<Option> dolgnFieldOptions = ComponentAccessor.getOptionsManager().getOptions(dolgnConfig)


    // перебор всех опций
//    for (Option oneOpt : dolgnFieldOptions) {
//        log.warn(" = oneOpt: " + oneOpt)
//
//        List<Option> dolgnFieldOptions_2 = oneOpt.getChildOptions()
//        for (Option secondOpt : dolgnFieldOptions_2) {
//            log.warn(" == secOpt: " + secondOpt)
//        }
//
//    }

//    issueInputParameters.addCustomFieldValue(10600L, dolgnFieldOptions.getOptionById(11500L).optionId.toString())
    issueInputParameters.addCustomFieldValue(dolgnField.getId() + ":0", dolgnFieldOptions.getOptionById(11500L).optionId.toString())


    //////////////////////////////////////////////////
    // ФИО руководителя
    issueInputParameters.addCustomFieldValue(10071L, getValueFromStringField(10071L))

    //////////////////////////////////////////////////
    // Организация
    issueInputParameters.addCustomFieldValue(10061L, getValueFromStringField(10061L))

    //////////////////////////////////////////////////
    // должность руководителя
    issueInputParameters.addCustomFieldValue(10072L, getValueFromStringField(10072L))

    //////////////////////////////////////////////////
    // подписант
    CustomField podpisantCf = customFieldManager.getCustomFieldObject(10031L)
    ApplicationUser podpisantVal = (ApplicationUser)issue.getCustomFieldValue(podpisantCf)

    if (podpisantVal == null) {
        podpisantVal = curUser
    }

    issueInputParameters.addCustomFieldValue(10031L, podpisantVal.getName())

    //////////////////////////////////////////////////
    // способ отправки
    // 10020 способ отправки
    CustomField sposobOtprCf = customFieldManager.getCustomFieldObject(10020L)
    FieldConfig sposobOtprConfig = sposobOtprCf.getRelevantConfig(issue)
    // все возможные способы отправки
    Options sposobOtprPossibleVal = ComponentAccessor.getOptionsManager().getOptions(sposobOtprConfig)

    issueInputParameters.addCustomFieldValue(10020L, sposobOtprPossibleVal.getOptionById(10004L).optionId.toString());

    //////////////////////////////////////////////////
    // делопроизводитель
    issueInputParameters.addCustomFieldValue(10032L, curUser.getName())

    //////////////////////////////////////////////////
    // телефон автора
    issueInputParameters.addCustomFieldValue(10033L, getValueFromStringField(10033L))


    //////////////////////////////////////////////////
    // индекс
    issueInputParameters.addCustomFieldValue(10069L, getValueFromStringField(10069L))


    //////////////////////////////////////////////////
    // Подпись - подпись логином паролем, хз зачем

    CustomField signCf = customFieldManager.getCustomFieldObject(10037L)
//    Object signVal = issue.getCustomFieldValue(signCf)
//
//    log.warn(" sign field ")
//    if (signVal == null) {
//        log.warn("null")
//    } else {
//        log.warn(signVal)
//    }


//    issueInputParameters.addCustomFieldValue(10037L, null)
//    issueInputParameters.addCustomFieldValue("10037", curUser.getName(), "passw")


//    String[] myValues = ["username","userpass", "opts"]

    log.warn("opts")
//    log.warn(myValues)
//    issueInputParameters.addCustomFieldValue(signCf.getId(), "useralex")
//    issueInputParameters.addCustomFieldValue(signCf.getId() + ":1", "password")
    issueInputParameters.addCustomFieldValue(signCf.getId(), curUser.getName())
    issueInputParameters.addCustomFieldValue(signCf.getId() + ":1", "pizdetskakoyto")

    

//            .setReporterId(String.valueOf(curUser.getId()))
//            .setAssigneeId(String.valueOf(oneUser.getId()))


//    ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()




    CreateValidationResult createValidationResult = issueService.validateCreate(curUser, issueInputParameters)

    if (createValidationResult.isValid())
    {
        log.error("entrou no createValidationResult")
        IssueResult createResult = issueService.create(curUser, createValidationResult)
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

        Map<String, String> errorCollection = createValidationResult.getErrorCollection().getErrors()
        log.warn("ERROR: Validation errors:")
        for (String errorKey : errorCollection.keySet()) {
            log.warn(errorKey);
            log.warn(errorCollection.get(errorKey));
        }

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
