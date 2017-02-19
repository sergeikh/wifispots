Wifi spots SaaS application 

Application for management wifi spots in organisation or open site, and
project for my Docker, microservices tests.

- wifispots-core common for all sub projects:
    model
    repository
    specifications for querydsl
        
- wifispots-reverse-geocoder microservice for google directions
- wifispots-web web presentation:
   Spring MVC with Thymeleaf and HTMl5 template from http://html5up.net


Tech stack:
- mutipom maven project
- Spring Boot, MVC, Data
- Lombok
- multi profile for production and staging 
- unit and integrational tested