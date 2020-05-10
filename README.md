### JEE Hello World
- build and run the smallest and simplest possible JavaEE application in Thorntail, Wildfly, WebSphere or any JEE container.

#### Running in Thorntail
using maven plugin
```bash
mvn thorntail:run
```
- go to browser localhost:8080 and done

### debugging
```bash
mvn clean package
java -jar web/target/web-thorntail.jar
```
- setup a run configuration in your IDE which targets the jar and calls mvn clean package prior.
- run the configuration in debug mode
- this should get you break points and hot swapping
- for reference https://www.youtube.com/watch?v=LPvZ1P7ko9c

#### deploy an Enterprise Archive (EAR) or WAR to an app server such as Wildfly or WebSphere.
```
mvn clean install
mvn ear:ear
```
- the EAR is ready to deploy