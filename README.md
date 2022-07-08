## **Demo Application Angular**

Kotlin Project MAS 2021 Mobile Applications / ZHAW: <br>
[https://github.zhaw.ch/ineicch1/skulls]()

## RedanzCore
#### **Bootstrap**
Necessary tools for testing and development
- Android Studio (IDE - Powered by IntelliJ)

##### What Todo
- https://start.spring.io/

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
    - `on ubuntu: ` source /usr/local/redanz/redanzCore/redanz-Core/src/main/resources/init_sql_countries.sql
- Save mysql db:
  - export: ``mysqldump -u root -p redanz > /usr/local/redanz/redanzCore/data/[YYMMDD]_redanz_backup.sql``
  - copy with sftp-client to kdrive
  - import: ``mysql -u root -p redanz_backup < /Users/inc/kDrive/030_associations_communities/020_lindyhop/003_Stirit/2211_stirit/2211_registration/data/220708_redanz_backup.sql
    ``
  - check submissions: `
    select 
      reg.registration_id id
     ,first_name
     ,bu.name bundle
     ,t.name track
     ,dr.name dance_role
     ,wfs.name status
     ,rm.partner_email
     ,rm.registration_2_id reg2
    from registration reg 
      inner join person per on reg.participant_id = per.person_id 
      left join track t on t.track_id = reg.track_id 
      left join dance_role dr on dr.dance_role_id = reg.dance_role_id 
      inner join bundle bu on bu.bundle_id = reg.bundle_id 
      left join workflow_status wfs on wfs.workflow_status_id = reg.current_workflow_status_id
      left join registration_matching rm on rm.registration_1_id = reg.registration_id
    order by current_workflow_Status_id desc
    ;`
 #### on Server
 - Create SSH key for git-repository
     - ``ssh-keygen -t ed25519 -C [email@address]``
     - ``touch ~/ssh/config``
         - `AddKeysToAgend yes`
         - `ÌdentityFile ~/.ssh/id_ed25519`
     - `cat ~.ssh/id_ed25519`
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
