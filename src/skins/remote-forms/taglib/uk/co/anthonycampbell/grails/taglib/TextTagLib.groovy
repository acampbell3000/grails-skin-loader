package uk.co.anthonycampbell.grails.taglib

import org.codehaus.groovy.grails.commons.DomainClassArtefactHandler

import org.springframework.beans.SimpleTypeConverter
import org.springframework.validation.Errors
import org.springframework.web.servlet.support.RequestContextUtils as RCU

/**
 * Text Tag Library
 * 
 * This library generic text tags for common features used
 * throughout the website:
 * <ul>
 * <li>copyright</li>
 * <li>remoteText</li>
 * <li>remoteArea</li>
 * <li>remoteSelect</li>
 * <li>remotePaginate</li>
 * <li>remoteSortableColumn</li>
 * <li>displayFieldError</li>
 * </ul>
 */
class TextTagLib {
	// Declare namespace
	static namespace = "skin-loader"
	
	// Initialise lib property
	def typeConverter = new SimpleTypeConverter()

	/*
	 * Copyright Tag
	 * 
	 * Builds the copyright text. Automatically determines the user's locale
	 * and inserts the current year. Optional parameters of "code" allows
	 * the message key to be overridden.
	 * 
	 * Example: <g:copyright code="footer.copyright">
	 */
	def copyright = { attributes ->
	
		//Declare variables
		def output
 		String year = new GregorianCalendar().get(GregorianCalendar.YEAR)
 		
		//Copyright attribute
		if (attributes['code'] != null) {
			//Load message properties file
	 		def messageSource = grailsAttributes.
	 			getApplicationContext().
	 			getBean("messageSource")
			
			def args = [year, grailsApplication.getMetadata().get("app.name")]
			
            //Get message value
			def copyright = messageSource.getMessage(
                attributes['code'],
                args == null ? null : args.toArray(),
                null,
                RCU.getLocale(request)
			);
			
			//Store name
			if(copyright != null) {
				output = """${copyright}"""
			} else {
				output = """&copy; ${year}, All Rights Reserved"""
			}
		} else {
			//Use default
			output = """&copy; ${year}, All Rights Reserved"""
		}
		
		//Output
		out << output
	}

	/**
	 * Display a remote text field
	 *
	 * Based on the standard "remoteField" tag library. Supports additional attributes
	 * such as "readonly" and "password".
	 */
	def ajaxTextField = { attrs, body ->
		// Get attributes
		def paramName = attrs.paramName ? attrs.remove('paramName') : 'value'
		def value = attrs.remove('value')
		def readonly = attrs.remove('readonly')
		def password = attrs.remove('password')
		def type = 'text'

		// Validate attributes
		if(!value) value = ''
		if(!readonly) {
			readonly = ''
		} else {
			readonly = 'readonly=\"readonly\" '
		}
		if(password && password == "password") type = 'password'

		out << "<input type=\"${type}\" id=\"${attrs.remove('id')}\" name=\"${attrs.remove('name')}\" value=\"${value}\" ${readonly}onkeyup=\""

		if (attrs.params) {
			if (attrs.params instanceof Map) {
				attrs.params.put(paramName,
					new org.codehaus.groovy.grails.plugins.web.taglib.JavascriptValue('this.value'))
			} else {
				attrs.params += "+'${paramName}='+this.value"
			}
		} else {
			attrs.params = "'${paramName}='+this.value"
		}

		out << remoteFunction(attrs)
		
		attrs.remove('params')
		out << "\""

		attrs.remove('url')
		attrs.each {
			k,v-> out << " $k=\"$v\""
		}
		out <<" />"
	}
	
	
	def remoteField = { attrs, body ->
		def paramName = attrs.paramName ? attrs.remove('paramName') : 'value'
		def value = attrs.remove('value')
		if (!value) value = ''
		out << "<input type=\"text\" name=\"${attrs.remove('name')}\" value=\"${value}\" onkeyup=\""

		if (attrs.params) {
			if (attrs.params instanceof Map) {
				attrs.params[paramName] = new JavascriptValue('this.value')
			}
			else {
				attrs.params += "+'${paramName}='+this.value"
			}
		}
		else {
			attrs.params = "'${paramName}='+this.value"
		}
		out << remoteFunction(attrs)
		attrs.remove('params')
		out << "\""
		attrs.remove('url')
		attrs.each { k,v->
			out << " $k=\"$v\""
		}
		out <<" />"
	}

