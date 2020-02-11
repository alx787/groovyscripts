
LinkedHashMap<String, String>[] issueProps = [
["obj": "Обработка.ШаблонДляВременнойРегистрации", "descr": "Обработка.Шаблон для временной регистрации", "size": "12 386", "rows": "137", "columns": "3", "req": "42", "hh":"2,2347", "hd":"0,37245"],
["obj": "Обработка.СозданиеНовогоДок1ТР", "descr": "Обработка.Создание нового док 1ТР_МРСК", "size": "3 287", "rows": "47", "columns": "", "req": "40", "hh":"1,0179", "hd":"0,16965"],
["obj": "Обработка.ВыборкаПоМаркамШин", "descr": "Обработка.Выборка по маркам шин", "size": "10 908", "rows": "112", "columns": "10", "req": "40", "hh":"2,2464", "hd":"0,3744"],
["obj": "Обработка.КоличествоТСпоАКтипамТСвидамтоплива", "descr": "Обработка.Количество ТС по АК типам ТС видам топлива", "size": "18 015", "rows": "167", "columns": "7", "req": "40", "hh":"2,7495", "hd":"0,45825"],
["obj": "Обработка.ОтчетПоМаркамПоАК", "descr": "Обработка.Отчет по маркам по АК", "size": "15 075", "rows": "156", "columns": "5", "req": "40", "hh":"2,5272", "hd":"0,4212"],
]
//strArr[1].size()
//issueProp[1].rows

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.bc.issue.IssueService.IssueResult
import com.atlassian.jira.bc.issue.IssueService
import com.atlassian.jira.bc.issue.IssueService.CreateValidationResult
import com.atlassian.jira.issue.IssueInputParameters

import com.atlassian.jira.project.Project
import com.atlassian.jira.project.ProjectManager
import com.atlassian.jira.user.util.UserManager
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.MutableIssue

IssueService issueService = ComponentAccessor.getIssueService()
ProjectManager projectManager = ComponentAccessor.getProjectManager()
UserManager userManager = ComponentAccessor.getUserManager()

String projName = "SD"
Project proj = projectManager.getProjectObjByKey(projName)

String userName = "jadmin"
ApplicationUser user = userManager.getUserByName(userName)

MutableIssue mutableIssue
String issueDescr = ""

for (LinkedHashMap<String, String> oneProp : issueProps) {
  log.warn(oneProp.toString())
  
  
    IssueInputParameters issueInputParameters = issueService.newIssueInputParameters()
  
    issueInputParameters.setProjectId(proj.getId())
  
    issueInputParameters.setIssueTypeId("10001") // history
    issueInputParameters.setStatusId("10000") // сделать

    issueInputParameters.setSummary(oneProp["obj"])

//    issueInputParameters.setDescription(issue.getDescription())


    issueDescr = "Объект:" + oneProp["obj"] + "\n\n" \
                + "Описание: " + oneProp["descr"] + "\n" \
                + "размер:   " + oneProp["size"] + "\n\n" \
                + "количество строк:      " + oneProp["rows"] + "\n" \
                + "количество столбцов:   " + oneProp["columns"] + "\n" \
                + "количество реквизитов: " + oneProp["req"] + "\n\n" \
                + "человеко час:  " + oneProp["hh"] + "\n" \
                + "человеко день: " + oneProp["hd"] + "\n"



    issueInputParameters.setDescription(issueDescr)

    issueInputParameters.setPriorityId("3") // string
  
    issueInputParameters.setReporterId(user.getKey())
    issueInputParameters.setAssigneeId(user.getKey())  
  

   CreateValidationResult createValidationResult = issueService.validateCreate(user, issueInputParameters)

    if (createValidationResult.isValid())
    {
        log.error("entrou no createValidationResult")
        IssueResult createResult = issueService.create(user, createValidationResult)
        if (!createResult.isValid())
        {
            log.error("Error while creating the issue.")
        } else {
            mutableIssue = createResult.getIssue();
            log.warn(" ==================== new issue  ")
            log.warn(mutableIssue.toString())

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
  
