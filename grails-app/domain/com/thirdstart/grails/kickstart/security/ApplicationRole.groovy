package com.thirdstart.grails.kickstart.security

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
@EqualsAndHashCode(includes='authority')
@ToString(includes='authority', includeNames=true, includePackage=false)
class ApplicationRole implements Serializable {

	private static final long serialVersionUID = 1

	UUID id
	String authority

	static constraints = {
		authority blank: false, unique: true
	}

	static mapping = {
		cache true
	}
}
