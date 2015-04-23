package controllers;

import models.User;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import providers.UserRegistrationProvider;
import annotations.Authenticated;
import annotations.LoginRequired;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
/**
 * Service implementation which offers functionality via JSON
 * @author chait
 *
 */
public class LoginService extends Controller {
	
	/**
	 * User registration. Passed in email and password are stored to DB. TOTPEnabled is set to false
	 * @return
	 */
	public static Result register() {
		JsonNode payload = request().body().asJson();
		
		String email = payload.get("email").asText();
		String password = payload.get("password").asText();
		
		UserRegistrationProvider provider = new UserRegistrationProvider();
		try {
			User user = provider.register(email, password);
			return created(Json.toJson(user));
		} catch (Exception e) {
			//you can throw specific error message here. For simplicity, throwing 500
			return internalServerError("Operation failed. Please retry!");
		}
	}
	
	/**
	 * User login operation. If TOTP is enabled, this will set totpRequired session flag to yes, else totpRequired is set to no.
	 * userId and totpRequired=no in session is treated as fully authenticated user.
	 * @return
	 */
	public static Result login() {
		session().clear();
		JsonNode payload = request().body().asJson();
		
		String email = payload.get("email").asText();
		String password = payload.get("password").asText();
		
		UserRegistrationProvider provider = new UserRegistrationProvider();
		try {
			User user = provider.login(email, password);
			session("userId", user.getId().toString());
			if(user.getTotpEnabled()) {
				session("totpRequired", "yes");
			} else {
				session("totpRequired", "no");
			}
			return ok(Json.toJson(user));
		} catch (Exception e) {
			//you can throw specific error message here. For simplicity, throwing 500
			return internalServerError("Operation failed. Please retry!");
		}
	}
	
	/**s
	 * Enable TOTP operation for a fully logged in user.
	 * @Authenticated action takes care of securing this end point.
	 * @return
	 */
	@Authenticated
	public static Result enableTOTP() {
		String userId = session("userId");
		UserRegistrationProvider provider = new UserRegistrationProvider();
		GoogleAuthenticatorKey gKey = provider.enableTOTP(userId);
		String qrUrl = GoogleAuthenticatorQRGenerator.getOtpAuthURL("TOTP-example", userId, gKey);
		ObjectNode result = Json.newObject();
		result.put("qrURL", qrUrl);
		return ok(result);
	}
	
	/**
	 * Validates the user passed in TOTP token.
	 * Accessible for loggedIn user with before TOTP authentication
	 * @return
	 */
	@LoginRequired
	public static Result totpValidate() {
		JsonNode payload = request().body().asJson();
		
		Integer token = payload.get("totp").asInt();
		String userId = session("userId");
		
		UserRegistrationProvider provider = new UserRegistrationProvider();
		if(provider.validateTOTP(userId, token)) {
			//clear the totp flag
			session("totpRequired", "no");
			return noContent();
		}
		
		return unauthorized("TOTP validation failed!");
	}
	
	@Authenticated
	public static Result disableTOTP() {
		String userId = session("userId");
		UserRegistrationProvider provider = new UserRegistrationProvider();
		provider.disableTOTP(userId);
		return noContent();
	}
	
	/**
	 * Logs in user with scratch codes
	 * On success, disables the two-factor authentication for the user
	 * @return
	 */
	@LoginRequired
	public static Result loginWithScratchCodes() {
		JsonNode payload = request().body().asJson();
		
		Integer token = payload.get("totp").asInt();
		String userId = session("userId");
		
		UserRegistrationProvider provider = new UserRegistrationProvider();
		if(provider.loginWithScratchCodes(userId, token)) {
			//clear the totp flag
			session("totpRequired", "no");
			return noContent();
		}
		
		return unauthorized("TOTP validation failed!");
	}
	

}
