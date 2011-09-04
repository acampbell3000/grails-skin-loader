
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
    def version = "1.0.8"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.0.0.M1"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    // Plugin description
    def author = "Anthony Campbell"
    def authorEmail = "acampbell3000 [[at]] mail from google"
    def title = "Skin Loader"
    def description = "A simple plug-in which provides the ability to load \"skin\" into your application or plug-in."

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/skin-loader"
}
