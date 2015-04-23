package providers;

import java.security.MessageDigest;
import java.security.SecureRandom;

import models.BackupCodes;
import models.User;

import com.avaje.ebean.Ebean;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
/**
 * Provider for offer user registration and login
 * @author chait
 *
 */
public class UserRegistrationProvider implements IUserRegistrationProvider {
	
	@Override
	public User register(String username, String password) {
		String salt = generateSalt();
		
		User user=new User();
		user.setEmail(username);
		try {
			user.setPassword(getDigest(password, salt));
		} catch (Exception e) {
			throw new RuntimeException("Operation failed");
		}
		user.setSalt(salt);
		user.setTotpEnabled(false);
		Ebean.save(user);
		
		return user;
	}

	@Override
	public User login(String email, String password) {
		User user = Ebean.find(User.class).where().eq("email", email).findUnique();
		String hashedPassword=null;
		try {
			hashedPassword = getDigest(password, user.getSalt());
		} catch (Exception e) {
			throw new RuntimeException("Operation failed");
		}	
		if(user.getPassword().equals(hashedPassword)) {
			return user;
		} else {
			throw new RuntimeException("Unauthorized");
		}
	}
	
	
	@Override
	public GoogleAuthenticatorKey enableTOTP(String username) {
		GoogleAuthenticator gAuth = new GoogleAuthenticator();
		GoogleAuthenticatorKey gKey = gAuth.createCredentials(username);
		return gKey;
	}
	
	@Override
	public boolean validateTOTP(String username, Integer token) {
		GoogleAuthenticator gAuth = new GoogleAuthenticator();
		return gAuth.authorizeUser(username, token);
	}

	@Override
	public void logout() {
		
	}
	
	

	@Override
	public void disableTOTP(String username) {
		//ebean transaction
		Ebean.execute(() -> {
			User user = Ebean.find(User.class, username);
			user.setTotpEnabled(false);
			user.setTotpKey(null);
			user.save();
			
			BackupCodes backupCodes= Ebean.find(BackupCodes.class, username);
			if(backupCodes!=null) {
				backupCodes.delete();
			}
		});
		
	}

	@Override
	public boolean loginWithScratchCodes(String username, Integer token) {
		
		return false;
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