	/**
	 * Display a remote text field
	 *
	 * Based on the standard "remoteField" tag library. Supports additional attributes
	 * such as "readonly" and "password".
	 */
	def remoteArea = { attrs, body ->
		// Get Attributes
		def paramName = attrs.paramName ? attrs.remove('paramName') : 'value'
		def value = attrs.remove('value')
		def readonly = attrs.remove('readonly')
		def cols = attrs.remove('cols')
		def rows = attrs.remove('rows')

		// Validate attributes
		if(!value) value = ''
		if(!cols) {
			cols = ''
		} else {
			cols = 'cols=\"' + cols + '\" '
		}
		if(!rows) {
			rows = ''
		} else {
			rows = 'rows=\"' + rows + '\" '
		}
		if(!readonly) {
			readonly = ''
		} else {
			readonly = 'readonly=\"readonly\" '
		}

		out << "<textarea id=\"${attrs.remove('id')}\" name=\"${attrs.remove('name')}\" ${cols}${rows}${readonly}onkeyup=\""

		if (attrs.params) {
			if (attrs.params instanceof Map) {
				attrs.params.put(paramName,
					new org.codehaus.groovy.grails.plugins.web.taglib.JavascriptValue('this.value'))
			} else {
				attrs.params += "+'${paramName}='+this.value"
			}
		} else {
			attrs.params = "'${paramName}='+this.value"
		}

		out << remoteFunction(attrs)

		attrs.remove('params')
		out << "\""
		out <<" />${value}</textarea>"
	}

	/**
	 * Display a remote select field
	 *
	 * Based on the standard "remoteField" tag library.
	 *
	 * Examples:
     * <g:select name="user.age" from="${18..65}" value="${age}" />
     * <g:select name="user.company.id" from="${Company.list()}" value="${user?.company.id}" optionKey="id" />
	 */
	def remoteSelect = { attrs ->

		def messageSource = grailsAttributes.getApplicationContext().getBean("messageSource")
        def locale = RCU.getLocale(request)
        def writer = out

        attrs.id = attrs.id ? attrs.id : attrs.name
        def from = attrs.remove('from')
        def keys = attrs.remove('keys')
        def optionKey = attrs.remove('optionKey')
        def optionValue = attrs.remove('optionValue')
        def value = attrs.remove('value')
		def paramName = attrs.paramName ? attrs.remove('paramName') : 'value'

        if (value instanceof Collection && attrs.multiple == null) {
            attrs.multiple = 'multiple'
        }

        def valueMessagePrefix = attrs.remove('valueMessagePrefix')
        def noSelection = attrs.remove('noSelection')

        if (noSelection != null) {
            noSelection = noSelection.entrySet().iterator().next()
        }

        def disabled = attrs.remove('disabled')
        if (disabled && Boolean.valueOf(disabled)) {
            attrs.disabled = 'disabled'
        }

		writer << "<select id=\"${attrs.remove('id')}\" name=\"${attrs.remove('name')}\" onchange=\""

		if (attrs.params) {
			if (attrs.params instanceof Map) {
				attrs.params.put(paramName,
					new org.codehaus.groovy.grails.plugins.web.taglib.JavascriptValue('this.value'))
			} else {
				attrs.params += "+'${paramName}='+this.value"
			}
		} else {
			attrs.params = "'${paramName}='+this.value"
		}

		out << remoteFunction(attrs)

		attrs.remove('params')
		out << "\" "

		// Process remaining attributes
		outputAttributes(attrs)

		writer << '>'
		writer.println()

		if (noSelection) {
			renderNoSelectionOption(noSelection.key, noSelection.value, value)
			writer.println()
		}

		// Create options from list
		if (from) {
			from.eachWithIndex {el, i ->
				def keyValue = null
				writer << '\t\t\t<option '

				if (keys) {
					keyValue = keys[i]
					remoteWriteValueAndCheckIfSelected(keyValue, value, writer)
				} else if (optionKey) {
					if (optionKey instanceof Closure) {
						keyValue = optionKey(el)
					} else if (el != null && optionKey == 'id'
						&& grailsApplication.getArtefact(DomainClassArtefactHandler.TYPE,
							el.getClass().name)) {
						keyValue = el.ident()
					} else {
						keyValue = el[optionKey]
					}

					remoteWriteValueAndCheckIfSelected(keyValue, value, writer)
				} else {
					keyValue = el
					remoteWriteValueAndCheckIfSelected(keyValue, value, writer)
				}

				writer << '>'

				if (optionValue) {
					if (optionValue instanceof Closure) {
						writer << optionValue(el).toString().encodeAsHTML()
					} else {
						writer << el[optionValue].toString().encodeAsHTML()
					}
				} else if (valueMessagePrefix) {
					def message = messageSource.getMessage("${valueMessagePrefix}.${keyValue}", null,
						null, locale)

					if (message != null) {
						writer << message.encodeAsHTML()
					} else if (keyValue) {
						writer << keyValue.encodeAsHTML()
					} else {
						def s = el.toString()
						if (s) writer << s.encodeAsHTML()
					}
				} else {
					def s = el.toString()
					if (s) writer << s.encodeAsHTML()
				}

				writer << '</option>'
				writer.println()
			}
		}
		
		// Close tag
		writer << '\t\t\t</select>'
	}

