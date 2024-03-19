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
  type = string
}

variable "zone" {
  type = string
}

variable "disk_size" {
  type = number
}

variable "disk_type" {
  type = string
}

variable "image_name" {
  type = string
}

variable "image_family" {
  type = string
}


variable "image_storage_locations" {
  type = list(string)
}

variable "ssh_username" {
  type = string
}

source "googlecompute" "dev-project-custom-image" {
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
  network                 = "projects/${var.project_id}/global/networks/default"
}

build {
  sources = [
    "sources.googlecompute.dev-project-custom-image"
  ]

  # update centos 8
  provisioner "shell" {
    script = "scripts/updateOs.sh"
  }

  # set up app directory
  provisioner "shell" {
    script = "scripts/appDirSetup.sh"
  }

  # copy webapp folder to vm
  provisioner "file" {
    source      = "webapp/"
    destination = "/opt/webapp"
  }

  # install jdk 17
  provisioner "shell" {
    script = "scripts/installJDK.sh"
  }

  # install maven
  provisioner "shell" {
    script = "scripts/installMaven.sh"
  }

  # set JAVA_HOME environment variable
  provisioner "shell" {
    script = "scripts/createJavaHomeVar.sh"
  }

  # set JAVA_HOME environment variable
  provisioner "shell" {
    script = "scripts/setUpJavaHomeVar.sh"
  }

  # install unzip
  provisioner "shell" {
    script = "scripts/installUnzip.sh"
  }

  # unzip file
  provisioner "shell" {
    script = "scripts/unzipFile.sh"
  }

  # install google ops agent
  provisioner "shell" {
    script = "scripts/installGoogleOpsAgent.sh"
  }

  # copy config file to tmp
  provisioner "file" {
    source      = "config.yaml"
    destination = "/tmp/config.yaml"
  }

  # copy webapp service to vm
  provisioner "file" {
    source      = "webapp.service"
    destination = "/tmp/webapp.service"
  }

  # create a local user
  provisioner "shell" {
    script = "scripts/createLocalUser.sh"
  }

  # move config file and set permission
  #  provisioner "shell" {
  #    script = "scripts/moveOpsAgentConfigFile.sh"
  #  }

  # use systemd to start service
  provisioner "shell" {
    script = "scripts/systemd.sh"
  }
}
