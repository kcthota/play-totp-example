package actions;

import play.libs.F.Function0;
import play.libs.F.Promise;
import play.mvc.Http.Context;
import play.mvc.Result;

public class LoginAction extends play.mvc.Action.Simple  {

	protected Promise<Result> getUnauthorizedResult() {
		Promise<Result> unauthorizedPromise = Promise.promise(new Function0<Result>() {

			@Override
			public Result apply() throws Throwable {
				return unauthorized("Unauthorized");
			}
			
		});
		return unauthorizedPromise;
	}
	
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