	def renderNoSelectionOption = {noSelectionKey, noSelectionValue, value ->
		// If a label for the '--Please choose--' first item is supplied, write it out
		out << '\t\t\t<option value="' << (noSelectionKey == null ? "" : noSelectionKey) << '"'
		if (noSelectionKey.equals(value)) {
			out << ' selected="selected" '
		}

		out << '>' << noSelectionValue.encodeAsHTML() << '</option>'
	}

	private String optionValueToString(def el, def optionValue) {
		if (optionValue instanceof Closure) {
			return optionValue(el).toString().encodeAsHTML()
		}

		el[optionValue].toString().encodeAsHTML()
	}

    /**
     * Dump out attributes in HTML compliant fashion
     */
    private void outputAttributes(attrs) {
        attrs.remove( 'tagName') // Just in case one is left
        attrs.each { k, v ->
            out << k << "="" << v.encodeAsHTML() << "" "
        }
    }

	/**
	 * Display field Error
	 * 
	 * Outputs the error (if any) of the selected field from the selected
	 * bean. Error message is built from the user's selected locale.
	 * 
	 * Example: <g:displayFieldError bean="\${MessageInstance}" field="subject">
	 */
	def displayFieldError = { attributes, body ->
	
		// Declare variables
		def errors
        
		// Validate attributes
		if (attributes['bean'] == null) {
			throwTagError("Tag [fieldError] is missing required attribute [bean]")			
		} else if(attributes['field'] == null) {
			throwTagError("Tag [fieldError] is missing required attribute [field]")
		}

		// Get errors from selected bean
        if (attributes['bean'] instanceof Errors) {
            errors = attributes['bean']
        } else {
            def mc = GroovySystem.metaClassRegistry.getMetaClass(attributes['bean'].getClass())
            if (mc.hasProperty(attributes['bean'], 'errors')) {
                errors = attributes['bean'].errors
            }
        }
    	
		// Build output
        if (errors?.hasErrors() && errors.hasFieldErrors(attributes['field'])) {   	
			//Load message properties file
	 		def messageSource = grailsAttributes.
	 			getApplicationContext().
	 			getBean("messageSource")

            // Get error message value
			def errorMessage = messageSource.getMessage(
                errors.getFieldError(attributes['field']),
                RCU.getLocale(request)
			);
			
        	// Return to body
        	out << body(errorMessage)
        }
	}
	
	/**
	 * Add the value to the provided write and check whether the
	 * value should be selected.
	 *
	 * @param keyValue the key's value.
	 * @param value the value.
	 * @param writer the writer.
	 */
	private remoteWriteValueAndCheckIfSelected(keyValue, value, writer) {

		boolean selected = false
		def keyClass = keyValue?.getClass()
		if (keyClass.isInstance(value)) {
			selected = (keyValue == value)
		} else if (value instanceof Collection) {
			selected = value.contains(keyValue)
		} else if (keyClass && value) {
			try {
				value = typeConverter.convertIfNecessary(value, keyClass)
				selected = (keyValue == value)
			} catch (Exception) {
				// ignore
			}
		}

		writer << "value=\"${keyValue}\" "
		if (selected) {
			writer << 'selected="selected" '
		}
	}

