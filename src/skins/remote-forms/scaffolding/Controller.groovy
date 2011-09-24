<%=packageName ? "package ${packageName}\n\n" : ''%>
<% final def lowerCaseName = grails.util.GrailsNameUtils.getPropertyName(className) %>
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

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.web.servlet.support.RequestContextUtils as RCU

/**
 * ${className} controller.
 *
 * Controller which handles all of the common actions for the ${className}
 * domain class. In addition, the class also provides support for ajax
 * requests.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class ${className}Controller {

	// Declare dependencies
	def messageSource
	
	// Delete, save and update actions only accept POST requests
	static allowedMethods =
		[save: 'POST', ajaxSave: 'POST', update: 'POST', ajaxUpdate: 'POST', delete: 'POST']
	
	/**
	 * Re-direct index requests to list view.
	 */
	def index() { redirect(action: "list", params: params) }
	
	/**
	 * Invoke non-ajax list method.
	 */
	def list() { doList(false) }
	
	/**
	 * Invoke non-ajax list method.
	 */
	def ajaxList() { doList(true) }
	
	/**
	 * Invoke non-ajax show method.
	 */
	def show() { doShow(false) }
	
	/**
	 * Invoke non-ajax show method.
	 */
	def ajaxShow() { doShow(true) }
	
	/**
	 * Invoke non-ajax save method.
	 */
	def save() { doSave(false) }
	
	/**
	 * Invoke ajax save method.
	 */
	def ajaxSave() { doSave(true) }
	
	/**
	 * Invoke non-ajax update method.
	 */
	def update() { doUpdate(false) }
	
	/**
	 * Invoke ajax update method.
	 */
	def ajaxUpdate() { doUpdate(true) }
	
	/**
	 * Validate an individual field
	 */
	def validate() {
		// Initialise domain instance and error message
		final def ${propertyName} = new ${className}(params)
		def errorMessage = ""
		def field = ""
		
		// Get selected field
		for (param in params) {
			if (param?.key && !param?.key?.equals("action")
				&& !param?.key?.equals("controller")) {
					field = param?.key
					break
			}
		}
		
		log.debug "Validating field: $field"
		
		// Check whether provided field has errors
		if (!${propertyName}?.validate() && ${propertyName}?.errors?.hasFieldErrors(field)) {
			// Get error message value
			errorMessage = messageSource?.getMessage(
				${propertyName}?.errors?.getFieldError(field), RCU.getLocale(request))
			
			log.debug "Error message: $errorMessage"
		}
		
		render(errorMessage)
	}
	
	/**
	 * Initialise form and render view.
	 */
	def create() {
		flash.${lowerCaseName} = ""
		
		[${propertyName}: new ${className}(params)]
	}
	
	/**
	 * Get selected instance and render edit view.
	 */
	def edit() {
		final def ${propertyName} = ${className}.get(params.id)
		
		flash.${lowerCaseName} = ""
		
		log.debug "Editing ${lowerCaseName} (params.id=" + params.id + ")"
		
		// Check whether ${lowerCaseName} exists
		if (!${propertyName}) {
			flash.${lowerCaseName} = message(code: 'default.not.found.message',
				args: [message(code: '${domainClass.propertyName}.label',
					default: '${className}'), params.id])
			redirect(action: "list")
		} else {
			return [${propertyName}: ${propertyName}]
		}
	}
	
	/**
	 * Get selected instance and attempt to perform a hard delete.
	 */
	def delete() {
		final def ${propertyName} = ${className}.get(params.id)
				
		log.debug "Attempting to delete ${lowerCaseName} (params.id=" + params.id + ")"
		
		// Check whether ${lowerCaseName} exists
		if (${propertyName}) {
			try {
				// Attempt delete
				${propertyName}.delete(flush: true)
				
				log.debug "Deleted ${lowerCaseName} (params.id=" + params.id + ")"
				
				flash.${lowerCaseName} = message(code: "default.deleted.message",
					args: [message(code: "${domainClass.propertyName}.label",
						default: "${className}"), params.id])
				redirect(action: "list")
				
			} catch (final DataIntegrityViolationException dive) {
				flash.${lowerCaseName} = message(code: "default.not.deleted.message",
					args: [message(code: "${domainClass.propertyName}.label",
						default: "${className}"), params.id])
				redirect(action: "show", id: params.id)
			}
		} else {
			flash.${lowerCaseName} = message(code: "default.not.found.message",
				args: [message(code: "${domainClass.propertyName}.label",
					default: "${className}"), params.id])
			redirect(action: "list")
		}
	}
	
	/**
	 * Retrieve and render list of ${lowerCaseName}s.
	 *
	 * @param isAjax whether the request is from an ajax call.
	 */
	private doList(final boolean isAjax) {
		final def listView = (isAjax ? "list" : "_list")
		
		flash.${lowerCaseName} = ""
		params.max = Math.min(params.int('max') ?: 10, 500)
		
		log.debug "Display ${lowerCaseName} list with " + listView +
			" view (isAjax=" + isAjax + ", params.max=" + params.max + ")"
		
		render(view: listView, model: [${propertyName}List: ${className}.list(params),
			${propertyName}Total: ${className}.count()])
	}
	
	/**
	 * Get the selected ${lowerCaseName} and render the view.
	 *
	 * @param isAjax whether the request is from an Ajax call.
	 */
	private doShow(final boolean isAjax) {
		final def showView = (isAjax ? "show" : "_show")
		final def ${propertyName} = ${className}.get(params.id)
		
		flash.${lowerCaseName} = ""
		
		// Check whether ${lowerCaseName} exists
		if (!${propertyName}) {
			flash.${lowerCaseName} = message(code: "default.not.found.message",
				args: [message(code: "${domainClass.propertyName}.label",
					default: "${className}"), params.id])
			redirect(action: "list")
		} else {
			log.debug "Display ${lowerCaseName} with " + showView +
				" view (isAjax=" + isAjax + ", params.id=" + params.id + ")"
			
			render(view: showView, model: [${propertyName}: ${propertyName}])
		}
	}
	
	/**
	 * Attempt to update the provided ${lowerCaseName} instance.
	 * In addition, render the correct view depending on whether the
	 * call is Ajax or not.
	 *
	 * @param isAjax whether the request is from an Ajax call.
	 */
	private doUpdate(final boolean isAjax) {
		final def editView = (isAjax ? "edit" : "_edit")
		final def showView = (isAjax ? "show" : "_show")
		final def ${propertyName} = ${className}.get(params.id)
		
		log.debug "Attempting to update an instance of ${className} (isAjax=" + isAjax +
			", params.id=" + params.id + ")"
		
		// Check whether ${lowerCaseName} exists
		if (${propertyName}) {
			// Check version has not changed
			if (params.version) {
				final def version = params.version.toLong()
				
				if (${propertyName}.version > version) {
					${propertyName}.errors.rejectValue("version", "default.optimistic.locking.failure",
						[message(code: "${domainClass.propertyName}.label",
							default: "${className}")] as Object[],
								"Another user has updated this ${className} while you were editing")
					render(view: editView, model: [${propertyName}: ${propertyName}])
					return
				}
			}
			
			// Get updated properties
			${propertyName}.properties = params
			
			// Perform update
			if (!${propertyName}.hasErrors() && ${propertyName}.save(flush: true)) {
				flash.${lowerCaseName} = message(code: "default.updated.message",
					args: [message(code: "${domainClass.propertyName}.label",
						default: "${className}"), ${propertyName}.id])
				render(view: showView, model: [${propertyName}: ${propertyName}])
			} else {
				render(view: editView, model: [${propertyName}: ${propertyName}])
			}
		} else {
			flash.${lowerCaseName} = message(code: "default.not.found.message",
				args: [message(code: "${domainClass.propertyName}.label",
					default: "${className}"), params.id])
			redirect(action: "list")
		}
    }
    
    /**
	 * Attempt to save the provided ${lowerCaseName} instance.
	 * In addition, render the correct view depending on whether the
	 * call is Ajax or not.
	 *
	 * @param isAjax whether the request is from an Ajax call.
	 */
    private doSave(final boolean isAjax) {
		final def createView = (isAjax ? "create" : "_create")
		final def showView = (isAjax ? "show" : "_show")
        final def ${propertyName} = new ${className}(params)

		log.debug "Attempting to save an instance of ${className} (isAjax=" + isAjax + ")"

        if (${propertyName}.save(flush: true)) {
            flash.${lowerCaseName} = message(code: "default.created.message",
                args: [message(code: "${domainClass.propertyName}.label",
					default: "${className}"), ${propertyName}.id])
            render(view: showView, model: [${propertyName}: ${propertyName}])
        } else {
            render(view: createView, model: [${propertyName}: ${propertyName}])
        }
    }
}
