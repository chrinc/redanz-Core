## **Demo Application Angular**

Kotlin Project MAS 2021 Mobile Applications / ZHAW: <br>
[https://github.zhaw.ch/ineicch1/skulls]()

## RedanzCore
#### **Bootstrap**
Necessary tools for testing and development
- Android Studio (IDE - Powered by IntelliJ)

##### What Todo
- https://start.spring.io/
![start_spring_io](reference/start_spring_io.png)

- Setup Domain Classes in Project (e.g. registration / )
- Setup MySQL Configuration (in resources/application.properties)
- setup [cores](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS) 
- Maildev 
  - install ``npm install -g maildev``
  - run ``maildev`` ``or cd /usr/local/bin => ./maildev``
- Connect to mysql: 
  - ``export PATH=":/usr/local/mysql/bin"``
  - ``mysql -u root -p incIsRoot``
  - ``use redanz``
 
 #### on Server
 
- JAVA_HOME: ``export JAVA_HOME = /usr/lib/jvm/java-11-openjdk-amd64``
- SNAPSHOT: ``export SNAPSHOT=[currentSnapshot]``
- Find Folder: ``cd /usr/local/redanz/redanzCore``
- Start Spring on Server:
``java -jar $SNAPSHOT --email.host.username=[EMAIL-HOST] --email.host.password=[PW] --email.receiver.dev=[EMAIL-DEV]``

#### **Insert Image**
Image:
[https://skulls-b072a-default-rtdb.firebaseio.com//](https://skulls-b072a-default-rtdb.firebaseio.com/)

![snr_db_model](app/documentation/skulls_db_model.png)

