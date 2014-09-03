package controllers;

import models.User;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

import static play.data.Form.form;

/**
 * Created by philipp on 02.09.14.
 */
public class Users extends Controller {
    final static Form<User> USER_FORM = form(User.class);

    public static Result index() {
        return ok("index");
    }

    public static Result save() {
        Logger.info("Users.save()");
        Form<User> userForm = USER_FORM.bindFromRequest();
        if (userForm.hasErrors()) {
            Logger.info("register form has errors");
            return redirect(controllers.routes.Application.index());
        }
        User fUser = userForm.get();
        if (User.existsWithUsername(fUser.username)) {
            return status(409, "User already exists!");
        }
        User user = User.create(fUser.username, fUser.password);
        user.updateSession();
        user.save();
        session().put("sessionId", user.sessionId);
        return redirect(controllers.routes.Application.index());
    }
}
