package controllers;

import helpers.AuthHelper;
import models.User;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import annotations.Authenticated;
import annotations.LoginRequired;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;

public class LoginService extends Controller {
	
	/**
	 * User registration. Passed in email and password are stored to DB. TOTPEnabled is set to false
	 * @return
	 */
	public static Result register() {
		JsonNode payload = request().body().asJson();
		
		String email = payload.get("email").asText();
		String password = payload.get("password").asText();
		
		AuthHelper authHelper = new AuthHelper();
		try {
			User user = authHelper.registerNewUser(email, password);
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
		
		AuthHelper authHelper = new AuthHelper();
		try {
			User user = authHelper.authenticate(email, password);
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
	
	/**
	 * Enable TOTP operation for a fully logged in user.
	 * @Authenticated action takes care of securing this end point.
	 * @return
	 */
	@Authenticated
	public static Result enableTOTP() {
		String userId = session("userId");
		GoogleAuthenticator gAuth = new GoogleAuthenticator();
		GoogleAuthenticatorKey gKey = gAuth.createCredentials(userId);
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
		
		Integer totp = payload.get("totp").asInt();
		String userId = session("userId");
		
		GoogleAuthenticator gAuth = new GoogleAuthenticator();
		if(gAuth.authorizeUser(userId, totp)) {
			//clear the totp flag
			session("totpRequired", "no");
			return noContent();
		}
		
		return unauthorized("TOTP validation failed!");
		
	}
	

}
