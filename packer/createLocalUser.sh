# create a user group
sudo groupadd csye6225

# create a user
sudo adduser csye6225 -g csye6225 --shell /usr/sbin/nologin

## change the owner of the feployed file to csye6255
#sudo chown csye6225:csye6255 webapp.jar
#
## allow csye6255 to read and execute the app
#sudo chmod 500 webapp.jar