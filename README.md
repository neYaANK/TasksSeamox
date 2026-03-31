# User Management App
App that provides the ability to CRUD Users and their addresses;
Users be registered or they can log in the system, although the account must be verified after the registration via email (using SQS);
User account can be locked if the number of unsuccessful attempts is too big;
they also can have profile images that are downscaled and stored at the S3 bucket; 
Test coverage via black-box testing.
## How It's Made:

**Tech used:** Java, Spring Boot, Hibernate, PostgreSQL, SQS, S3, JUnit, Testcontainers, Docker, Docker-Compose, Thumblainator 

Here I tried to follow TDD during development. The API offers registration of the new Users and logging in as the existing ones. After registering a new user, the user is not verified and the verification email is sent.
Until User is not verified, he has reduced permissions that are restored back to normal after verification. Email is sent with the use of SQS to improve overall asynchronous actions handling.
If during the logging in phase password is incorrect too many times, then User is locked for specified number of hours/days.
User can upload his profile picture. When it is uploaded, the image is downscaled with the help of Thumbnailator library and then uploaded to the S3 bucket.
All persistence actions are annotated with @Transaction(readonly=true/false) respectively.
The whole application and it's dependencies is set up with the help of Docker Compose.
Testing is performed with the help of JUnit and Testcontainers for some dependencies like an in-memory DB.
All tests are conducted through all of the application by mocking sent request to a specific endpoint.


## Optimizations

Nginx is used to load-balance the requests;
All the tests are optimized to use the lowest number of possible Spring contextes which lowers test suite execution time drastically.
S3 is used to out-source storing the image to AWS, thus lowering the requirments for the local storage.
Hexagonal architecture is applied, so the explicit implementation of different layers can be changed without any hassle.

## Lessons Learned:

Packaging by layer and by feature should be combined: this time I tried sticking only to package-by-feature and it looked like a mess for me. I guess, that combining both methods should imrpove the situation.
Testing at different levels provides more value as it can discover the erroneous state of the system earlier and reduces test suite execution time. Also, by having different levels of testing implemented, we can execute different tests at different development stages, thus not needing to execute integration and system tests after introducing a small change.
Although commenting only the non-obvious stuff makes more sense than making Javadocs for everything, still for any project with more than 1 collaborator Javadocs would offer better insight of the project.
TDD is worth it
Locking the user out of account for limited time provides the possibility of DOS attack targeted on the specified user, so instead of locking we should use proper 2FA or email in this case