	/**
	 * Display ajax based pagination
	 *
	 * Outputs an ajax based paginate tag.
	 */
    def remotePaginate = { attrs ->
		def writer = out

        if(attrs.total == null)
            throwTagError("Tag [remotePaginate] is missing required attribute [total]")

        def messageSource = grailsAttributes.messageSource
        def locale = RCU.getLocale(request)

        def total = attrs.int('total') ?: 0
        def action = (attrs.action ? attrs.action : (params.action ? params.action : "list"))
        def offset = params.int('offset') ?: 0
        def max = params.int('max')
        def maxsteps = (attrs.int('maxsteps') ?: 10)
        def update = (attrs.update ? attrs.update : "")

        if (!offset) offset = (attrs.int('offset') ?: 0)
        if (!max) max = (attrs.int('max') ?: 10)

        def linkParams = [:]
        if (attrs.params) linkParams.putAll(attrs.params)
        linkParams.offset = offset - max
        linkParams.max = max
        if (params.sort) linkParams.sort = params.sort
        if (params.order) linkParams.order = params.order

        def linkTagAttrs = [action:action, update:update, method:"get"]
        if (attrs.controller) {
            linkTagAttrs.controller = attrs.controller
        }
        if (attrs.id != null) {
            linkTagAttrs.id = attrs.id
        }
        linkTagAttrs.params = linkParams

        // Determine paging variables
        def steps = maxsteps > 0
        int currentstep = (offset / max) + 1
        int firststep = 1
        int laststep = Math.round(Math.ceil(total / max))

        // Display previous link when not on firststep
        if (currentstep > firststep) {
            linkTagAttrs.class = 'prevLink'
            linkParams.offset = offset - max

            writer << remoteLink(linkTagAttrs.clone()) {
                (attrs.prev ? attrs.prev : messageSource.getMessage('paginate.prev', null,
                        messageSource.getMessage('default.paginate.prev', null, 'Previous', locale), locale))
            }

            writer << ' '
        }

        // Display steps when steps are enabled and laststep is not firststep
        if (steps && laststep > firststep) {
            linkTagAttrs.class = 'step'

            // Determine begin and endstep paging variables
            int beginstep = currentstep - Math.round(maxsteps / 2) + (maxsteps % 2)
            int endstep = currentstep + Math.round(maxsteps / 2) - 1

            if (beginstep < firststep) {
                beginstep = firststep
                endstep = maxsteps
            }

            if (endstep > laststep) {
                beginstep = laststep - maxsteps + 1

                if(beginstep < firststep) {
                    beginstep = firststep
                }
                endstep = laststep
            }

            // Display firststep link when beginstep is not firststep
            if (beginstep > firststep) {
                linkParams.offset = 0

                writer << remoteLink(linkTagAttrs.clone()) {
                    firststep.toString()
                }

                writer << ' <span class="step">..</span> '
            }

            // Display paginate steps
           (beginstep..endstep).each { i ->
               if(currentstep == i) {
                   writer << "<span class=\"currentStep\">${i}</span> "
               } else {
                   linkParams.offset = (i - 1) * max
                   writer << remoteLink(linkTagAttrs.clone()) {
                       i.toString()
                   }

                   writer << ' '
               }
            }

            // Display laststep link when endstep is not laststep
            if(endstep < laststep) {
                writer << '<span class="step">..</span> '
                linkParams.offset = (laststep -1) * max
                writer << remoteLink(linkTagAttrs.clone()) {
                    laststep.toString()
                }

                writer << ' '
            }
        }

        // Display next link when not on laststep
        if(currentstep < laststep) {
            linkTagAttrs.class = 'nextLink'
            linkParams.offset = offset + max
            writer << remoteLink(linkTagAttrs.clone()) {
                (attrs.next ? attrs.next : messageSource.getMessage('paginate.next', null,
                        messageSource.getMessage('default.paginate.next', null, 'Next', locale), locale))
            }

            writer << ' '
        }
    }

	/**
	 * Display ajax based sortable columns
	 *
	 * Outputs an ajax based sortable column tag.
	 */
    def remoteSortableColumn = { attrs ->
		final def writer = out

		if (!attrs.property)
			throwTagError("Tag [sortableColumn] is missing required attribute [property]")
		if (!attrs.title && !attrs.titleKey)
			throwTagError("Tag [sortableColumn] is missing required attribute [title] or [titleKey]")

		final def property = attrs.remove("property")
		final String action = attrs.action ? attrs.remove("action") : (actionName ?: "list")
        final String update = attrs.update ? attrs.remove("update") : ""

		def defaultOrder = attrs.remove("defaultOrder")
		if (defaultOrder != "desc") defaultOrder = "asc"

		// Current sorting property and order
		def sort = params.sort
		def order = params.order

		// Add sorting property and params to link params
		def linkParams = [:]
		if (params.id) linkParams.put("id", params.id)
		if (attrs.params) linkParams.putAll(attrs.remove("params"))
		linkParams.sort = property

		// Determine and add sorting order for this column to link params
		attrs.class = (attrs.class ? "${attrs.class} sortable" : "sortable")
		if(property == sort) {
			attrs.class = attrs.class + " sorted " + order
			if(order == "asc") {
				linkParams.order = "desc"
			} else {
				linkParams.order = "asc"
			}
		} else {
			linkParams.order = defaultOrder
		}

        // Prepare link attributes
        def linkSortAttrs = [action: action, update: update, method: "get"]
        if (attrs.controller) {
            linkSortAttrs.controller = attrs.controller
        }
        if (attrs.id != null) {
            linkSortAttrs.id = attrs.id
        }
        linkSortAttrs.params = linkParams

		// Determine column title
		def title = attrs.remove("title")
		def titleKey = attrs.remove("titleKey")
		if(titleKey) {
			if(!title) title = titleKey
			def messageSource = grailsAttributes.messageSource
			def locale = RCU.getLocale(request)
			title = messageSource.getMessage(titleKey, null, title, locale)
		}

		writer << "<th "
		// process remaining attributes
		attrs.each { k, v ->
			writer << "${k}=\"${v.encodeAsHTML()}\" "
		}
		writer << ">${remoteLink(linkSortAttrs.clone()) { title }}</th>"
	}
}
