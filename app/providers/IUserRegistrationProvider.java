package providers;

import models.User;

public interface IUserRegistrationProvider {
	
	public User register(String username, String password);
	
	public User login(String username, String password);

}
