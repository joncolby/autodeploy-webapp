import javax.servlet.http.HttpServletRequest

class BootStrap {

    def zookeeperHandlerService

    def init = { servletContext ->

        log.info("Entering bootstrap init")
        zookeeperHandlerService.init()


        HttpServletRequest.metaClass.isXhr = {->
            'XMLHttpRequest' == delegate.getHeader('X-Requested-With')
        }

        log.info("Leaving bootstrap init")
    }
    def destroy = {
    }
}
