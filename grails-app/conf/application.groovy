// see https://github.com/grails/grails-core/releases/tag/v3.0.12
grails.resources.pattern = '/**'

// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.securityConfigType = "Annotation"
grails.plugin.springsecurity.userLookup.userDomainClassName = 'com.thirdstart.grails.kickstart.security.ApplicationUser'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'com.thirdstart.grails.kickstart.security.ApplicationUserApplicationRole'
grails.plugin.springsecurity.authority.className = 'com.thirdstart.grails.kickstart.security.ApplicationRole'
grails.plugin.springsecurity.controllerAnnotations.staticRules = [
	[pattern: '/',               access: ['permitAll']],
	[pattern: '/error',          access: ['permitAll']],
	[pattern: '/index',          access: ['permitAll']],
	[pattern: '/index.gsp',      access: ['permitAll']],
	[pattern: '/shutdown',       access: ['permitAll']],
	[pattern: '/assets/**',      access: ['permitAll']],
	[pattern: '/**/js/**',       access: ['permitAll']],
	[pattern: '/**/css/**',      access: ['permitAll']],
	[pattern: '/**/images/**',   access: ['permitAll']],
	[pattern: '/**/favicon.ico', access: ['permitAll']],
]

grails.plugin.springsecurity.filterChain.chainMap = [
	[pattern: '/assets/**',      filters: 'none'],
	[pattern: '/**/js/**',       filters: 'none'],
	[pattern: '/**/css/**',      filters: 'none'],
	[pattern: '/**/images/**',   filters: 'none'],
	[pattern: '/**/favicon.ico', filters: 'none'],
	[pattern: '/**',             filters: 'JOINED_FILTERS']
]

grails.gorm.default.mapping = {
	id generator : 'uuid2', type: 'pg-uuid'
}