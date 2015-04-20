package actions;

import play.mvc.Http.Context;

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
