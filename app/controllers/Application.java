package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;

import annotations.Authenticated;
import annotations.LoginRequired;
import models.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import providers.UserRegistrationProvider;
import views.html.*;
/**
 * Service implementation offering functionality with simple html forms
 * @author chait
 *
 */
public class Application extends Controller {
	final static String APP_NAME="TOTP Example";
    
	public static Result index() {
		String page = request().getQueryString("page");

		if(!"login".equals(page)) {
			return ok(index.render(APP_NAME));
		} else {
			return ok(login.render(APP_NAME));
		}
    }
    
	
    public static Result registerAction() {
    	DynamicForm requestData = Form.form().bindFromRequest();
    	String email = requestData.get("email");
    	String password = requestData.get("password");
    	Logger.debug("email:"+email);
    	UserRegistrationProvider provider = new UserRegistrationProvider();
    	try {
    		User user = provider.register(email, password);
    	} catch(Exception e) {
    		return internalServerError(e.getMessage());
    	}
    	
    	return redirect("/?page=login");
    }

    public static Result loginAction() {
    	DynamicForm requestData = Form.form().bindFromRequest();
    	String email = requestData.get("email");
    	String password = requestData.get("password");
    	UserRegistrationProvider provider = new UserRegistrationProvider();
    	try {
    		User user = provider.login(email, password);
    		session("userId", user.getId().toString());
			if(user.getTotpEnabled()) {
				session("totpRequired", "yes");
			} else {
				session("totpRequired", "no");
			}
			
    		if(user.getTotpEnabled()) {
    			return redirect("/totp");
    		}
    	} catch(Exception e) {
    		return internalServerError(e.getMessage());
    	}
    	return redirect("/home");
    }
    
    @Authenticated
    public static Result enableTOTP() {
    	String userId = session("userId");
		GoogleAuthenticator gAuth = new GoogleAuthenticator();
		GoogleAuthenticatorKey gKey = gAuth.createCredentials(userId);
		String qrUrl = GoogleAuthenticatorQRGenerator.getOtpAuthURL("TOTP-example", userId, gKey);
    	return ok(home.render(APP_NAME, qrUrl));
    }
    
    @LoginRequired
    public static Result totpAction() {
    	
    	DynamicForm requestData = Form.form().bindFromRequest();
    	String totpKey = requestData.get("totpKey");
    	Integer totp = Integer.parseInt(totpKey);
    	String userId = session("userId");
		
		GoogleAuthenticator gAuth = new GoogleAuthenticator();
		if(gAuth.authorizeUser(userId, totp)) {
			//clear the totp flag
			session("totpRequired", "no");
			return redirect("/home");
		}
		
		return unauthorized("TOTP validation failed!");
    }
    
    @LoginRequired
    public static Result showTOTP() {
		return ok(totp.render(APP_NAME));
    }
    
    @Authenticated
    public static Result home() {
    	return ok(home.render(APP_NAME, null));
    }
    
}
