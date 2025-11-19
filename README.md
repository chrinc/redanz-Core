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
    - `stirit: ` source /usr/local/redanz/redanz-core/src/main/resources/init_sql_countries.sql
    - `model:` source /usr/local/redanz/redanzCore/redanz-Core/src/main/resources/init_sql_countries.sql
- Save mysql db:
  - install mysql: `https://www.digitalocean.com/community/tutorials/how-to-install-mysql-on-ubuntu-20-04`
  - export: ``mysqldump -u root -p stirit_redanz > /usr/local/redanz/data/prod/[YYMMDD]_redanz_backup.sql``
  - copy with sftp-client to kdrive
  - import: ``mysql -u root -p redanz_backup < /Users/inc/kDrive/030_associations_communities/020_lindyhop/003_Stirit/2211_stirit/2211_registration/data/220708_redanz_backup.sql``
  - import: ``mysql -u root -p stirit_redanz < /usr/local/redanz/data/prod/[YYMMDD]_redanz_backup.sql``
  - import: ``mysql -u root -p stirit_redanz < /Users/inc/Documents/redanz/stirit/data/220724_005_redanz_backup.sql``
  - optimize mysqldb on Server (file: sudo vim /etc/mysql/my.cnf)
  - `[mysqld]
     performance_schema = OFF
     symbolic-links = 0
     skip-external-locking
     key_buffer_size = 32k
     max_allowed_packet = 4M
     table_open_cache = 8
     sort_buffer_size = 128K
     read_buffer_size = 512K
     read_rnd_buffer_size = 512K
     net_buffer_length = 4K
     thread_stack = 480K
     innodb_file_per_table
     max_connections = 100
     max_user_connections = 50
     wait_timeout = 50
     interactive_timeout = 50
     long_query_time = 5`

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
- Scholarship: `select reg.registration_id, pers.first_name, pers.last_name, user.email, sreg.intro from registration
  reg inner join scholarship_registration sreg on sreg.registration_id = reg.registration_id 
  inner join person pers on pers.person_id = reg.participant_id inner join user on user.user_id = pers.user_id;`
- Donation: `select reg.registration_id, dreg.amount, pers.first_name, pers.last_name, user.email from registration
  reg inner join donation_registration dreg on dreg.registration_id = reg.registration_id
  inner join person pers on pers.person_id = reg.participant_id inner join user on user.user_id = pers.user_id;`
  
 #### on Server
 - Create SSH key for git-repository
     - ``ssh-keygen -t ed25519 -C [email@address]``
     - ``touch ~/.ssh``
     - `cat ~.ssh/id_ed25519`
     - Copy the Key to SSH Keys on github.com
     - Clone the repository to the server: 
         `git fetch origin`
         `git status`
         `git pull origin [branchName]`
 - JAVA_HOME: ``JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64``
 - SNAPSHOT: ``SNAPSHOT=[currentSnapshot]``
 - Find Folder: ``cd /usr/local/redanz/redanzCore``
 - screen: 
   - create new screen: `screen -dmS redanz.spring`
   - list all screens: `screen -list`
   - detach to existing screen and attach to it: `screen -d -r redanz.spring`
   - remove a screen: `screen -X -S redanz.spring quite`
   - detach `Ctrl + A Ctrl + D`
 - Start Spring on Server:
-- clean inst all: `mvn clean install`
--Set password and Snapshot: `snapshot=[snapshot], pass=[password] => escape with \ before special characters`
--RUN: ``java -jar $SNAPSHOT --spring.profiles.active=prod --redanz.master.password=$PASSWORD --server.port=8083``
 - encrypt Data at: [Devglan.com](https://www.devglan.com/online-tools/jasypt-online-encryption-decryption/)
 - Create a Cronjob for
   - sql-db backup
   - renew certbot certificates
   - List all jobs: `crontab -l`
   - Edit Cronjobs: `crontab -e` 
 - Renew Certificates
   - show all Certificats: `certbot certificates`
   - certbot certonly --force-renew -d model.redanz.ch
