
/**
 * Copyright 2010 Anthony Campbell (anthonycampbell.co.uk)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Skin Loader Plug-in
 *
 * A simple plug-in which provides a skin loader for your Grails
 * application.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
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
}
