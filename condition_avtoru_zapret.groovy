import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser

// поле руководитель 
def customFieldManager = ComponentAccessor.getCustomFieldManager()
def rukovoditelVSPCstFld = customFieldManager.getCustomFieldObject(10402L)

// руководитель 
ApplicationUser rukVSP = (ApplicationUser) issue.getCustomFieldValue(rukovoditelVSPCstFld)
// текущий пользователь
ApplicationUser curUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

passesCondition = false

if (curUser.equals(rukVSP)) {
    passesCondition = true
} else {
    passesCondition = false
}
