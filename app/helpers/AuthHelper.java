package helpers;

import java.security.MessageDigest;
import java.security.SecureRandom;

import models.User;

import com.avaje.ebean.Ebean;
/**
 * Helper class for user registration and authentication. Saves to DB using ebean.
 * TODO Extract an interface and cleanup the implementation
 * @author chait
 *
 */
public class AuthHelper {
	
	/**
	 * User registration
	 * @param email
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public User registerNewUser(String email, String password) throws Exception {
		String salt = generateSalt();
		
		User user = new User();
		user.setEmail(email);
		user.setPassword(getDigest(password, salt));
		user.setSalt(salt);
		user.setTotpEnabled(false);
		Ebean.save(user);
		
		return user;
		
	}
	
	/**
	 * User authentication
	 * @param email
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public User authenticate(String email, String password) throws Exception {
		User user = Ebean.find(User.class).where().eq("email", email).findUnique();
		String hashedPassword = getDigest(password, user.getSalt());	
		if(user.getPassword().equals(hashedPassword)) {
			return user;
		} else {
			throw new RuntimeException("Unauthorized");
		}
	}
	
	/**
	 * Salts and converts the user entered password to digest
	 * @param password
	 * @param salt
	 * @return
	 * @throws Exception
	 */
	private String getDigest(String password, String salt) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		String saltedPassword=String.format("%s:%s", salt,password);
		md.update(saltedPassword.getBytes("UTF-8"));
		return new String(md.digest());
	}
	
	/**
	 * Generates a random salt via SecureRandom
	 * @return
	 */
	private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[20];
        random.nextBytes(bytes);
        return new String(bytes);
    }
}
