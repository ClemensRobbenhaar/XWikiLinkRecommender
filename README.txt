Enable Link Recommender and Extension Plug-in in XWiki
- download xwiki 5.4.4 from http://forge.ow2.org/project/showfiles.php?group_id=170 (http://enterprise.xwiki.org/xwiki/bin/view/Main/Download --> older versions)
- use Link Recommender Code from CD or import it from github: https://github.com/haenna89/XWikiLinkRecommender/tree/ontology_extension
- copy classes to XWIKI_HOME/WEB-INF/classes; e.g. by "ant deploy"
- modify XWIKI_HOME/WEB-INF/xwiki.cfg; in the section #-# List of active plugins add
  the plug-in class names "de.csw.xwiki.plugin.OntologyPlugin"
  and "de.csw.linkgenerator.plugin.lucene.LucenePlugin"
- in the same file add the property (or change it if already set):
    xwiki.authentication.rightsclass=de.csw.xwiki.CSWXWikiCachingRightService
- change path to your xwiki installation in build.properties.user file

- modify XWIKI_HOME/WEB-INF/struts-config.xml; inside the <action-mapping>  add some lines like:
    	<action path="/cswlinks/"
        	type="de.csw.linkgenerator.struts.CSWLinkAction"
        	name="cswlinks"
        	scope="request" />
	<action path="/ontologyexautocomplete/"
        	type="de.csw.ontologyextension.struts.AutoComplete"
        	name="ontologyexautocomplete"
        	scope="request"
		contentType="json">
	</action>
	<action path="/ontologyexaddclass/"
        	type="de.csw.ontologyextension.struts.Addition"
        	name="ontologyexaddclass"
        	scope="request">
	</action>
	<action path="/ontologyexdeleteclass/"
        	type="de.csw.ontologyextension.struts.Deleting"
        	name="ontologyexdeleteclass"
        	scope="request">
	</action>
	<action path="/ontologyexeditclass/"
        	type="de.csw.ontologyextension.struts.Relations"
        	name="ontologyexeditclass"
        	scope="request"
		contentType="json">
	</action>
	<action path="/ontologyexgetrel/"
        	type="de.csw.ontologyextension.struts.Update"
        	name="ontologyexgetrel"
        	scope="request">
	</action>
        <action path="/ontologyexgetclasses/"
        	type="de.csw.ontologyextension.struts.Classes"
        	name="ontologyexgetclasses"
        	scope="request">
	</action>
- See /resources/xwiki/Panel.README for further information about including panels



Eclipse specific
- the classpath in eclipse is configured for XWiki 5.4.4
- you should set the classpath variable "XWIKI5_HOME" to the root directory of the xwiki-jetty installation

Configure Tomcat for Debugging
- add the following options to the startup of tomcat
	-Xdebug
	-Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n
- in eclipse create a new Debug Configuration
  - Go to "Run->Debug Configurations...".
  - Click on "Remote Java Applications", then click "New".
  - Type in the title and all.
  - Notice that port 8000 from the Tomcat instructions.
  - Save and run.

Configure Jetty for Debugging
- in the XWiki package there already exists a start file for debugging
  - some lines corresponding to profiling have to be commented out
- in eclipse create a new Debug Configuration
  - Go to "Run->Debug Configurations...".
  - Click on "Remote Java Applications", then click "New".
  - Type in the title and all.
  - Notice they use port 5005 as standard.
  - Save and run.

Documentation
- XWiki API 
  for scripting: http://platform.xwiki.org/xwiki/bin/view/SRD/Navigation?xpage=embed
  XWiki core api: http://nexus.xwiki.org/nexus/service/local/repositories/releases/archive/org/xwiki/platform/xwiki-platform-oldcore/5.4.4/xwiki-platform-oldcore-5.4.4-javadoc.jar/!/index.html
