# BigSantander Provider

BigSantander is one of the selected [BIG-IoT](http://big-iot.eu/) OC2 proposals. This extension integrates several Mobility and Environment-related data sources as new offerings at the BIG-IoT Marketplace both from the [SmartSantander](http://www.smartsantander.eu/) and [Open Data Santander](http://datos.santander.es/) platforms.

## Compile

Just use maven.
```
$ mvn clean package
```

## Installation

### Create bash scripts to run the provider
```
$ sudo mkdir -p /opt/bigiot/provider/bin/
$ sudo cp smartsantander-bigiot-provider.jar /opt/bigiot/provider/bin/
$ sudo ln -s /opt/bigiot/provider/bin/smartsantander-bigiot-provider.jar /opt/bigiot/provider/bin/smartsantander-bigiot-provider.jar
$ sudo tee /opt/bigiot/provider/bin/smartsantander-bigiot-provider > /dev/null << 'EOL'
#!/bin/bash

if [ -z "$1" ]
	config=/opt/bigiot/provider/config/
else 
	config=$1
fi

cd /opt/bigiot/provider
sudo /usr/bin/java -Dlog4j.configurationFile=$config/log4j2.xml -jar bin/smartsantander-bigiot-provider.jar -c $config/smartsantander.properties
EOL
$ sudo chmod +x /opt/bigiot/provider/bin/smartsantander-bigiot-provider
$ sudo ln -s /opt/bigiot/provider/bin/smartsantander-bigiot-provider /usr/local/bin/smartsantander-bigiot-provider 
```

### Create the configuration files
Create properties file.
```
$ sudo mkdir -p /opt/bigiot/provider/config
$ sudo tee /opt/bigiot/provider/config/smartsantander.properties > /dev/null << 'EOL'
# BIG IoT Marketplace
marketplaceUri  = https://market.big-iot.org
organization    = YOUR_ORGANIZATION

# BIG IoT Provider
providerId            = YOUR_PROVIDER_ID
providerSecret        = YOUR_PROVIDER_PASSWORD
providerLocalDnsName  = localhost
providerLocalPort     = 9004
providerPublicDnsName = big-iot.smartsantander.eu
providerPublicPort    = 443

# FIWARE
orion = YOUR_ORION_CONTEXT_BROKER_BASE_URL
EOL
```
Create logging configuration file.
```
$ sudo tee /opt/bigiot/provider/config/log4j2.xml > /dev/null << 'EOL'
    <?xml version="1.0" encoding="UTF-8"?>
    <Configuration status="INFO">
      <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
          <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n" />
        </Console>
        <RollingFile name="DailyRollingFile"
          fileName="/var/log/big-iot/smartsantander-provider.log"
          filePattern="/var/log/big-iot/smartsantander-provider.%d{yyyy-MM-dd}-%i.log.gz"
          ignoreExceptions="false" bufferedIO="true" immediateFlush="true">
          <PatternLayout>
            <!-- <Pattern>%d %p %c{1.} [%t] %m%n</Pattern> -->
            <Pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n</Pattern>
          </PatternLayout>
          <Policies>
            <TimeBasedTriggeringPolicy interval="1" modulate="true"/>
            <SizeBasedTriggeringPolicy size="250 MB" />
          </Policies>
        </RollingFile>
      </Appenders>
      <Loggers>
        <Root level="INFO">
          <AppenderRef ref="DailyRollingFile" />
        </Root>
      </Loggers>
    </Configuration>
EOL

$ sudo mkdir -p /var/log/big-iot
```
### Create systemd service
```
$ sudo tee /etc/systemd/system/smartsantander-bigiot-provider.service > /dev/null << 'EOL'
[Unit]
Description=SmartSantander BIG-IoT Provider Service

[Service]
User=root

# The configuration file application.properties should be here:
#change this to your workspace

WorkingDirectory=/opt/bigiot/provider

#path to executable. 
#executable is a bash script which calls jar file
ExecStart=/opt/bigiot/provider/bin/smartsantander-bigiot-provider

SuccessExitStatus=143
TimeoutStopSec=10
Restart=on-failure
RestartSec=5

[Install]
WantedBy=multi-user.target
EOL
```

### Start the service
```
$ sudo systemctl daemon-reload
$ sudo systemctl enable smartsantander-bigiot-provider.service
$ sudo systemctl start smartsantander-bigiot-provider

$ sudo systemctl status smartsantander-bigiot-provider
```

### Stop the service
```
$ sudo systemctl stop smartsantander-bigiot-provider
```

### Create KeyStore for SSL Certificate

We will using Let's Encrypt certificates. Upon update of certificate, we have to restart the provider.
```
$ sudo certbot certonly --standalone --non-interactive --preferred-challenges http --domains big-iot.smartsantander.eu --force-renew
```

## Restart provider
Then we can force the provider to be restarted by monitoring file changes on the certificate.

For both cases the restart provider script will be similar. It will look like:

```
$ sudo tee /opt/bigiot/provider/bin/update-keystore.sh > /dev/null << 'EOL'
#!/bin/bash

RENEWED_LINEAGE=PATH_TO_LETSENCRYPT_DOMAIN_FOLDER
CERT_ALIAS=big-iot.smartsantander.eu_letsencrypt
CERT_FILE=$RENEWED_LINEAGE/fullchain.pem
KEY_FILE=$RENEWED_LINEAGE/privkey.pem
PKCS_FILE=/tmp/pkcs.p12
PKCS_PW='PKCS_FILE_PASSWORD'
KEYSTORE_FILE=/opt/bigiot/provider/config/keystore.jks
KEYSTORE_PW='KEYSTORE_PASSWORD'

# Wait to be sure both key and cert are fully updated
sleep 2

umask 077
# Generate PKCS12 file
openssl pkcs12 -export -in $CERT_FILE -inkey $KEY_FILE -out $PKCS_FILE -name $CERT_ALIAS -passout pass:$PKCS_PW

# Delete existing entry in Java keystore
keytool -delete -keystore $KEYSTORE_FILE -alias $CERT_ALIAS -storepass $KEYSTORE_PW

# Add new Java keystore entry from PKCS12 file
keytool -importkeystore -srckeystore $PKCS_FILE -srcstorepass $PKCS_PW -srcstoretype PKCS12 -destkeystore $KEYSTORE_FILE -deststorepass $KEYSTORE_PW -destkeypass $KEYSTORE_PW -deststoretype PKCS12 -alias $CERT_ALIAS

# Delete temporary files
rm $PKCS_FILE

# Restart server
systemctl restart smartsantander-bigiot-provider
EOL
```

Make the file executable and set the owner to root.
```
$ sudo chmod +x /opt/bigiot/provider/bin/update-keystore.sh
$ sudo chown root:root /opt/bigiot/provider/bin/update-keystore.sh
```

If you want to check the content of the keystore, just run the following command:
```
$ keytool -list -v -keystore /opt/bigiot/provider/config/keystore.jks
```

As we are using Let's encrypt certificates, every 3 months the certificate is updated. Therefore to automatically update the keystore and restart the provider service, we have opted to use `incrond`. 

The inotify cron daemon (`incrond`) is a daemon which monitors filesystem events and executes commands defined in system and user tables. It's use is generally similar to `cron`.

Enable root to be able to define task.
```
$ sudo echo root >> /etc/incron.allow
```

Define the task to be run on certificate update. Note that the cron job is set for user `root`.
```
$ export RENEWED_LINEAGE=PATH_TO_LETSENCRYPT_DOMAIN_FOLDER
$ sudo incrontab -u root -e
$RENEWED_LINEAGE/fullchain.pem IN_CLOSE_WRITE /opt/bigiot/provider/bin/update-keystore.sh
```
