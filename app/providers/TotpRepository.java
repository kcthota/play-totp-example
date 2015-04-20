package providers;

import java.util.List;

import models.User;

import com.avaje.ebean.Ebean;
import com.warrenstrange.googleauth.ICredentialRepository;

public class TotpRepository implements ICredentialRepository {

	@Override
	public String getSecretKey(String userId) {
		User user = Ebean.find(User.class, userId);
		if(!user.getTotpEnabled()) {
			throw new RuntimeException("TOTP not enabled");
		}
		return user.getTotpKey();
	}

	@Override
	public void saveUserCredentials(String userId, String totpKey, int validationCode, List<Integer> scratchCodes) {
		User user = Ebean.find(User.class, userId);
		user.setTotpEnabled(true);
		user.setTotpKey(totpKey);
		user.save();
	}

}
