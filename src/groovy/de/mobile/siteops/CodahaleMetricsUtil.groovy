package de.mobile.siteops

import java.util.concurrent.TimeUnit

import javax.servlet.ServletContext

import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.codahale.metrics.JmxReporter
import com.codahale.metrics.MetricFilter
import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.graphite.Graphite
import com.codahale.metrics.graphite.GraphiteReporter
import com.codahale.metrics.servlet.InstrumentedFilter

/**
 * Static utility class for Codahale metrics.
 */
class CodahaleMetricsUtil {
  private static final Logger log = LoggerFactory.getLogger(CodahaleMetricsUtil)
  private static final String GRAPHITE_PREFIX = 'autodeploy'

  static final MetricRegistry registry = new MetricRegistry()

  /**
   * Initialize Codahale metrics registry and reporting based on current configuration.
   * 
   * Must be called once from BootStrap.
   */
  static void initialize(ServletContext servletContext) {
    servletContext.setAttribute(InstrumentedFilter.REGISTRY_ATTRIBUTE, registry)

    String graphiteHost = ConfigurationHolder.config.metrics.graphite.host
    Integer graphitePort = ConfigurationHolder.config.metrics.graphite.port as Integer
    Boolean jmxEnabled = ConfigurationHolder.config.metrics.jmx.enabled.toBoolean()

    // Graphite
    if (graphiteHost) {
      String prefix = MetricRegistry.name(GRAPHITE_PREFIX, InetAddress.localHost.hostName.toLowerCase().split('\\.')[0])

      log.info("Reporting metrics to Graphite server ${graphiteHost}:${graphitePort} with prefix ${prefix}")
      Graphite graphite = new Graphite(new InetSocketAddress(graphiteHost, graphitePort))

      GraphiteReporter reporter = GraphiteReporter.forRegistry(registry)
          .prefixedWith(prefix)
          .convertRatesTo(TimeUnit.SECONDS)
          .convertDurationsTo(TimeUnit.MILLISECONDS)
          .filter(MetricFilter.ALL)
          .build(graphite)
      reporter.start(1, TimeUnit.MINUTES)
    } else {
      log.error("No Graphite server specified, metrics reporting disabled!")
    }

    // JMX (for development/testing only)
    if (jmxEnabled) {
      JmxReporter reporter = JmxReporter.forRegistry(registry).build()
      reporter.start()
    }
  }

  /**
   * Adds the servlet filter for Codahale metrics into the web.xml. Must be called from the plugin's
   * doWithWebDescriptor handler.
   * 
   * @see http://grails.1312388.n4.nabble.com/Adding-a-Servlet-Filter-into-web-xml-td1358783.html
   * @see http://metrics.codahale.com/manual/servlet/
   */
  static void addServletFilter(xml) {
    Boolean filterEnabled = ConfigurationHolder.config.metrics.filter.enabled.toBoolean()

    if (filterEnabled) {
      // add the filter(s) right after the last context-param
      def contextParam = xml.'context-param'

      contextParam[contextParam.size() - 1] + {
        'filter' {
          'filter-name'('webappMetricsFilter')
          'filter-class'(DefaultWebappMetricsFilter.name)
        }
      }

      // and the filter-mapping(s) right after the last filter
      def filter = xml.'filter'

      filter[filter.size() - 1] + {
        'filter-mapping'{
          'filter-name'('webappMetricsFilter')
          'url-pattern'('/*')
        }
      }
    }
  }
}
