
package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Column;
import javax.persistence.Transient;
import javax.persistence.Table;

import org.mindrot.jbcrypt.BCrypt;

import com.avaje.ebean.Model;

import play.Logger;
import play.libs.Json;
import java.util.UUID;

/**
 * Created by philipp on 31.08.14.
 */

@Entity
@Table(name = "users")
public class User extends Model {

 	@Id
	public String id;
	
	public String username;

	public String password;
	public String salt;

	public String sessionId;

	public String googleSecretKey;
	public Integer googleValidationCode;

	@Lob
	@Column(columnDefinition = "text")
	public String googleScratchCodes;

	public String yubiKeyNonce;
	public String yubiKeyIdentity;

	@Transient
	public String yubiKey;

	public static final Find<Long,User> Find = new Find<Long,User>(){};
	
	public User() {
		this.id = UUID.randomUUID().toString();
	}

	public List<Integer> loadScratchCodes() {
		List<Integer> scratchCodes = new ArrayList<Integer>();
		try {
			if (googleScratchCodes != null) {
				for (String code : googleScratchCodes.split("|")) {
					scratchCodes.add(Integer.parseInt(code));
				}
			}
		} catch (NumberFormatException ex) {
			Logger.error(ex.getLocalizedMessage(), ex);
		}

		return scratchCodes;
	}

	public void updateSession() {
		this.sessionId = UUID.randomUUID().toString();
	}

	public void clearSession() {
		this.sessionId = null;
	}

	public void hashPassword(String password) {
		this.password = getHash(password.trim());
	}

	public void saveScratchCodes(List<Integer> scratchCodes) {
		StringBuilder sb = new StringBuilder();
		if (scratchCodes != null) {
			for (int i = 0; i < scratchCodes.size(); i++) {
				sb.append(scratchCodes.get(i));
				if (i < scratchCodes.size() - 1) {
					sb.append("|");
				}
			}
		}
		Logger.debug("saving ScratchCodes " + sb.toString());
		googleScratchCodes = sb.toString();
	}

	public boolean verify(String challenge) {
		if(challenge == null) {
			return false;
		}
		return BCrypt.checkpw(challenge.trim(), this.password);
	}

	public String toString() {
		return Json.toJson(this).toString();
	}

	public static User create(String username, String password) {
		User user = new User();
		user.username = username.trim();
		user.hashPassword(password);
		user.save();
		Logger.info("created user: " + user.toString());
		return user;
	}

	public static User getOrCreate(String username) {
		User user = User.Find.where().eq("username", username.trim()).findUnique();
		if (user == null) {
			Logger.info("creating new user with name " + username);
			user = new User();
			user.username = username;
			user.save();
		}
		return user;
	}

	public static boolean existsWithUsername(String username) {
		return User.Find.where().eq("username", username).findUnique() != null;
	}

	private static String getHash(String password) {
		String hash = BCrypt.hashpw(password, BCrypt.gensalt());
		Logger.info("from: " + password + " => " + hash);
		return hash;
	}
}
