package de.mobile.siteops

class NotificationService {

    static transactional = false

    def accessControlService

    private def notification


    def createNotification(message) {
        notification = [message: message, created: new Date(), user: accessControlService.getCurrentUser()]
    }

    def removeNotification() {
         notification = null
    }

    boolean hasNotification() {
        return notification != null
    }

    def getNotification() {
        return notification
    }
}
