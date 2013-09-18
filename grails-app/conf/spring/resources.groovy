import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH
import com.mchange.v2.c3p0.ComboPooledDataSource

// Place your Spring DSL code here
beans = {
    /**
    * c3P0 pooled data source that allows 'DB keepalive' queries
    * to prevent stale/closed DB connections
    * Still using the JDBC configuration settings from DataSource.groovy
    * to have easy environment specific setup available
    */

   dataSource(ComboPooledDataSource) { bean ->
     bean.destroyMethod = 'close'
     //use grails' datasource configuration for connection user, password, driver and JDBC url
     user = CH.config.dataSource.username
     password = CH.config.dataSource.password
     driverClass = CH.config.dataSource.driverClassName
     jdbcUrl = CH.config.dataSource.url

     testConnectionOnCheckin=true
     maxConnectionAge=900
     initialPoolSize=10
     numHelperThreads=100
     minPoolSize=25
     maxPoolSize=100
     maxIdleTime=300
     maxIdleTimeExcessConnections=120
     idleConnectionTestPeriod=60
     connectionTesterClassName="com.mysql.jdbc.integration.c3p0.MysqlConnectionTester"

     // dont set preferred test query
     //preferredTestQuery="SELECT 1;"

     /* very dangerous. destroys connects after n seconds */
     //unreturnedConnectionTimeout = 5 // seconds
     //debugUnreturnedConnectionStackTraces="true"


  }

}
