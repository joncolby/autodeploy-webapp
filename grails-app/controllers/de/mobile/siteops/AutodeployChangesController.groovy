package de.mobile.siteops

class AutodeployChangesController {

    def index = {
        def changelog = getClass().getResourceAsStream('/changelog.txt').text
        [changelog: changelog]
    }
}
