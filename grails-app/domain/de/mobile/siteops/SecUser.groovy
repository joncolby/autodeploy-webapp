package de.mobile.siteops

class SecUser {

    transient springSecurityService

	String username
	String password
    String passwordConfirmation
	boolean enabled = true
	boolean accountExpired = false
	boolean accountLocked = false
	boolean passwordExpired = false

    static transients = [ 'passwordConfirmation' ]

	static constraints = {
		username blank: false, unique: true
        password(maxSize: 100, nullable: false, blank: false,validator: SecUser.validatePassword)
	}

	static mapping = {
		password column: '`password`'
	}

	Set<SecRole> getAuthorities() {
		SecUserSecRole.findAllBySecUser(this).collect { it.secRole } as Set
	}

    def beforeInsert() {
        //password = generateRandomPassword()
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

    private static validatePassword = { password, user ->
        if (!user.id) {
            password = password?.trim()
            if (!password) {
                password = ""
            }
            if (password.length() < 6 || password.length() > 100) {
                user.errors.rejectValue('password', 'user.password.length',
                    ['password', 'User', password] as Object[],
                    'Property [{0}] of class [{1}] must be between 6 and 40 characters')
            }
        }
        true
    }
}
