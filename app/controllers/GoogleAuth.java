package controllers;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import models.User;
import play.Logger;
import static play.data.Form.*;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Created by philipp on 31.08.14.
 */
public class GoogleAuth extends Controller {
    final static Form<User> USER_FORM = form(User.class);

    public static Result init() {
        String username = request().getQueryString("username");
        if (username == null && username.length() == 0) {

            return ok(views.html.error.render("you need to set a username!"));
        } else {
            User user = User.getOrCreate(username);
            GoogleAuthenticator gAuth = new GoogleAuthenticator();
            final GoogleAuthenticatorKey key = gAuth.createCredentials(username);
            Logger.info("key: " + key.getKey());
            //String target = request().host()+controllers.routes.GoogleAuth.auth();
            String qrCode = GoogleAuthenticatorKey.getQRBarcodeURL(username, request().host(), key.getKey());
            qrCode = qrCode.replace("https://www.google.com/chart?", "https://chart.googleapis.com/chart?");
            Logger.info("qrCode: " + qrCode);
            return ok(views.html.GoogleAuth.init.render(qrCode));
        }
    }

    public static Result auth() {
        Form<User> userForm = USER_FORM.bindFromRequest();
        if (userForm.hasErrors()) {
            return redirect(controllers.routes.Application.index());
        }
        User fUser = userForm.get();
        User user = User.getOrCreate(fUser.username);
        if (user == null) {
            return notFound();
        } else {
            Integer code = fUser.googleValidationCode;
            String secret = user.googleSecretKey;
            Logger.info("username: " + user.username + ", secret: " + secret);
            GoogleAuthenticator gAuth = new GoogleAuthenticator();
            boolean isCodeValid = gAuth.authorize(secret, code);
            if(isCodeValid) {
                return ok(views.html.message.render());
            } else {
                return forbidden("Code not valid!");
            }
        }
    }
}
