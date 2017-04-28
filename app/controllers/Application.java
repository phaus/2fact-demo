package controllers;

import models.User;

import static play.data.Form.*;

import javax.inject.Inject;

import play.Logger;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.*;

import views.html.*;

/**
 * Created by philipp on 31.08.14.
 */

public class Application extends Controller {
	
	private final Form<User> userForm;
	
	@Inject
	public Application(FormFactory formFactory) {
		this.userForm = formFactory.form(User.class);
	}	

    public Result index() {
        String sessionId = session().get("sessionId");
        if (sessionId == null) {
            Logger.info("not logged in!");
            return ok(login.render("Please Login!", userForm));            
        }
        User user = User.Find.where().eq("sessionId", sessionId).findUnique();
        Logger.info("sessionId: " + sessionId);
        if (user == null) {
            Logger.info("not logged in!");
            return ok(login.render("Please Login!", userForm));
        } else {
            Logger.info("User logged in:\n" + user.toString());
        }
        return ok(index.render("Welcome to 2Fact Auth Examples!", user, userForm));
    }

    public Result auth() {
        Form<User> newUserForm = userForm.bindFromRequest();
        if (newUserForm.hasErrors()) {
            return redirect(routes.Application.index());
        }
        User fUser = newUserForm.get();
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
        return redirect(routes.Application.index());
    }

    public Result logout() {
        String sessionId = session().get("sessionId");
        User user = User.Find.where().eq("sessionId", sessionId).findUnique();
        if (sessionId != null && user != null) {
            user.clearSession();
            user.save();
        }
        return redirect(routes.Application.index());
    }

}
