package de.mobile.siteops

class SecUser {

    transient springSecurityService

	String username
	String password = generateRandomPassword()
	boolean enabled = true
	boolean accountExpired = false
	boolean accountLocked = false
	boolean passwordExpired = false

	static constraints = {
		username blank: false, unique: true
		//password blank: false
	}

	static mapping = {
		password column: '`password`'
	}

	Set<SecRole> getAuthorities() {
		SecUserSecRole.findAllBySecUser(this).collect { it.secRole } as Set
	}

    def beforeInsert() {
		encodePassword()
	}

	def beforeUpdate() {
		if (isDirty('password')) {
			encodePassword()
		}
	}

    private String generateRandomPassword() {
         def pool = ['a'..'z','A'..'Z',0..9,'_'].flatten()
         Random rand = new Random(System.currentTimeMillis())
         def passChars = (0..10).collect { pool[rand.nextInt(pool.size())] }
         return passChars.join()
    }

	protected void encodePassword() {
		password = springSecurityService.encodePassword(password)
	}
}
