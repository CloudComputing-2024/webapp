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
}

variable "zone" {
  type    = string
}

variable "disk_size" {
  type    = number
}

variable "disk_type" {
  type    = string
}

variable "image_name"{
  type = string
}

variable "image_family"{
  type = string
}


variable "image_storage_locations"{
  type = list(string)
}

variable "ssh_username"{
  type = string
}

source "googlecompute" "csye6255-webapp-custom-image" {
  project_id              = var.project_id
  source_image_family     = var.source_image_family
  zone                    = var.zone
  disk_size               = var.disk_size
  disk_type               = var.disk_type
  image_name              = var.image_name
  image_family            = var.image_family
  image_project_id        = var.project_id
  image_storage_locations = var.image_storage_locations
  ssh_username            = var.ssh_username
}

build {
  sources = [
    "sources.googlecompute.csye6255-webapp-custom-image"
  ]

  # update centos 8
  provisioner "shell" {
    script = "scripts/updateOs.sh"
  }

  provisioner "shell" {
    script = "scripts/appDirSetup.sh"
  }

  provisioner "file" {
    source      = "webapp/"
    destination = "/opt/webapp"
  }

  # install jdk 17
  provisioner "shell" {
    script = "scripts/installJDK.sh"
  }

  # install mysql
  provisioner "shell" {
    script = "scripts/installMysql.sh"
  }

  # setup mysql
  provisioner "shell" {
    script = "scripts/setUpMysql.sh"
  }

  # install maven
  provisioner "shell" {
    script = "scripts/installMaven.sh"
  }

  #install unzip
  provisioner "shell" {
    script = "scripts/installUnzip.sh"
  }

  # set JAVA_HOME environment variable
  provisioner "shell" {
    script = "scripts/createJavaHomeVar.sh"
  }

  # set JAVA_HOME environment variable
  provisioner "shell" {
    script = "scripts/setUpJavaHomeVar.sh"
  }

  provisioner "shell" {
    script = "scripts/unzipFile.sh"
  }

  provisioner "file" {
    source      = "webapp.service"
    destination = "/tmp/webapp.service"
  }

  # create a local user
  provisioner "shell" {
    script = "scripts/createLocalUser.sh"
  }

  provisioner "shell" {
    script = "scripts/systemd.sh"
  }
}
