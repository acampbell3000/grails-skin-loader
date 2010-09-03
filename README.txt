Copyright 2010 Anthony Campbell (anthonycampbell.co.uk)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Based on Scaffold Tag Plugin by Daiji Takamoro.

--------------------------------------
Grails Skin Loader
--------------------------------------

A simple plug-in which provides the ability to load "skins" into
your application or plug-in.

The plug-in is designed to speed up the process further when
developing grails applications. It will install templates, layout,
images, CSS, JavaScript, i18n, configuration, Groovy / Java sources,
utilities, services, and tag libraries into the relevant folders from
an existing skin folder. Particularly useful for developers who use
Grails frequently and find themselves performing these steps manually.

Grails Skin Loader by default also comes with the "remote-forms" skin.
This will scaffold Ajax based forms which will provide real time
validation when the form is used.

Unlike ScaffoldTags this plug-in only provides the ability to pre-install
the range of resources it supports. It does not attempt to provide the
additional ScaffoldTags which allow the views to auto update upon domain
class change. This helps reduce the plug-in's complexity, management, and
hopefully increase the plug-in's uptake.

If you find any issues, please submit a bug on JIRA:

     http://jira.codehaus.org/browse/GRAILSPLUGINS

Please look at the CHANGES file to see what has changed since the last 
official release.

----------------------
Upgrading from an earlier release
----------------------
There shouldn't be any issues upgrading from an earlier release.
You'll need to blow away the older GrailsSkinLoader plugin directory,
as usual.

------------------------
Installation:
------------------------
Grails Skin Loader comes with basic scaffolding views that replace
the core grails scaffolding view templates. To install these, run
the following command:

	grails load-skin

If you already have your own scaffold templates in src/templates, 
I suggest you make backups before running this command.  It will
warn you before erasing/overwriting anything, though.

It will prompt you to select a skin.  Currently the following skins are provided:
- remote-forms - makes use of prototype and script.aculo.us and the existing
remote form tags provided by Grails. Allows the forms generated through
scaffolding to be Ajax driven.

After doing that, you can generate your views as you normally would 
(or use Grails scaffolding as you normally would).

----------------------
Tips:
----------------------
* Sometimes you might find it necessary to run a grails clean in order 
to eliminate a view you created that has been copied to the WEB-INF 
directory but which no longer exists in your views / scaffold.

* Sometimes you might find it necessary to clean up the plugin installation
if the scaffolding you used before leaves some files behind. This will be
improved upon in a future release.

------------------------
Changing skins:
------------------------
You can change view skins used via
	grails load-skin <skinname>

If you don't specify a skin, it will look at all the skins available 
in the skins directory of the plugin.

------------------------
Creating a skin:
------------------------
When creating a skin, I recommend providing enough templates such that 
it can stand alone.  You may want to package up your rendering templates
into a skin that you can reuse across applications; currently these can 
then be used by other applications by placing them in the skins directory 
of the plugin after installing the plugin.

------------------------
Removing a skin:
------------------------
If you don't have a need for a given skin, you can blow it away from the 
skins directory.
