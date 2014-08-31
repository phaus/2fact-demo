package controllers;

import models.User;
import static play.data.Form.*;
import play.data.Form;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {
    final static Form<User> USER_FORM = form(User.class);

    public static Result index() {

        return ok(index.render("Welcome to 2Fact Auth Examples!", USER_FORM));
    }
}
