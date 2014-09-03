package controllers;

import models.User;
import play.Logger;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import tyrex.services.UUID;

import static play.data.Form.form;

/**
 * Created by philipp on 31.08.14.
 */
public class YubiAuth extends Controller {
    // this was setup here: https://upgrade.yubico.com/getapikey/
    private final static String REQUESTOR_ID = "18010";
    final static Form<User> USER_FORM = form(User.class);
    // sl = percentage of syncing
    // timeout = Number of seconds to wait for sync responses
    private final static String YUBI_API_URL_TEMPLATE = "http://api.yubico.com/wsapi/2.0/verify?otp=%s&nonce=%s&id=%s&timeout=8&sl=50";

    public static Promise<Result> auth() {
        Form<User> userForm = USER_FORM.bindFromRequest();
        if (userForm.hasErrors()) {
            return Promise.pure(redirect(controllers.routes.Application.index()));
        }
        User fUser = userForm.get();
        final User user = User.getOrCreate(fUser.username);
        if (user == null) {
            return Promise.pure((Result) notFound());
        } else if (!user.verify(fUser.password)) {
            return Promise.pure((Result) forbidden("password is wrong!"));
        } else {
            final String callUrl = String.format(YUBI_API_URL_TEMPLATE, fUser.yubiKey, user.yubiKeyNonce, REQUESTOR_ID);
            Logger.info("calling: " + callUrl);
            return WS.url(callUrl).get().map(new Function<WSResponse, Result>() {
                @Override
                public Result apply(WSResponse wsResponse) throws Throwable {
                    if (wsResponse == null && wsResponse.getBody() == null) {
                        return internalServerError();
                    }
                    Logger.info("auth response:\n " + wsResponse.getBody());
                    String[] parts = wsResponse.getBody().split("\n");
                    String line;
                    for (String part : parts) {
                        line = part.trim().toLowerCase();
                        if (line.startsWith("status")) {
                            if (line.substring(line.indexOf("=") + 1).trim().equals("ok")) {
                                user.updateSession();
                                session().put("sessionId", user.sessionId);
                                Logger.info("create session with: " + user.sessionId);
                                user.save();
                                return ok(views.html.message.render(user.toString(), user));
                            }
                        }
                    }
                    return ok(views.html.error.render("Code not valid!"));
                }
            });
        }
    }

    public static Result init() {
        String sessionId = session().get("sessionId");
        User user = User.FINDER.where().eq("sessionId", sessionId).findUnique();
        Form<User> userForm = USER_FORM.bindFromRequest();
        String yubiKey = userForm.get() != null ? userForm.get().yubiKey : null;
        if (yubiKey == null || sessionId == null || user == null || yubiKey.length() == 0) {
            return ok(views.html.error.render("you need to be logged in and set a yubiKey!"));
        } else {
            if (user == null) {
                return notFound();
            } else {
                user.yubiKeyIdentity = getYKidentifyer(yubiKey);
                user.yubiKeyNonce = UUID.create().toLowerCase().replace("-", "").substring(0, 20);
                user.save();
                Logger.info("setup done for " + user);
                return redirect(controllers.routes.Application.index());
            }
        }
    }

    private static String getYKidentifyer(String key) {
        return key.substring(0, 12);
    }
}
