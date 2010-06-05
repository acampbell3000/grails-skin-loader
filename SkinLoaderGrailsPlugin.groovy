class SkinLoaderGrailsPlugin {
    // the plugin version
    def version = "1.0.7"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.3.0 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    // Plugin description
    def author = "Anthony Campbell"
    def authorEmail = "acampbell3000 [[at] googlemail [dot]] com "
    def title = "Skin Loader for the Grails Web Framework"
    def description = '''\\
A simple plug-in which provides the ability to load "skins" into your application or plug-in.

The plug-in is designed to speed up the process further when developing grails applications. It will install templates, layout, images, CSS, JavaScript, i18n, configuration, Groovy / Java sources, utilities, services, and tag libraries into the relevant folders from an existing skin folder. Particularly useful for developers who use Grails frequently and find themselves performing these steps manually.

Grails Skin Loader by default also comes with the "remote-forms" skin. This will scaffold Ajax based forms which will provide real time validation when the form is used.

To install, run the following command:

	grails load-skin

'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/skin-loader"

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before 
    }

    def doWithSpring = {
        // TODO Implement runtime spring config (optional)
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { applicationContext ->
        // TODO Implement post initialization spring config (optional)
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }
}
