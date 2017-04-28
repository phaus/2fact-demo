package services;

import com.warrenstrange.googleauth.ICredentialRepository;
import models.User;

import java.util.List;

/**
 * Created by philipp on 31.08.14.
 */
public class GACredentialsRepository implements ICredentialRepository {
	@Override
	public String getSecretKey(String username) {
		User user = User.Find.where().eq("username", username).findUnique();
		if (user != null) {
			return user.googleSecretKey;
		}
		return "";
	}

	@Override
	public void saveUserCredentials(String username, String secretKey, int validationCode, List<Integer> scratchCodes) {
		User user = User.Find.where().eq("username", username).findUnique();
		if (user != null) {
			user.googleSecretKey = secretKey;
			user.googleValidationCode = validationCode;
			user.saveScratchCodes(scratchCodes);
			user.save();
		}

	}
}
