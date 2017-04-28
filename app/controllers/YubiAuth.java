package controllers;

import models.User;
import play.Logger;
import play.data.Form;
import play.data.FormFactory;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;
import play.mvc.Controller;
import play.mvc.Result;
import tyrex.services.UUID;

import static play.data.Form.form;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;

/**
 * Created by philipp on 31.08.14.
 */
public class YubiAuth extends Controller {

	private final Form<User> userForm;
	private final WSClient ws;
	
	@Inject
	public YubiAuth(FormFactory formFactory, WSClient ws) {
		this.userForm = formFactory.form(User.class);
		this.ws = ws;
	}

	// this was setup here: https://upgrade.yubico.com/getapikey/
	private final static String REQUESTOR_ID = "18010";
	// sl = percentage of syncing
	// timeout = Number of seconds to wait for sync responses
	private final static String YUBI_API_URL_TEMPLATE = "http://api.yubico.com/wsapi/2.0/verify?otp=%s&nonce=%s&id=%s&timeout=8&sl=50";

	public CompletionStage<Result> auth() {
		Form<User> filledUserForm = userForm.bindFromRequest();
		if (filledUserForm.hasErrors()) {
			return CompletableFuture.supplyAsync(() -> "").thenApply(result -> redirect(routes.Application.index()));
		}
		User fUser = filledUserForm.get();
		final User user = User.getOrCreate(fUser.username);
		if (user == null) {
			return CompletableFuture.supplyAsync(() -> "").thenApply(result -> notFound());
		} else if (!user.verify(fUser.password)) {
			return CompletableFuture.supplyAsync(() -> "").thenApply(result -> forbidden("password is wrong!"));
		} else {
			final String callUrl = String.format(YUBI_API_URL_TEMPLATE, fUser.yubiKey, user.yubiKeyNonce, REQUESTOR_ID);
			Logger.info("calling: " + callUrl);
			WSRequest request = ws.url(callUrl);

			return request.get().thenApply(wsResponse -> {
				
				if (wsResponse == null && wsResponse.getBody() == null) {
					internalServerError();
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
			});
		}
	}

	public Result init() {
		String sessionId = session().get("sessionId");
		User user = User.Find.where().eq("sessionId", sessionId).findUnique();
		Form<User> filledUserForm = userForm.bindFromRequest();
		String yubiKey = filledUserForm.get() != null ? filledUserForm.get().yubiKey : null;
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
