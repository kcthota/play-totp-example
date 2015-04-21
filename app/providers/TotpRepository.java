package providers;

import java.util.List;

import models.User;

import com.avaje.ebean.Ebean;
import com.warrenstrange.googleauth.ICredentialRepository;
/**
 * Two factor authentication repository. Loaded via Java ServiceLoader API
 * @author chait
 *
 */
public class TotpRepository implements ICredentialRepository {

	/**
	 * Gets the key for the passed in userId
	 */
	@Override
	public String getSecretKey(String userId) {
		User user = Ebean.find(User.class, userId);
		if(!user.getTotpEnabled()) {
			throw new RuntimeException("TOTP not enabled");
		}
		return user.getTotpKey();
	}

	/**
	 * Saves the TOTP key and enables totp for the user
	 */
	@Override
	public void saveUserCredentials(String userId, String totpKey, int validationCode, List<Integer> scratchCodes) {
		User user = Ebean.find(User.class, userId);
		user.setTotpEnabled(true);
		user.setTotpKey(totpKey);
		user.save();
	}

}
