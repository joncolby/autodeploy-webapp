# autodeploy-webapp

Autodeploy is an agent-based deployment system written in Java/Grails.  It consists of several components:

* Autodeploy-webapp: Provides the Web UI, API, and deployment orchestration logic
* Autodeploy-agent:  Daemon process running on the target systems which listen for event triggers from the autodeploy-webapp
* autodeploy-script: The agent daemon will run an arbitrary deployment script to perform a deployment.  This script can be in any language, provided it can be called from the command line.

Additionally, these it uses these components for its architecture:
* a relational database: Structure is managed by GORM (MySQL recommended)
* Zookeeper:  The coordination framework and message bus


## More information

Autodeploy has a simple architecture.  However, the domain model and orchestration logic were programmed for specific processes at my company.  If you are interested in this software or deployment concept, please contact me for more information. 
  