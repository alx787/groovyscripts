// проверяем по заданной задаче, которая является связанной по отношению
// к головной задаче исходящей связью, все такие же связанные задачи
// то есть  ЭТА ЗАДАЧА <- ГОЛОВНАЯ ЗАДАЧА
// ищем все задачи которые такие же как ЭТА ЗАДАЧА
// проверяем в каком они статусе

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.issue.link.IssueLink

IssueManager issueManager = ComponentAccessor.getIssueManager();


String results = ""

boolean retVal = true

// головные задачи
List<Issue> rootIssues = new ArrayList<Issue>()

List<IssueLink> allInIssueLink = ComponentAccessor.getIssueLinkManager().getInwardLinks(issue.getId());
for (Iterator<IssueLink> outIterator = allInIssueLink.iterator(); outIterator.hasNext();) {
	IssueLink issueLink = (IssueLink) outIterator.next();
 	String key = issueLink.getSourceObject().getKey();
 	results = results + key + "(" + issueLink.getIssueLinkType().getName() + "), ";
	rootIssues.add(issueLink.getSourceObject())
}

//
if (rootIssues.size() != 1) {
  retVal = false
} else {
  
	// головная задача
  	Issue parentIssue = rootIssues.get(0)

	// здесь перебираем все связанные (дочерние "исходящая связь") задачи
  	List<IssueLink> allOutIssueLink = ComponentAccessor.getIssueLinkManager().getOutwardLinks(parentIssue.getId());
 	for (Iterator<IssueLink> outIterator = allOutIssueLink.iterator(); outIterator.hasNext();) {
 		IssueLink issueLink = (IssueLink) outIterator.next();
      
      	log.warn(issueLink.getDestinationObject().getKey())
      	log.warn(issueLink.getDestinationObject().getStatusId())
      
      // статус "исполнено"
      if (issueLink.getDestinationObject().getStatusId() != "10110") {
        retVal = false
      }
 }  
  
}

  
//results

retVal
