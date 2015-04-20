# Play Framework TOTP Example
This is an example service implementation for enabling Time based One Time Password (TOTP) a.k.a two-factor authentication with Play Framework. The example uses [GoogleAuth] (https://github.com/wstrange/GoogleAuth) library for implementing TOTP.

#Sample HTTP calls

**Register User:** Registers the User.
Method: POST
Url: http://localhost:9000/api/Register
Payload:
``{
"email":"test@test.com",
"password":"test"
}``

**Login User:** Logs in the User. If TOTP is enabled for the user, /api/TOTPValidate must be called for Login to complete.
Method: POST
Url: http://localhost:9000/api/Login
Payload:
``{
"email":"test@test.com",
"password":"test"
}``

**Enable TOTP:** Returns the QRCode url. Replace &amp; with &.
Method: PUT
Url: http://localhost:9000/api/EnableTOTP

**Validate TOTP:** Validates the passed in TOTP code sent and completes the login.
Method: POST
Url: http://localhost:9000/api/TOTPValidate
Payload:
``{
"totp":854557
}``


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