package providers;

import java.security.MessageDigest;
import java.security.SecureRandom;

public abstract class BaseUserRegistrationProvider implements IUserRegistrationProvider {

	/**
	 * Salts and converts the user entered password to digest
	 * @param password
	 * @param salt
	 * @return
	 * @throws Exception
	 */
	protected String getDigest(String password, String salt) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		String saltedPassword=String.format("%s:%s", salt,password);
		md.update(saltedPassword.getBytes("UTF-8"));
		return new String(md.digest());
	}
	
	/**
	 * Generates a random salt via SecureRandom
	 * @return
	 */
	protected String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[20];
        random.nextBytes(bytes);
        return new String(bytes);
    }
	
}
