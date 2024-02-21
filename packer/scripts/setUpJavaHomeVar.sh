source ~/.bashrc
sudo update-alternatives --set java /usr/lib/jvm/java-17-openjdk-17.0.6.0.9-0.3.ea.el8.x86_64/bin/java
echo $JAVA_HOME
java -version