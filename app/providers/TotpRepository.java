package providers;

import java.util.List;
import java.util.UUID;

import models.BackupCodes;
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
		//ebean transaction
		Ebean.execute(() -> {
			User user = Ebean.find(User.class, userId);
			user.setTotpEnabled(true);
			user.setTotpKey(totpKey);
			user.save();
			
			BackupCodes backupCodes= Ebean.find(BackupCodes.class, userId);
			if(backupCodes==null) {
				backupCodes = new BackupCodes();
				backupCodes.setUserId(UUID.fromString(userId));
			}
			
			backupCodes.setCode1(scratchCodes.get(0));
			backupCodes.setCode2(scratchCodes.get(1));
			backupCodes.setCode3(scratchCodes.get(2));
			backupCodes.setCode4(scratchCodes.get(3));
			backupCodes.setCode5(scratchCodes.get(4));
			backupCodes.save();
		});
		
	}

}
