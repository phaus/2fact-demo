package models;

import play.Logger;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Lob;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by philipp on 31.08.14.
 */
@Entity
public class User extends Model {

    public String username;
    public String googleSecretKey;

    public Integer googleValidationCode;

    @Lob
    public String googleScratchCodes;


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

    public static User getOrCreate(String username) {
        User user = User.FINDER.where().eq("username", username).findUnique();
        if (user == null) {
            user = new User();
            user.username = username;
            user.save();
        }
        return user;
    }
}
