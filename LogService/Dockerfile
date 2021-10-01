FROM openjdk:8

WORKDIR /usrapp/bin

ENV PORT 6000

COPY /target/classes /usrapp/bin/classes
COPY /target/dependency /usrapp/bin/dependency

CMD ["java","-DCOLLECTION_NAME=logs","-DDATABASE_URI=db","-DDATABASE_NAME=app","-DDATABASE_PORT=27017","-cp","./classes:./dependency/*","edu.escuelaing.web.LogServiceApp"]