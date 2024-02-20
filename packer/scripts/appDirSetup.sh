set -e

DIR="/opt/webapp"

sudo mkdir -p "${DIR}"

sudo chown -R packer:packer "${DIR}"