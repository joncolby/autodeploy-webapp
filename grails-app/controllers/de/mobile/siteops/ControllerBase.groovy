package de.mobile.siteops


abstract class ControllerBase {


      def getChosenApps() {

          def applicationList = params.applications

          if (applicationList instanceof String) {
            applicationList = []
            applicationList << params.applications
          }

          def applicationChosen = applicationList.collect{Application.get(it)}

          return applicationChosen
    }

        def getChosenHostClasses() {

          def hostClassesList = params?.hostClasses

          if (hostClassesList instanceof String) {
            hostClassesList = []
            hostClassesList << params.hostClasses
          }

          return hostClassesList.collect{HostClass.get(it)}
    }


}
