# Validate Social login token at back-end and get profile information

# Social Login (IOS, Google, Facebook, Linkedin)
- Create Web Project in developer console of social platform 
- Using developer console and get the access token then hit the API


- Facebook PlayGround 
  - https://developers.facebook.com/tools/explorer/


- Google PlayGround
  - https://developers.google.com/oauthplayground/
  - Scope : openid email profile
  - Exchange authorization code for token then you will get id_token
    

- Linkedin 
  - https://www.linkedin.com/developers
  - Get client id and secret 
  - Hit below url to get code and use this code in request body for token field
  - https://www.linkedin.com/oauth/v2/authorization?client_id=helloworld&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fhome&response_type=code&state=abcdef&scope=r_liteprofile%20r_emailaddress
  - No playground has been found for linkedin


- Swagger
  - http://localhost:8080/app/swagger-ui.html



- For Facebook 
  - Request body
    - {
        "provider": "facebook",
        "token": " Get access token from Playground "
      }



- For Google
  - Request body
    - {
        "provider": "google",
        "token": " Get id token from Playground "
      }



- For Linkedin
    - Request body
        - {
          "provider": "linkedin",
          "token": " Get authorization code"
          }
          


- For Apple : 
  - We need authorization code to validate on it.
  - We are passing the values of firstName and lastName because when we get authorization code that time user-info will be provided after that only email will be received. This is current behaviour in APPLE doc. 
  - Request body
    - {
        "firstName" : "Hello"
        "lastName" : "World"
        "provider" : "apple",
        "token" : "Get authorization code. It will be used only one time to validate and get the user information"
      }
      

- Important class and points
    - SocialClient.java 
        - google and facebook api to get user info    
        
    - IOSConfig.java
        - 4 properties we need from apple developer project
    
    - IOSClient.java
        - get apple user info