import sun.security.util.Debug

// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if(System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }


grails.config.locations = []

if (System.properties["instance.confdir"])
    grails.config.locations << System.properties["instance.confdir"] + "/${appName}.properties"
else
    grails.config.locations << "classpath:${appName}.properties"

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [ html: ['text/html','application/xhtml+xml'],
                      xml: ['text/xml', 'application/xml'],
                      text: 'text/plain',
                      js: 'text/javascript',
                      rss: 'application/rss+xml',
                      atom: 'application/atom+xml',
                      css: 'text/css',
                      csv: 'text/csv',
                      all: '*/*',
                      json: ['application/json','text/json'],
                      form: 'application/x-www-form-urlencoded',
                      multipartForm: 'multipart/form-data'
                    ]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// whether to install the java.util.logging bridge for sl4j. Disable for AppEngine!
grails.logging.jul.usebridge = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// set per-environment serverURL stem for creating absolute links
environments {
    production {
        grails.serverURL = "http://autodeploy.corp.mobile.de/${appName}"
        grails.plugins.springsecurity.portMapper.httpPort = "80"
        grails.plugins.springsecurity.portMapper.httpsPort = "443"
    }
    mobile-production {
        grails.serverURL = "https://autodeploy.corp.mobile.de/${appName}"
        grails.plugins.springsecurity.portMapper.httpPort = "80"
        grails.plugins.springsecurity.portMapper.httpsPort = "443"
    }
    ebayk-production {
        grails.serverURL = "https://kautodeploy.corp.mobile.de/${appName}"
        grails.plugins.springsecurity.portMapper.httpPort = "80"
        grails.plugins.springsecurity.portMapper.httpsPort = "443"
    }
    development {
        grails.serverURL = "http://localhost:8080/${appName}"
    }
    test {
        grails.serverURL = "http://localhost:8080/${appName}"
    }

}

// log4j configuration
log4j = {
    root {
      //info 'stdout', 'file'
      info 'stdout'
    }
    // Example of changing the log pattern for the default console
    // appender:
    //
    appenders {
       console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
       // file name:'file', file:'/var/logs/mylog.log'
    }

    error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
           'org.codehaus.groovy.grails.web.pages', //  GSP
           'org.codehaus.groovy.grails.web.sitemesh', //  layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping', // URL mapping
           'org.codehaus.groovy.grails.commons', // core / classloading
           'org.codehaus.groovy.grails.plugins', // plugins
           'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate',
           'org.apache.zookeeper',
           'com.jbrisbin.vpc.zk'

    warn   'org.mortbay.log'

    error  'de.mobile.zookeeper'

    info 'org.springframework.security.ldap'

}

grails.plugins.springsecurity.secureChannel.definition = [
//   '/login/**':  'REQUIRES_SECURE_CHANNEL'
   '/api/**': 'ANY_CHANNEL',
   '/dashboard/**': 'ANY_CHANNEL',
   '/xml/versions/**': 'ANY_CHANNEL',
   '/json/versions/**': 'ANY_CHANNEL',
   '/**':  'REQUIRES_SECURE_CHANNEL'
]

// Added by the Spring Security Core plugin:
grails.plugins.springsecurity.userLookup.userDomainClassName = 'de.mobile.siteops.SecUser'
grails.plugins.springsecurity.userLookup.authorityJoinClassName = 'de.mobile.siteops.SecUserSecRole'
grails.plugins.springsecurity.authority.className = 'de.mobile.siteops.SecRole'

zooKeeper.url="localhost:2181"
zooKeeper.timeout=2000

// Configuration for Codahale (f.k.a. Yammer) metrics
metrics.graphite.host = ''
metrics.graphite.port = 2003
metrics.jmx.enabled = true
metrics.filter.enabled = true

// Active Directory config
grails.plugins.springsecurity.ldap.auth.hideUserNotFoundExceptions=false
grails.plugins.springsecurity.ldap.context.managerDn = '_auth_mobiledir@corp.ebay.com'
grails.plugins.springsecurity.ldap.context.managerPassword = 'Ohf1quec1pah6eephol6ye8biethahdahcah9Woh'
// stunnel4 is used to route ldap requests to ldaps
//grails.plugins.springsecurity.ldap.context.server = 'ldap://10.250.16.37/'
grails.plugins.springsecurity.ldap.context.server = 'ldap://localhost/'
grails.plugins.springsecurity.ldap.authorities.ignorePartialResultException = true // typically needed for Active Directory
grails.plugins.springsecurity.ldap.search.base = 'DC=CORP,DC=EBAY,DC=COM'
grails.plugins.springsecurity.ldap.search.filter="sAMAccountName={0}" // for Active Directory you need this
grails.plugins.springsecurity.ldap.search.searchSubtree = true
grails.plugins.springsecurity.ldap.auth.hideUserNotFoundExceptions = false
grails.plugins.springsecurity.ldap.search.attributesToReturn = ['mail', 'displayName'] // extra attributes you want returned; see below for custom classes that access this data

// comment out to allow at least admin user to come from the local database ...
//grails.plugins.springsecurity.providerNames = ['ldapAuthProvider', 'anonymousAuthenticationProvider'] // specify this when you want to skip attempting to load from db and only use LDAP

// role-specific Active Directory config
grails.plugins.springsecurity.ldap.useRememberMe = false
grails.plugins.springsecurity.ldap.authorities.retrieveDatabaseRoles = true  // do extra role lookup in database after LDAP
grails.plugins.springsecurity.ldap.authorities.retrieveGroupRoles = false
grails.plugins.springsecurity.ldap.authorities.groupSearchBase ='DC=CORP,DC=EBAY,DC=COM'
grails.plugins.springsecurity.ldap.authorities.groupSearchFilter = 'memberOf={0}' // Active Directory specific - the example settings will work fine for a plain LDAP server
// default role
//grails.plugins.springsecurity.ldap.authorities.defaultRole = 'ROLE_USER'
