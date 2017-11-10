package com.thirdstart.grails.kickstart.security

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
@EqualsAndHashCode(includes='username')
@ToString(includes='username', includeNames=true, includePackage=false)
class ApplicationUser implements Serializable {

	private static final long serialVersionUID = 1

	UUID id
	String username
	String password
	boolean enabled = true
	boolean accountExpired
	boolean accountLocked
	boolean passwordExpired

	Set<ApplicationRole> getAuthorities() {
		(ApplicationUserApplicationRole.findAllByApplicationUser(this) as List<ApplicationUserApplicationRole>)*.applicationRole as Set<ApplicationRole>
	}

	Boolean hasRole(ApplicationRole role) {
		return hasRole(role.authority)
	}

	Boolean hasRole(String roleName) {
		if ( !id ) {
			return false
		}

		return authorities.find { ApplicationRole role -> role.authority == roleName } != null
	}

	static constraints = {
		password blank: false, password: true
		username blank: false, unique: true
	}

	static mapping = {
		password column: 'password_hash'
	}
}
