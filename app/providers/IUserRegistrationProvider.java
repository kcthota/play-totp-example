package providers;

import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

import models.User;

public interface IUserRegistrationProvider {
	
	public User register(String username, String password);
	
	public User login(String username, String password);
	
	public GoogleAuthenticatorKey enableTOTP(String username);
	
	public void disableTOTP(String username);
	
	public boolean validateTOTP(String username, Integer token);
	
	public boolean loginWithScratchCodes(String username, Integer token);
	
	public void logout();

}
