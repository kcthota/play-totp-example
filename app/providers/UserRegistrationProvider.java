package providers;

import models.User;

import com.avaje.ebean.Ebean;
/**
 * Provider for offer user registration and login
 * @author chait
 *
 */
public class UserRegistrationProvider extends BaseUserRegistrationProvider {

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
	
	
}
