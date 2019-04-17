import com.atlassian.jira.component.ComponentAccessor

def projectManager = ComponentAccessor.getProjectManager()
def pluginAccessor = ComponentAccessor.getPluginAccessor()
def projectPropertiesManager = ComponentAccessor.getOSGiComponentInstanceOfType(pluginAccessor
       .getClassLoader().findClass("com.tse.jira.projectproperties.plugin.api.ProjectPropertiesAOMgr"))

def int N
def String NDoc
def property = projectPropertiesManager.getProjectPropertyByKeys("DOC", "Порядковый № документа - исходящий")
	if(property != null) {
	  NDoc = property.getPropertyValue()
      if (NDoc.isInteger()) {
        N = NDoc as Integer
        N = N + 1
        NDoc = N as String
        def propertyId = projectPropertiesManager.updateProjectProperty("DOC", "Порядковый № документа - исходящий", NDoc)
        Value = NDoc
      }
	}
