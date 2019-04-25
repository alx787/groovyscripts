// процесс - заявки из 
// переход - создание заявки

// в зависимости от значения селективного поля "Вид заявки " заполняем поле "Исполнители " теми или иными пользователями


import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.security.groups.GroupManager
import com.onresolve.scriptrunner.runner.util.UserMessageUtil

import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.util.IssueChangeHolder
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder

import com.atlassian.mail.Email
import com.atlassian.mail.server.SMTPMailServer

// логгер
//import org.apache.log4j.Logger
// доступ к сохраненным настройкам плагина
import com.onresolve.scriptrunner.runner.util.OSPropertyPersister
// джейсон парсер
import groovy.json.JsonSlurper

// для моего модуля
import com.onresolve.scriptrunner.runner.customisers.PluginModule
import com.onresolve.scriptrunner.runner.customisers.WithPlugin
import ru.hlynov.oit.alex.api.UserBossService
import com.onresolve.scriptrunner.runner.ScriptRunnerImpl

@WithPlugin("ru.hlynov.oit.alex.groupbosses")
//@PluginModule
//UserBossService userBossService
UserBossService userBossService = ScriptRunnerImpl.getPluginComponent(UserBossService)



// процедура отсылки сообщения 
// Create an email
def sendEmail(String emailAddr, String subject, String body) {
    SMTPMailServer mailServer = ComponentAccessor.getMailServerManager().getDefaultSMTPMailServer()
    if (mailServer) {
        Email email = new Email(emailAddr)
        email.setSubject(subject)
        email.setBody(body)
        mailServer.send(email)
        log.debug("Mail sent")
    } else {
        log.warn("Please make sure that a valid mailServer is configured")
    }
}


//log.warn("======================= issue = " + issue.getKey() + " start of postfunction =======================")

// получаем компоненты менеджеры
def watcherManager = ComponentAccessor.getWatcherManager()
def customFieldManager = ComponentAccessor.getCustomFieldManager()
//def groupManager = ComponentAccessor.getGroupManager()
def userManager = ComponentAccessor.getUserManager()
//IssueManager issueManager = ComponentAccessor.getIssueManager()

IssueChangeHolder changeHolder = new DefaultIssueChangeHolder()

def ispolniteliCstFld = customFieldManager.getCustomFieldObject(10100L)
def vidZayavkiFld = customFieldManager.getCustomFieldObject(10300L)



// Текущий пользователь
//ApplicationUser curUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

//log.warn("============================ issue =========================")
//log.warn(issue)


// проверка на заполненность вида заявлки
//Object zayavkaObj = issue.getCustomFieldValue(vidZayavkiFld)
//if (zayavkaObj == null) {
//    log.warn("Не найдено поле Вид заявки ОФМ")
//    UserMessageUtil.warning("Не найдено поле Вид заявки ОФМ")
//    return
//}

String zayavkaStr = (String) issue.getCustomFieldValue(vidZayavkiFld)


// промежуточный массив для исполнителей
List<ApplicationUser> approversOFM = []


// читаем сохраненные параметры
def pluginSettings = OSPropertyPersister.getPluginSettings()
String savedValue = pluginSettings.get("ru.alex.settings.ofm")

if (savedValue == null) {
    log.warn("исполнители не выбраны !!!")
    UserMessageUtil.warning("исполнители не выбраны !!!")
    return
}


JsonSlurper jsonSlurper = new JsonSlurper()
def objJson = jsonSlurper.parseText(savedValue)

// комментарим за ненадобностью

objJson.each { typeZayav ->

    if (typeZayav.ztype == "vofm") {
        typeZayav.params.each { oneParam ->

            if (oneParam.name == zayavkaStr) {
                oneParam.users.each { oneUser ->
                    // добавляем наблюдателей

                    ApplicationUser userFromJson = userManager.getUserByName(oneUser);

                    if (userFromJson.isActive()) {
                        approversOFM.add(userFromJson)

//                        // тут же добавляем всех в наблюдатели
//                        watcherManager.startWatching(userFromJson, issue)
//
//                        // отправляем сообщение
//                        sendEmail(userFromJson.getEmailAddress(), "script runner subject", "script runner body")

                    }
                }
            }
        }
    }
}


// добавить в наблюдатели начальника ВСП
//Issue issue
String userBossName = userBossService.getUserBoss(issue.getReporter().getUsername())
if (!userBossName.equals("")) {
    ApplicationUser nachVSP = userManager.getUserByName(userBossName)
    watcherManager.startWatching(nachVSP, issue)
}


if (approversOFM.size() != 0) {
    // сохраняем значение в поле так
    ispolniteliCstFld.updateValue(null, issue, new ModifiedValue(null, approversOFM), changeHolder)
}


//issue.store()
// или так  issue.setCustomFieldValue(cf, newApprovers)
//issue.setCustomFieldValue(cf, approversOFM)


//log.warn("======================= issue = " + issue.getKey() + " end of postfunction =======================")


//for(ApplicationUser user: (ArrayList<ApplicationUser>) multiuserCstFld.getValue(issue)) {
//    if (user) {
//        watcherManager.startWatching(user, issue)
//    }
//}

