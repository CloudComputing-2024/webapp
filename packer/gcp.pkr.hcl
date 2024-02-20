packer {
  required_plugins {
    googlecompute = {
      source  = "github.com/hashicorp/googlecompute"
      version = ">=1.0.0, < 2.0.0"
    }
  }
}

variable "project_id" {
  type = string
}

variable "source_image_family" {
  type    = string
  default = "centos-stream-8"
}

variable "zone" {
  type    = string
  default = "us-west1-b"
}

variable "disk_size" {
  type    = number
  default = 20
}

variable "disk_type" {
  type    = string
  default = "pd-standard"
}

source "googlecompute" "csye6255-webapp-custom-image" {
  project_id              = var.project_id
  source_image_family     = var.source_image_family
  zone                    = var.zone
  disk_size               = var.disk_size
  disk_type               = var.disk_type
  image_name              = "csye6225-{{timestamp}}"
  image_description       = "CSYE6225 webapp Custom Image"
  image_family            = "csye6225-webapp-image"
  image_project_id        = var.project_id
  image_storage_locations = ["us"]
  ssh_username            = "packer"
}

build {
  sources = [
    "sources.googlecompute.csye6255-webapp-custom-image"
  ]

  # update centos 8
  # provisioner "shell" {
  #   script = "updateOs.sh"
  # }

   provisioner "shell" {
     script = "scripts/appDirSetup.sh"
   }

   provisioner "file" {
     source      = "/webapp/"
     destination = "/opt/webapp"
   }

  # create a local user
#  provisioner "shell" {
#    script = "scripts/createLocalUser.sh"
#  }

#   # install jdk 17
#   provisioner "shell" {
#     script = "scripts/installJDK.sh"
#   }

  # # install mysql
  # provisioner "shell" {
  #   script = "installMysql.sh"
  # }

  # # setup mysql
  # provisioner "shell" {
  #   script = "setUpMysql.sh"
  # }

  # # install maven
  # provisioner "shell" {
  #   script = "installMaven.sh"
  # }

  # #install unzip
  # provisioner "shell" {
  #   script = "installUnzip.sh"
  # }

  # # set JAVA_HOME environment variable
  # provisioner "shell" {
  #   script = "setUpJavaHomeVar.sh"
  # }


}
