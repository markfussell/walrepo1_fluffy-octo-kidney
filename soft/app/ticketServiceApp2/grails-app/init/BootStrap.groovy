class BootStrap {
    def domainLoaderService

    def init = { servletContext ->
        domainLoaderService.handleInit();
    }

    def destroy = {
    }
}
