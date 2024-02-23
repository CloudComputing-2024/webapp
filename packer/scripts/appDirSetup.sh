set -e

DIR="/opt/webapp"

sudo mkdir -p "${DIR}"

sudo chown -R packer:packer "${DIR}"

# change the owner of the deployed file to csye6255
#sudo chown -R csye6225:csye6225 "${DIR}"

## allow csye6255 to read and execute the app
#sudo chmod 500 /opt/webapp/webapp.jar