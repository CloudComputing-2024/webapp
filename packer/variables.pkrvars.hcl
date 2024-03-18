project_id              = "dev-project-415121"
source_image_family     = "centos-stream-8"
zone                    =  "us-west1-b"
disk_size               = 100
disk_type               = "pd-balanced"
image_name              = "dev-project-custom-image-{{timestamp}}"
image_family            = "dev-project-custom-image"
image_storage_locations = ["us"]
ssh_username            = "packer"


