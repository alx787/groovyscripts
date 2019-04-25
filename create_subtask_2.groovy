////////////////////////////////
// постфункция - создает подзадачи, если в экране перехода заполнено поле Исполнители
//
//
//
// переход НА РЕЗОЛЮЦИИ -> НА ИСПОЛНЕНИИ
//
//
// все делаем по сценарию боба свифта

//    Create Sub-task
//    Conditions:
//    Ignore if original issue is a sub-task
//    Summary: %parent_summary%
//            Description: %parent_description%
//            Issue type: Поручение (10109)
//    Priority: Parent's priority
//    Reporter: Current user
//    Assignee: Specific user (%entry%)
//    Affected versions: None
//    Fixed versions: None
//    Components: Parent issue's components
//    Attachments: %original_key%, Transition attachments
//    Copy parent issue custom fields: Описание,Номенклатура дела,Подписант,Файл,Делопроизводитель,Согласующие,Вложение,Резолюция
//    Copy original issue custom fields: ФИО наблюдателей
//    Create multiple issues: %customfield_10301%,
////////////////////////////////

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.config.util.AttachmentPathManager
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.IssueFactory
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.issue.attachment.FileAttachments
import org.slf4j.LoggerFactory

import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.fields.CustomField

import com.atlassian.jira.issue.AttachmentManager
import com.atlassian.jira.issue.attachment.Attachment

import com.atlassian.jira.config.SubTaskManager

import com.onresolve.scriptrunner.runner.util.UserMessageUtil

///////////
// удалить
///////////
//Issue issue

IssueFactory issueFactory = ComponentAccessor.getIssueFactory()
IssueManager issueManager = ComponentAccessor.getIssueManager()
SubTaskManager subTaskManager = ComponentAccessor.getSubTaskManager()
CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager()

AttachmentManager attachmentManager = ComponentAccessor.getAttachmentManager()

AttachmentPathManager pathManager = ComponentAccessor.getAttachmentPathManager()

// поле исполнители 10301
CustomField customFieldIspolniteli = customFieldManager.getCustomFieldObject(10301L)
List<ApplicationUser> ispolniteli = (List<ApplicationUser>)issue.getCustomFieldValue(customFieldIspolniteli)

// текущий пользователь
ApplicationUser curUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

if (ispolniteli == null) {
    ispolniteli = new ArrayList<ApplicationUser>()
}

// сдесь простая проверка, если исполнители не заполнены то подзадачи не создаем
if (ispolniteli.size() == 0) {
    return
}

// тут тоже небольшая проверочка надо ли создавать подзадачи
// если подзадачи уже есть то ничего не создаем
Collection<Issue> subtasks = issue.getSubTaskObjects()
if (subtasks.size() > 0) {
    UserMessageUtil.error("Subtasks not created")
    return
}



// создаем подзадачи - по количеству пользователей в поле ИСПОЛНИТЕЛИ (переменная ApplicationUser)
for (ApplicationUser oneUser : ispolniteli) {

    MutableIssue newSubTask = issueFactory.getIssue()
    // поручение
    newSubTask.setIssueTypeId("10109")

    newSubTask.setAssignee(oneUser)

    newSubTask.setComponent(issue.getComponents())
    newSubTask.setReporter(curUser)
    newSubTask.setDescription(issue.getDescription())

    newSubTask.setSummary(issue.getSummary())
    newSubTask.setParentObject(issue)
    newSubTask.setProjectObject(issue.getProjectObject())
    newSubTask.setPriority(issue.getPriority())

    // приватный уровень безопасности
    newSubTask.setSecurityLevelId(issue.getSecurityLevelId())

    // присваиваем поле срок исполнения

    CustomField cfObj
    Object cfVal

    cfObj = customFieldManager.getCustomFieldObject(10019L)
    cfVal = issue.getCustomFieldValue(cfObj)


//    newSubTask.setDueDate(issue.getDueDate())
    newSubTask.setDueDate(cfVal)

//    newSubTask.set


    Map<String,Object> newIssueParams = ["issue" : newSubTask] as Map<String,Object>
    Issue newIssue = issueManager.createIssueObject(curUser, newIssueParams)
    subTaskManager.createSubTaskIssueLink(issue, newSubTask, curUser)


    // копирование полей в подзадачу
    // Copy parent issue custom fields:

    // Номенклатура дела - 10022
    // Подписант - 10031
    // Файл - 10043
    // Делопроизводитель - 10032
    // Согласующие - 10035
    // Резолюция - 10036

    // СРОК ИСПОЛНЕНИЯ - 10019

//    CustomField cfObj
//    Object cfVal

    cfObj = customFieldManager.getCustomFieldObject(10022L)
    cfVal = issue.getCustomFieldValue(cfObj)
    newIssue.setCustomFieldValue(cfObj, cfVal);
    issueManager.updateIssue(curUser, newIssue, EventDispatchOption.DO_NOT_DISPATCH, false)

    cfObj = customFieldManager.getCustomFieldObject(10031L)
    cfVal = issue.getCustomFieldValue(cfObj)
    newIssue.setCustomFieldValue(cfObj, cfVal);
    issueManager.updateIssue(curUser, newIssue, EventDispatchOption.DO_NOT_DISPATCH, false)

    cfObj = customFieldManager.getCustomFieldObject(10043L)
    cfVal = issue.getCustomFieldValue(cfObj)
    newIssue.setCustomFieldValue(cfObj, cfVal);
    issueManager.updateIssue(curUser, newIssue, EventDispatchOption.DO_NOT_DISPATCH, false)

    cfObj = customFieldManager.getCustomFieldObject(10032L)
    cfVal = issue.getCustomFieldValue(cfObj)
    newIssue.setCustomFieldValue(cfObj, cfVal);
    issueManager.updateIssue(curUser, newIssue, EventDispatchOption.DO_NOT_DISPATCH, false)

    cfObj = customFieldManager.getCustomFieldObject(10035L)
    cfVal = issue.getCustomFieldValue(cfObj)
    newIssue.setCustomFieldValue(cfObj, cfVal);
    issueManager.updateIssue(curUser, newIssue, EventDispatchOption.DO_NOT_DISPATCH, false)

    cfObj = customFieldManager.getCustomFieldObject(10036L)
    cfVal = issue.getCustomFieldValue(cfObj)
    newIssue.setCustomFieldValue(cfObj, cfVal);
    issueManager.updateIssue(curUser, newIssue, EventDispatchOption.DO_NOT_DISPATCH, false)



    // перекинем вложения с родительской задачи
    List<Attachment> attachmentList = attachmentManager.getAttachments(issue)
    for (Attachment oneAttach : attachmentList) {

//        File rootDir = new File(pathManager.attachmentPath)
//        File filePath = FileAttachments.getAttachmentDirectoryForIssue(rootDir, issue.projectObject.getKey(), issue.getKey())
//        String filePaths = filePath.toString()
//        filePaths = filePaths + "//" + oneAttach.getId()
//        File atFile = new File(filePaths)
//
//        if (atFile.exists()) {
//            attachmentManager.createAttachmentCopySourceFile(atFile, oneAttach.filename, oneAttach.mimetype, oneAttach.author, newIssue, [:], oneAttach.created)
//        }

        attachmentManager.copyAttachment(oneAttach, oneUser, newIssue.getKey().toString())


    }

//    UserMessageUtil.success("Создана подзадача для " + oneUser.getDisplayName())
    UserMessageUtil.success("Create subtask " + newIssue.getKey().toString()  + " for " + oneUser.getDisplayName())


}

