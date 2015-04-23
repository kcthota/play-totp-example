# Play Framework TOTP Example
This is an example service implementation for enabling Time based One Time Password (TOTP) a.k.a two-factor authentication with Play Framework. The example uses [GoogleAuth] (https://github.com/wstrange/GoogleAuth) library for implementing TOTP.

There are two implementations in this project. 

1. Simple html form based implementation. Refer [Application.java] (https://github.com/kcthota/play-totp-example/blob/master/app/controllers/Application.java)
2. JSON based ReST end point. Refer [LoginService.java] (https://github.com/kcthota/play-totp-example/blob/master/app/controllers/LoginService.java)

#HTML forms implementation

See the demo at the following YouTube location. 

The video shows user registration, login, enabling two-factor authentication, generating the QR Code (user should scan this QR with Authy or Google Authenticator) and Logging back in with two-factor authentication code.

<a href="http://www.youtube.com/watch?feature=player_embedded&v=p516xPQztCw
" target="_blank"><img src="http://img.youtube.com/vi/p516xPQztCw/0.jpg" 
alt="Play TOTP Example" width="240" height="180" border="10" /></a>


#Sample RESTful calls

##Register User
Registers the User.  
Method: POST  
Url: http://localhost:9000/api/Register  
Payload:  
``  
{  
"email":"test@test.com",  
"password":"test"  
}  
``

##Login User
Logs in the User. If TOTP is enabled for the user, /api/TOTPValidate must be called for Login to complete.  
Method: POST  
Url: http://localhost:9000/api/Login  
Payload:  
``  
{  
"email":"test@test.com",  
"password":"test"  
}  
``

##Enable TOTP
Returns the QRCode url. Replace &amp;amp; with &.  
Method: PUT  
Url: http://localhost:9000/api/EnableTOTP  

##Validate TOTP
Validates the passed in TOTP code sent and completes the login.  
Method: POST  
Url: http://localhost:9000/api/TOTPValidate  
Payload:  
``  
{  
"totp":854557  
}  
``

##Disable TOTP
Disables two-factor authentication for the user  
Method: PUT  
Url: http://localhost:9000/api/DisableTOTP  

##Login with Backup Codes
Login with Backup codes. Also disables two-factor authentication post successful login.  
Method: POST  
Url: http://localhost:9000/api/LoginWithScratchCodes  
Payload:  
``  
{  
"totp":85455712  
}  
``

#Action Composition
The implementation has two action composition annotations defined.

**@LoginRequired** annotation verifies if the user is logged in with password and ignores the TOTP Authentication status.

**@Authentication** annotation validates both password based and TOTP authentication.


## License:

Copyright 2015 Krishna Chaitanya Thota (kcthota@gmail.com)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this work except in compliance with the License.
You may obtain a copy of the License in the LICENSE file, or at:

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.