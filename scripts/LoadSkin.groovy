/*
 * Copyright 2010 Anthony Campbell (anthonycampbell.co.uk)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Groovy Ant Script that installs a skin. Based on Scaffold Tag
 * Plugin by Daiji Takamoro.
 * 
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
grailsHome = Ant.antProject.properties."environment.GRAILS_HOME"
includeTargets << grailsScript( "_GrailsInit" )

// Override default to run load-skin instead of init
setDefaultTarget("loadSkin")

target (loadSkin: "Installs a skin from the plugin skin directory") {
    depends(init, parseArguments)

    // Get plug-in version number
    final def version = metadata["plugins.skin-loader"]
    if (!version) {
        version = metadata?.getApplicationVersion()
    }
    
    pluginHome = "${pluginsHome}/skin-loader-${version}"
    pluginSkins = "${pluginHome}/src/skins"
    appSkins = "${basedir}/src/skins"

	// Initial output
	println "\n====== Grails Skin Loader (${version}) ======\n"
	println "Base directory: ${basedir}"
	println "Plug-in home: ${pluginHome}"
	println "Plug-in skins directory: ${pluginSkins}"
	println "Base skins directory: ${appSkins}"
	println "\n================ BEGIN =================\n"
    
    // Obtain the skin name; make sure it exists
    pluginArgs = argsMap['params']
    skin = pluginArgs[0]
    first = true

    // Check we have skin name
    while (first || !skin) {
        first = false
        
        // Get list of available skins
        availableSkins = new File(pluginSkins)?.list()?.toList()

        // Check whether base directory also contains any skins
        if (new File(appSkins)?.exists()) {
            availableSkins?.addAll(new File(appSkins)?.list()?.toList())
        }

        // If not parameter provided list all skins
        if (!skin) {
            println "Available skins under grails-app/skins directories:"
            availableSkins?.each {
                println "\t${it}"
            }
            
            println "Skin name not specified. Please enter:"
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in))
            skin = br?.readLine()
        }

        if (!availableSkins?.contains(skin)) {
            println "Skin \"${skin}\" not available!"
            skin = null
        }
    }

    skinDir = "${appSkins}/${skin}"
    if (!new File(skinDir)?.exists()) {
        skinDir = "${pluginSkins}/${skin}"
    }

    println "Skin directory: ${skinDir}\n\n"
    
    // Establish initial variables
    skinSources = [ conf: "${skinDir}/conf",
                scaffolding: "${skinDir}/scaffolding",
                layouts: "${skinDir}/layouts",
                taglib: "${skinDir}/taglib",
                groovycode: "${skinDir}/src/groovy",
                javacode: "${skinDir}/src/java",
                css: "${skinDir}/css",
                images: "${skinDir}/images",
                js: "${skinDir}/js",
                i18n: "${skinDir}/i18n",
                services: "${skinDir}/services",
                utils: "${skinDir}/utils",
                integration: "${skinDir}/test/integration" ]
    baseSources = [ conf: "${basedir}/grails-app/conf",
                scaffolding: "${basedir}/src/templates/scaffolding",
                layouts: "${basedir}/grails-app/views/layouts",
                taglib: "${basedir}/grails-app/taglib",
                groovycode: "${basedir}/src/groovy",
                javacode: "${basedir}/src/java",
                css: "${basedir}/web-app/css",
                images: "${basedir}/web-app/images",
                js: "${basedir}/web-app/js",
                i18n: "${basedir}/grails-app/i18n",
                services: "${basedir}/grails-app/services",
                utils: "${basedir}/grails-app/utils",
                integration: "${basedir}/test/integration" ]
    clearTargets = [ 'scaffolding', 'layouts', 'css', 'images', 'js' ]
    skinSourceFiles = [ index: "${skinDir}/index.gsp" ]
    baseSourceFiles = [ index: "${basedir}/grails-app/views/index.gsp" ]
    
    // Prompt the user if any overwrites will occur before starting copy
    println "Checking for pre-existing files..."
    skinSources.each { sourceType, sourcePath ->
        sourceDir = new File(sourcePath)
        if (sourceDir?.exists()) {
            targetPath = baseSources[sourceType]
            targetDir = new File(targetPath)

            if (clearTargets?.contains(sourceType)) {
                // Prompt the user if the existing dir will be blown away.
                if (targetDir?.exists()) {
                    println "Warning: Directory ${targetPath} already exists and would be completely erased."
                    
                    Ant.input(addProperty: "skin.dir.${sourceType}.delete",
                        message: "Delete existing ${sourceType}? ", validArgs: "y,n")
                }
            } else if (targetDir?.exists()) {
                // Prompt the user if an overwrite will occur
                final def sources = sourceDir?.list()?.toList()
                final def targets = targetDir?.list()?.toList()
                final def overlap = sources?.intersect(targets)

                if (!overlap?.empty) {
                    println "Warning: Pre-existing files found in ${targetDir}"
                    
                    def hasNewer = false
                    def hasOlder = false
                    def hasDir = false
                    overlap.each { f ->
                        def overlapTarget = new File(targetDir, f)
                        def isDir = overlapTarget.directory
                        print "Warning: ${isDir ? 'Directory' : 'File' } ${f} exists already"

                        if (overlapTarget.lastModified() <= new File(sourceDir, f).lastModified()) {
                            println " and would be overwritten."
                            hasOlder = true
                        } else {
                            println " and is newer than source."
                            hasNewer = true
                        }

                        hasDir |= isDir
                    }

                    if (hasOlder || hasDir) {
                        Ant.input(addProperty: "skin.dir.${sourceType}.copy",
                            message: "Copy on top of existing ${sourceType}? ", validArgs: "y,n")
                    }

                    if (Ant.antProject.properties."skin.dir.${sourceType}.copy" != "n" && hasNewer) {
                        Ant.input(addProperty: "skin.dir.${sourceType}.overwrite",
                            message: "Overwrite files in existing ${sourceType} even if newer? ",
                            validArgs: "y,n")
                    }
                }
            }
        }
    }

    skinSourceFiles.each { sourceType, sourcePath ->
        sourceFile = new File(sourcePath)
        if (sourceFile?.exists()) {
            targetPath = baseSourceFiles[sourceType]
            targetFile = new File(targetPath)

            // Prompt the user if an overwrite will occur
            if (targetFile?.exists()) {
                print "Warning: File ${targetPath} exists already"
                
                if (targetFile.lastModified() <= sourceFile.lastModified()) {
                    println " and would be overwritten."
                    
                    Ant.input(addProperty: "skin.file.${sourceType}.copy",
                        message: "Overwrite existing ${sourceType}? ", validArgs: "y,n")
                } else {
                    println " and is newer than source file."
                    
                    Ant.input(addProperty: "skin.file.${sourceType}.overwrite",
                        message: "Overwrite existing ${sourceType} even if newer? ", validArgs: "y,n")
                }
            }
        }
    }

    def doCopy = false
    skinSources.each { sourceType, sourcePath ->
        sourceDir = new File(sourcePath)
        if (sourceDir.exists() && (Ant.antProject.properties."skin.dir.${sourceType}.copy" != "n")) {
            doCopy = true
        }
    }

    skinSourceFiles.each { sourceType, sourcePath ->
        sourceFile = new File(sourcePath)
        if (sourceFile.exists() && (Ant.antProject.properties."skin.file.${sourceType}.copy" != "n")) {
            doCopy = true
        }
    }

    if (doCopy) {
        skinSources.each { sourceType, sourcePath ->
            sourceDir = new File(sourcePath)
            if (sourceDir.exists() && (Ant.antProject.properties."skin.dir.${sourceType}.copy" != "n")) {

                targetPath = baseSources[sourceType]
                if (Ant.antProject.properties."skin.dir.${sourceType}.delete" == "y") {
                    println "Removing existing ${sourceType}..."
                    Ant.delete(dir: targetPath)
                }

                def overwrite = (Ant.antProject.properties."skin.dir.${sourceType}.overwrite" != "n")
                if (overwrite) {
                    println "Installing skin '${skin}' ${sourceType}..."
                } else {
                    println "Installing only newer files in skin '${skin}' ${sourceType}..."
                }

                // Create any directories that don't exist
                if (!new File(targetPath)?.exists()) {
                        Ant.mkdir(dir: targetPath)
                }

                // Copy files
                Ant.copy(todir: targetPath, overwrite: overwrite, preservelastmodified: true) {
                        fileset(dir: sourcePath, includes: "**")
                }
            }
        }

        skinSourceFiles.each { sourceType, sourcePath ->
            sourceFile = new File(sourcePath)
            if (sourceFile?.exists() &&
                    (Ant.antProject.properties."skin.file.${sourceType}.copy" != "n")) {
                overwrite = false
                targetPath = baseSourceFiles[sourceType]
                
                if (Ant.antProject.properties."skin.file.${sourceType}.overwrite" != "n")
                    overwrite = true
                    
                if (overwrite) {
                    println "Installing skin '${skin}' ${sourceType}..."
                } else {
                    println "Installing skin '${skin}' ${sourceType} only if newer..."
                }

                // Copy file
                Ant.copy(file: sourcePath, tofile: targetPath, overwrite: overwrite,
                    preservelastmodified: true)
            }
        }

        println "Skin Loader skin '${skin}' installed successfully"
    } else {
        println "No files installed"
    }
}
