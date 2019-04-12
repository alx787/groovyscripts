import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.project.Project
import com.atlassian.jira.project.ProjectManager
import com.atlassian.jira.security.roles.ProjectRole
import com.atlassian.jira.security.roles.ProjectRoleActors
import com.atlassian.jira.security.roles.ProjectRoleManager


ProjectManager projectManager = ComponentAccessor.getProjectManager()
ProjectRoleManager projectRoleManager = ComponentAccessor.getComponentOfType(ProjectRoleManager.class) as ProjectRoleManager

Project proj = projectManager.getProjectObjByName("Обращения клиентов")

// name of role here
ProjectRole devsRole = projectRoleManager.getProjectRole("Команда обработки обращений")
ProjectRoleActors actors = projectRoleManager.getProjectRoleActors(devsRole, proj)
  	
// List<ApplicationUser>
actors.getUsers().toList()
 
