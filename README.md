# Scrollify

# 1. Setup & Running the app

This app used JDBC PostgreSQL. The easiest way is to just use docker to set up and run the postgres instance.
To do so, launch the docker app, and then simply run the following command in your terminal:
```
docker-compose up -d
```
Ensure the docker app is running before running the command above. Once the command is ran, you should see an output like `[+] Running`, which indicates the Postgres instance is now active.

If you want to reset your postgres container for any purposes, just run the command below:
```
docker-compose down --volumes
```
Don't forget to run the first command again to restart a fresh new postgres container.

Then, build and run the app with the following command:
```
gradle build run
```

Subsequent runs can be ran using the command:
```
gradle run
```

<br/>
Scrollify is a virtual scoll access system (VSAS) for the realm of Edstemus!

*This project is part of submission for a uni assignment*
