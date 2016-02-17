package controllers;

import models.User;

import static play.data.Form.*;

import play.Logger;
import play.data.Form;
import play.mvc.*;

import views.html.*;

/**
 * Created by philipp on 31.08.14.
 */

public class Application extends Controller {
    final static Form<User> USER_FORM = form(User.class);

    public static Result index() {
        String sessionId = session().get("sessionId");
        if (sessionId == null) {
            Logger.info("not logged in!");
            return ok(login.render("Please Login!", USER_FORM));            
        }
        User user = User.FINDER.where().eq("sessionId", sessionId).findUnique();
        Logger.info("sessionId: " + sessionId);
        if (user == null) {
            Logger.info("not logged in!");
            return ok(login.render("Please Login!", USER_FORM));
        } else {
            Logger.info("User logged in:\n" + user.toString());
        }
        return ok(index.render("Welcome to 2Fact Auth Examples!", user, USER_FORM));
    }

    public static Result auth() {
        Form<User> userForm = USER_FORM.bindFromRequest();
        if (userForm.hasErrors()) {
            return redirect(controllers.routes.Application.index());
        }
        User fUser = userForm.get();
        final User user = User.getOrCreate(fUser.username);
        if (user == null) {
            return notFound();
        }
        if (user.verify(fUser.password)) {
            user.updateSession();
            session().put("sessionId", user.sessionId);
            user.save();
        } else {
            Logger.info("password invalid!");
        }
        return redirect(controllers.routes.Application.index());
    }

    public static Result logout() {
        String sessionId = session().get("sessionId");
        User user = User.FINDER.where().eq("sessionId", sessionId).findUnique();
        if (sessionId != null && user != null) {
            user.clearSession();
            user.save();
        }
        return redirect(controllers.routes.Application.index());
    }

}
