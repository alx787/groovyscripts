import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.link.IssueLink

// 1 - получим значение поля Дата документа в текущей задаче
def cfManager = ComponentAccessor.getCustomFieldManager()
def cfDocDate = cfManager.getCustomFieldObject(10007L)
def valDocDate = issue.getCustomFieldValue(cfDocDate) 

// менеджер поля Дата входящего документа
def cfDocInputDate = cfManager.getCustomFieldObject(10007L)

// 2 - найдем все связанные задачи с типом исходящее письмо
List<IssueLink> allOutIssueLink = ComponentAccessor.getIssueLinkManager().getOutwardLinks(issue.getId());
for (Iterator<IssueLink> outIterator = allOutIssueLink.iterator(); outIterator.hasNext();) {
	IssueLink issueLink = (IssueLink) outIterator.next()

	def linkedIssue = issueLink.getDestinationObject()
  	// письмо исходящее
  	if (linkedIssue.getIssueType().getId() == "10105") {
		// 3 - в цикле переберем все задачи и установим значение реквизита Дата входящего документа
    	
  	}
  
  
  
	log.debug(linkedIssue.toString()) 
}



