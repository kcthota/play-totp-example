package actions;

import play.libs.F.Promise;
import play.mvc.Http.Context;
import play.mvc.Result;
/**
 * This action is used for validating user is logged in. Actions which should be accessible post login and before TOTP authentication use this.
 * @author chait
 *
 */
public class LoginAction extends play.mvc.Action.Simple  {

	protected Promise<Result> getUnauthorizedResult() {
		Promise<Result> unauthorizedPromise = Promise.promise(() -> unauthorized("Unauthorized"));
		return unauthorizedPromise;
	}
	
	/**
	 * checks if the user is logged in.
	 * @param ctx
	 */
	public void doCheck(Context ctx) {
		String userId = ctx.session().get("userId");
		
		if(userId==null) {
			throw new RuntimeException("Unauthorized");
			
		}
	}
	
	
	@Override
	public Promise<Result> call(Context ctx) throws Throwable {
		try {
			doCheck(ctx);
		} catch(Exception e) {
			return getUnauthorizedResult();
		}
		
		return delegate.call(ctx);
	}

}
