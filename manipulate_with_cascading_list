import com.atlassian.jira.component.ComponentAccessor 
import com.atlassian.jira.issue.CustomFieldManager 
import com.atlassian.jira.issue.MutableIssue 
import com.atlassian.jira.issue.fields.CustomField

CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager()

//MutableIssue issue = ComponentAccessor.getIssueManager().getIssueObject('JRA-6161') 
MutableIssue mutIssue = issue

CustomField cf = customFieldManager.getCustomFieldObject(10022L) 
Map cfVal = mutIssue.getCustomFieldValue(cf) as Map

String retVal = ""

if (cfVal) { 
  String first = cfVal.get(null) 
  String second = cfVal.get("1") 
  List allValues = cfVal.values() as List 
  //log.debug("First - second: $first - $second") 
  //log.debug("All: $allValues") 
  
  String firstPart = ""
  String secondPart = ""
  
  String[] arrStr = first.split()
  
  arrStr = first.split()
  if (arrStr.size() > 0) {
    firstPart = arrStr[0]
  }
  
  if (second) {
    arrStr = second.split()
    if (arrStr.size() > 0) {
      secondPart = arrStr[0]
    }
  }
  
  if (!secondPart) {
	retVal = firstPart
  } else {
    retVal = firstPart + "/" + secondPart
    //retVal = secondPart
  }

  
  
}
else { 
  //log.debug("Custom field not present on this issue") 
}

retVal
