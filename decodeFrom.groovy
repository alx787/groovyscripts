//String testString = "Наладить доступ к цветному принтеру директора из приемной. (Ошибка: принтер не подключен). (давняя проблема) [Created via e-mail received from: =?koi8-r?B?xxxxxxxx=?= <xxx@xxx.ru>]"

// заполнение полей по информации из описания задачи
// нужно выделить подстроку вида "[Created via e-mail received from: =?koi8-r?B?xxxxxxxx=?= <xxx@xxx.ru>]"
// и по ней заполнить поля
// далее при переходе можно будет добавить отправку сообщения пользователю в постфункции

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder

import javax.mail.internet.MimeUtility

CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager()

CustomField userPodr = customFieldManager.getCustomFieldObject(10302L)
CustomField userName = customFieldManager.getCustomFieldObject(10300L)
CustomField userEmail = customFieldManager.getCustomFieldObject(10301L)


//log.warn(issue.toString())


String summaryText = issue.getDescription()

// реверсим строку
String reversString = summaryText.reverse()
//
int closeSq = reversString.indexOf("]")
int openSq = reversString.indexOf("[")
int lenString = summaryText.length()

//log.warn("===== string from summary from " + String.valueOf(openSq) + " to " + String.valueOf(closeSq))

if ((closeSq != -1) && (openSq != -1)) {
  
	String mailFromText = summaryText.substring(lenString - openSq - 1, lenString - closeSq)
	///////////////////////////
	String nameFromAlias = mailFromText

	log.warn("===== string from summary: " + mailFromText)
  
  	// имя пользователя
  	int nameOpenSq = nameFromAlias.indexOf("=?")
  	int nameCloseSq = nameFromAlias.indexOf("?=")
  
  	if ((nameOpenSq != -1) && (nameCloseSq != -1)) {
		nameFromAlias = nameFromAlias.substring(nameFromAlias.indexOf("=?"));
		nameFromAlias = nameFromAlias.substring(0, nameFromAlias.indexOf("?=") + 2);
		//issue.setCustomFieldValue(userName, MimeUtility.decodeText(nameFromAlias))      	
      
		userName.updateValue(null, issue, new ModifiedValue(userName.getValue(issue), MimeUtility.decodeText(nameFromAlias)), new DefaultIssueChangeHolder())      
	    //log.warn("===== nameFromAlias: " + MimeUtility.decodeText(nameFromAlias))
    }
  
  
	String mailFromAddress = mailFromText
  
  	// емайл пользователя
	int emailOpenSq = mailFromAddress.indexOf("<")
	int emailCloseSq = mailFromAddress.indexOf(">")

  	if ((emailOpenSq != -1) && (emailCloseSq != -1)) {
		mailFromAddress = mailFromAddress.substring(mailFromAddress.indexOf("<") + 1);
		mailFromAddress = mailFromAddress.substring(0, mailFromAddress.indexOf(">"));
		//issue.setCustomFieldValue(userEmail, mailFromAddress)      	
		userEmail.updateValue(null, issue, new ModifiedValue(userEmail.getValue(issue), mailFromAddress), new DefaultIssueChangeHolder())      
      	
		//log.warn("===== email: " + mailFromAddress)
  	}
  
}

// подразделение пока не трогаем
//issue.setCustomFieldValue(userPodr, "")
