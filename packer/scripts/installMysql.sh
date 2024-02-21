# install mysql
sudo dnf install @mysql -y
sudo systemctl start mysqld
sudo systemctl enable mysqld
sudo systemctl status mysqld