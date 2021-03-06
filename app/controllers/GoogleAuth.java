package controllers;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import models.User;
import play.Logger;

import static play.data.Form.*;

import javax.inject.Inject;

import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Created by philipp on 31.08.14.
 */
public class GoogleAuth extends Controller {

	private final Form<User> userForm;

	@Inject
	public GoogleAuth(FormFactory formFactory) {
		this.userForm = formFactory.form(User.class);
	}

	public Result init() {
		String sessionId = session().get("sessionId");
		User user = User.Find.where().eq("sessionId", sessionId).findUnique();
		if (sessionId == null || user == null) {
			return ok(views.html.error.render("you need to be logged in!"));
		} else {
			GoogleAuthenticator gAuth = new GoogleAuthenticator();
			final GoogleAuthenticatorKey key = gAuth.createCredentials(user.username);
			Logger.info("key: " + key.getKey());
			// String target =
			// request().host()+controllers.routes.GoogleAuth.auth();
			String qrCode = GoogleAuthenticatorKey.getQRBarcodeURL(user.username, request().host(), key.getKey());
			qrCode = qrCode.replace("https://www.google.com/chart?", "https://chart.googleapis.com/chart?");
			Logger.info("qrCode: " + qrCode);
			Logger.info("setup done for " + user);
			return ok(views.html.GoogleAuth.init.render(qrCode, user));
		}
	}

	public Result auth() {
		Form<User> filledUserForm = userForm.bindFromRequest();
		if (filledUserForm.hasErrors()) {
			return redirect(routes.Application.index());
		}
		User fUser = filledUserForm.get();
		final User user = User.getOrCreate(fUser.username);
		if (user == null) {
			return notFound();
		} else if (!user.verify(fUser.password)) {
			return forbidden("password is wrong!");
		} else {
			Integer code = fUser.googleValidationCode;
			String secret = user.googleSecretKey;
			Logger.info("username: " + user.username + ", secret: " + secret);
			GoogleAuthenticator gAuth = new GoogleAuthenticator();
			boolean isCodeValid = gAuth.authorize(secret, code);
			if (isCodeValid) {
				return ok(views.html.message.render(user.toString(), user));
			} else {
				return ok(views.html.error.render("Code not valid!"));
			}
		}
	}
}
