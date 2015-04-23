package controllers;

import models.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import providers.UserRegistrationProvider;
import views.html.home;
import views.html.index;
import views.html.login;
import views.html.totp;
import views.html.backup;
import annotations.Authenticated;
import annotations.LoginRequired;

import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
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
    		provider.register(email, password);
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
    	UserRegistrationProvider provider = new UserRegistrationProvider();
		GoogleAuthenticatorKey gKey = provider.enableTOTP(userId);
		String qrUrl = GoogleAuthenticatorQRGenerator.getOtpAuthURL("TOTP-example", userId, gKey);
    	return ok(home.render(APP_NAME, qrUrl));
    }
    
    @LoginRequired
    public static Result totpAction() {
    	
    	DynamicForm requestData = Form.form().bindFromRequest();
    	String totpKey = requestData.get("totpKey");
    	Integer token = Integer.parseInt(totpKey);
    	String userId = session("userId");
		
    	UserRegistrationProvider provider = new UserRegistrationProvider();
		if(provider.validateTOTP(userId, token)) {
			//clear the totp flag
			session("totpRequired", "no");
			return redirect("/home");
		}
		
		return unauthorized("TOTP validation failed!");
    }
    
    @LoginRequired
    public static Result scratchCodeLoginAction() {
    	
    	DynamicForm requestData = Form.form().bindFromRequest();
    	String backupKey = requestData.get("backupKey");
    	Integer token = Integer.parseInt(backupKey);
    	String userId = session("userId");
		
    	UserRegistrationProvider provider = new UserRegistrationProvider();
		if(provider.loginWithScratchCodes(userId, token)) {
			//clear the totp flag
			session("totpRequired", "no");
			return redirect("/home");
		}
		
		return unauthorized("Login failed!");
    }
    
    @LoginRequired
    public static Result showTOTP() {
    	String page = request().getQueryString("page");
    	
    	if("backup".equals(page)) {
			return ok(backup.render(APP_NAME));
		} else {
			return ok(totp.render(APP_NAME));
		}
    }
    
    @Authenticated
    public static Result home() {
    	return ok(home.render(APP_NAME, null));
    }
    
}
