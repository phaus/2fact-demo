package controllers;

import models.User;
import play.Logger;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;

import static play.data.Form.form;

import javax.inject.Inject;

/**
 * Created by philipp on 02.09.14.
 */
public class Users extends Controller {

	private final Form<User> userForm;
	
	@Inject
	public Users(FormFactory formFactory) {
		this.userForm = formFactory.form(User.class);
	}

	public Result index() {
		return ok("index");
	}

	public Result save() {
		Logger.info("Users.save()");
		Form<User> filledUserForm = userForm.bindFromRequest();
		if (filledUserForm.hasErrors()) {
			Logger.info("register form has errors");
			return redirect(routes.Application.index());
		}
		User fUser = filledUserForm.get();
		if (User.existsWithUsername(fUser.username)) {
			return status(409, "User already exists!");
		}
		User user = User.create(fUser.username, fUser.password);
		user.updateSession();
		user.save();
		session().put("sessionId", user.sessionId);
		return redirect(routes.Application.index());
	}
}
