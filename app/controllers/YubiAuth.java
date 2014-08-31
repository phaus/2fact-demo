package controllers;

import play.mvc.Controller;
import play.mvc.Result;

/**
 * Created by philipp on 31.08.14.
 */
public class YubiAuth extends Controller {
    public static Result init() {

        return ok("yubi!");
    }
}
