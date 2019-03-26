//project = DOC AND statusCategory = Done AND resolution = Unresolved ORDER BY priority DESC, updated DESC

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.issue.Issue

def jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser)
def searchProvider = ComponentAccessor.getComponent(SearchProvider)
def issueManager = ComponentAccessor.getIssueManager()
def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

// edit this query to suit

def query = jqlQueryParser.parseQuery("project = DOC AND status in (Исполнено, Отправлено) AND resolution = Unresolved ORDER BY priority DESC, updated DESC")
//def query = jqlQueryParser.parseQuery("project = DOC AND statusCategory = Done AND resolution = Unresolved ORDER BY priority DESC, updated DESC")
//def query = jqlQueryParser.parseQuery("project = DOC AND statusCategory = Done AND resolution = Unresolved AND id = DOC-3649 ORDER BY priority DESC, updated DESC")



def results = searchProvider.search(query, user, PagerFilter.getUnlimitedFilter())

log.warn("Total issues: ${results.total}")

results.getIssues().each {documentIssue ->
    log.warn(documentIssue.key)

    // if you need a mutable issue you can do:
    def issue = issueManager.getIssueObject(documentIssue.id)
    
    if (issue.getStatusId() == "10113") {
	    log.warn("отправлено")
    	issue.setResolutionId("10100")
    } else {
	    log.warn("исполнено")
    	issue.setResolutionId("10102")
        
    }
    
    issue.store()
    

    // do something to the issue...
    log.warn(issue.summary)
}
