package com.thirdstart.grails.kickstart.security

import grails.gorm.DetachedCriteria
import groovy.transform.ToString

import org.codehaus.groovy.util.HashCodeHelper
import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
@ToString(cache=true, includeNames=true, includePackage=false)
class ApplicationUserApplicationRole implements Serializable {

	private static final long serialVersionUID = 1

	ApplicationUser applicationUser
	ApplicationRole applicationRole

	@Override
	boolean equals(other) {
		if (other instanceof ApplicationUserApplicationRole) {
			other.applicationUserId == applicationUser?.id && other.applicationRoleId == applicationRole?.id
		}
	}

    @Override
	int hashCode() {
	    int hashCode = HashCodeHelper.initHash()
        if (applicationUser) {
            hashCode = HashCodeHelper.updateHash(hashCode, applicationUser.id)
		}
		if (applicationRole) {
		    hashCode = HashCodeHelper.updateHash(hashCode, applicationRole.id)
		}
		hashCode
	}

	static ApplicationUserApplicationRole get(UUID applicationUserId, UUID applicationRoleId) {
		criteriaFor(applicationUserId, applicationRoleId).get()
	}

	static boolean exists(UUID applicationUserId, UUID applicationRoleId) {
		criteriaFor(applicationUserId, applicationRoleId).count()
	}

	private static DetachedCriteria criteriaFor(UUID applicationUserId, UUID applicationRoleId) {
		ApplicationUserApplicationRole.where {
			applicationUser == ApplicationUser.load(applicationUserId) &&
			applicationRole == ApplicationRole.load(applicationRoleId)
		}
	}

	static ApplicationUserApplicationRole create(ApplicationUser applicationUser, ApplicationRole applicationRole, boolean flush = false) {
		def instance = new ApplicationUserApplicationRole(applicationUser: applicationUser, applicationRole: applicationRole)
		instance.save(flush: flush)
		instance
	}

	static boolean remove(ApplicationUser u, ApplicationRole r) {
		if (u != null && r != null) {
			ApplicationUserApplicationRole.where { applicationUser == u && applicationRole == r }.deleteAll()
		}
	}

	static int removeAll(ApplicationUser u) {
		u == null ? 0 : ApplicationUserApplicationRole.where { applicationUser == u }.deleteAll() as int
	}

	static int removeAll(ApplicationRole r) {
		r == null ? 0 : ApplicationUserApplicationRole.where { applicationRole == r }.deleteAll() as int
	}

	static constraints = {
		applicationRole validator: { ApplicationRole r, ApplicationUserApplicationRole ur ->
			if (ur.applicationUser?.id) {
				ApplicationUserApplicationRole.withNewSession {
					if (ApplicationUserApplicationRole.exists(ur.applicationUser.id, r.id)) {
						return ['userRole.exists']
					}
				}
			}
		}
	}

	static mapping = {
		id composite: ['applicationUser', 'applicationRole']
		version false
	}
}
