import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.customfields.option.Option


log.warn(" ===================== =====================")
log.warn(" ===================== =====================")
log.warn(" ===================== =====================")
log.warn(" ===================== =====================")


//issue
//originalIssue

CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager()

//////////////////////////////////////////////////
// номенклатура дела
//////////////////////////////////////////////////

//log.warn("== nomenkl ==")

CustomField nomenklCf = customFieldManager.getCustomFieldObject(10022L)

Map nomenklValCfNew = (Map)issue.getCustomFieldValue(nomenklCf)
Map nomenklValCfOld = (Map)originalIssue.getCustomFieldValue(nomenklCf)

if (nomenklValCfNew != nomenklValCfOld) {
	issue.setCustomFieldValue(nomenklCf, nomenklValCfOld)
}

//////////////////////////////////////////////////
// должность
//////////////////////////////////////////////////

//log.warn("== dolgn ==")

CustomField dolgnCf = customFieldManager.getCustomFieldObject(10200L)

Option dolgnValCfNew = (Option)issue.getCustomFieldValue(dolgnCf) 
Option dolgnValCfOld = (Option)originalIssue.getCustomFieldValue(dolgnCf) 

if (dolgnValCfNew != dolgnValCfOld) {
	issue.setCustomFieldValue(dolgnCf, dolgnValCfOld)
}

//////////////////////////////////////////////////
// телефон автора
//////////////////////////////////////////////////

//log.warn("== phone ==")

CustomField telAvtCf = customFieldManager.getCustomFieldObject(10033L)

String telAvtValCfNew = (String)issue.getCustomFieldValue(telAvtCf) 
String telAvtValCfOld = (String)originalIssue.getCustomFieldValue(telAvtCf) 

if (!telAvtValCfNew.equals(telAvtValCfOld)) {
	issue.setCustomFieldValue(telAvtCf, telAvtValCfOld)
}

//////////////////////////////////////////////////
// способ отправки
//////////////////////////////////////////////////

//log.warn("== sposob ==")

CustomField sposobOtprCf = customFieldManager.getCustomFieldObject(10020L)

Option sposobOtprValCfNew = (Option)issue.getCustomFieldValue(sposobOtprCf) 
Option sposobOtprValCfOld = (Option)originalIssue.getCustomFieldValue(sposobOtprCf) 

if (sposobOtprValCfNew != sposobOtprValCfOld) {
	issue.setCustomFieldValue(sposobOtprCf, sposobOtprValCfOld)
}

//////////////////////////////////////////////////
// тема
//////////////////////////////////////////////////

//log.warn("== summary ==")

String newSummary = issue.getSummary()
String oldSummary = originalIssue.getSummary()

if (!newSummary.equals(oldSummary)) {
	issue.setSummary(oldSummary)  
}

