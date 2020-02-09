import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.event.type.EventDispatchOption

def customFieldManager = ComponentAccessor.getCustomFieldManager()
def issueManager = ComponentAccessor.getIssueManager()
def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

def cf = customFieldManager.getCustomFieldObject(10000L)


def jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser)
def searchProvider = ComponentAccessor.getComponent(SearchProvider)

//def user = ComponentAccessor.getJiraAuthenticationContext().getUser()

// edit this query to suit
def query = jqlQueryParser.parseQuery("project = ZVK")

def results = searchProvider.search(query, user, PagerFilter.getUnlimitedFilter())

log.debug("Total issues: ${results.total}")

results.getIssues().each {documentIssue ->

    log.debug("=========================")

    log.debug(documentIssue.key)

    // if you need a mutable issue you can do:
    def issue = issueManager.getIssueObject(documentIssue.id)
  
	issue.setCustomFieldValue(cf,"alxlog")
	issueManager.updateIssue(user, issue, EventDispatchOption.DO_NOT_DISPATCH, false)
  

    // do something to the issue...
    log.debug(issue.summary)
  	log.debug(issue.getSecurityLevelId().toString())
}
