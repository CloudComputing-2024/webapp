# add sql user
mysql -u root -e "CREATE USER 'webapplication'@'localhost' IDENTIFIED BY 'webapplication';
GRANT ALL PRIVILEGES ON *.* TO 'webapplication'@'localhost' WITH GRANT OPTION;
FLUSH PRIVILEGES;"