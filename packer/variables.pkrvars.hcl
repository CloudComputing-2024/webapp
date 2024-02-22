project_id              = "csye6225-414206"
source_image_family     = "centos-stream-8"
zone                    =  "us-west1-b"
disk_size               = 100
disk_type               = "pd-balanced"
image_name              = "csye6225-custom-image-{{timestamp}}"
image_family            = "csye6225-webapp-image"
image_storage_locations = ["us"]
ssh_username            = "packer"


