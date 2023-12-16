# Velvet | Drive

This is a file storage service - diploma project for my courses. Login - admin. Password - admin. 

~~You can try oauth2 on the site itself - https://velvet-drive.herokuapp.com.~~
**(Unavailable now!)**

This project is dockerized, you can launch containers with project and database by running this command from the root of the project:

`docker compose up`

You can then open the site by accessing it via http://localhost:8888/.

## P.S.
Currently, heroku and aws support are not working, as my free trials ended,
so for now project have all AWS S3 logic commented out.

But still you can look up the code and click the site through by launching it locally - by uncommenting the h2 database section.

Or you can run it via docker containers.

As of now, I'm working on a different solution for data storing in my free time.
