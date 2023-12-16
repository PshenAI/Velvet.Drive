# Velvet | Drive

This is a file storage service - diploma project for my courses. Login - admin. Password - admin. Or you can try oauth2 on the site itself - https://velvet-drive.herokuapp.com.
(May take a little while to boot!)

This project is dockerized, but to boot the project successfully via docker you need to follow a few simple steps.
1. Switch to a **`dockerized_branch`** branch in github.
2. Execute `mvn clean package -DskipTests` to build an executable jar file.
3. Finally, `run docker compose up`.

## P.S.
Currently, heroku and aws support are not working, as my free trials ended, 
so dockerized and local versions have all AWS S3 logic commented out. 

But still you can look up the code and click the site through.
As of now, I'm working on a different solution in my free time.