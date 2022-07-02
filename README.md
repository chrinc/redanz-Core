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
  - `init sql inserts with source (pwd shows path to folder) eg: ` source /Users/Chrine/Documents/GitHub/redAnz-Core/redanzCore/src/main/resources/init_sql_countries.sql
 
 #### on Server
 - Create SSH key for git-repository
     - ``ssh-keygen -t ed25519 -C [email@address]``
     - ``touch ~/ssh/config``
         - ``AddKeysToAgend yes`` 
         - `ÌdentityFile ~/.ssh/id_ed25519`
     - ``cat ~.ssh/id_ed25519``
     - Copy the Key to SSH Keys on github.com
 - Clone the repository to the server: 
     `git fetch origin`
     `git status`
     `git pull`
 - JAVA_HOME: ``export JAVA_HOME = /usr/lib/jvm/java-11-openjdk-amd64``
 - SNAPSHOT: ``export SNAPSHOT=[currentSnapshot]``
 - Find Folder: ``cd /usr/local/redanz/redanzCore``
 - screen: 
   - `Ctrl + A and then Ctrl + D` to leave the session
   - `screen -r`
 - Start Spring on Server:
-- clean inst all:
`mvn clean install`
--spring.profiles.active=prod --redanz.master.password=
``java -jar $snapshot --spring.profiles.active=prod --email.host.password=$pass``
`snapshot=[snapshot], pass=[password] => escape with \ before special characters`

 - encrypt Data at: [Devglan.com](https://www.devglan.com/online-tools/jasypt-online-encryption-decryption/)
#### **Insert Image**
Image:
[https://skulls-b072a-default-rtdb.firebaseio.com//](https://skulls-b072a-default-rtdb.firebaseio.com/)

![snr_db_model](app/documentation/skulls_db_model.png)

