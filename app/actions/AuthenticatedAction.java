package actions;

import play.mvc.Http.Context;

/**
 * Verifies if the user is loggedin and TOTP auth is complete.
 * @author chait
 *
 */
public class AuthenticatedAction extends LoginAction  {

	@Override
	public void doCheck(Context ctx) {
		super.doCheck(ctx);
		
		String totpRequired = ctx.session().get("totpRequired");
		
		if(!totpRequired.equals("no")) {
			throw new RuntimeException("Unauthorized");
			
		}
	}

}
