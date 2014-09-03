
package models;

import play.Logger;
import play.db.ebean.Model;
import play.libs.Crypto;
import play.libs.Json;
import tyrex.services.UUID;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Transient;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by philipp on 31.08.14.
 */

@Entity
public class User extends Model {

    public String username;

    public String password;
    public String salt;

    public String sessionId;

    public String googleSecretKey;
    public Integer googleValidationCode;

    @Lob
    public String googleScratchCodes;

    public String yubiKeyNonce;
    public String yubiKeyIdentity;

    @Transient
    public String yubiKey;

    public final static Finder<Long, User> FINDER = new Finder<Long, User>(Long.class, User.class);

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
        this.sessionId = UUID.create();
    }

    public void clearSession() {
        this.sessionId = null;
    }

    public void hashPassword(){
        salt = UUID.create().toString().replace("-", "").trim().substring(0, 10);
        password = getHash(password.trim(), salt);
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
        String challengeHash = getHash(challenge.trim(), this.salt);
        Logger.info("comparing:\n" + this.password + " with\n " + challengeHash);
        return challengeHash.equals(this.password);
    }

    public String toString() {
        return Json.toJson(this).toString();
    }

    public static User create(String username, String password){
        User user = new User();
        user.username = username.trim();
        user.password = password.trim();
        user.hashPassword();
        user.save();
        Logger.info("created user: " + user.toString());
        return user;
    }

    public static User getOrCreate(String username) {
        User user = User.FINDER.where().eq("username", username.trim()).findUnique();
        if (user == null) {
            Logger.info("creating new user with name "+username);
            user = new User();
            user.username = username;
            user.save();
        }
        return user;
    }

    public static boolean existsWithUsername(String username) {
        return User.FINDER.where().eq("username", username).findUnique() != null;
    }

    private static String getHash(String password, String salt) {
        String hash = Crypto.encryptAES(password);
        Logger.info("from: " + salt + " + " + password + " => " + hash);
        return hash;
    }

}
