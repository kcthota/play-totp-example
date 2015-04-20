package helpers;

import java.security.MessageDigest;
import java.security.SecureRandom;

import models.User;

import com.avaje.ebean.Ebean;

public class AuthHelper {
	
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
	
	public User authenticate(String email, String password) throws Exception {
		User user = Ebean.find(User.class).where().eq("email", email).findUnique();
		String hashedPassword = getDigest(password, user.getSalt());	
		if(user.getPassword().equals(hashedPassword)) {
			return user;
		} else {
			throw new RuntimeException("Unauthorized");
		}
	}
	
	private String getDigest(String password, String salt) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		String saltedPassword=String.format("%s:%s", salt,password);
		md.update(saltedPassword.getBytes("UTF-8"));
		return new String(md.digest());
	}
	
	private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[20];
        random.nextBytes(bytes);
        return new String(bytes);
    }
}
