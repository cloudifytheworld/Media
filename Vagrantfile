# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure("2") do |config|

   #master
   config.vm.define "master" do |master|
    master.vm.box = "ubuntu/bionic64"
    master.vm.hostname = "master"
    master.vm.network "forwarded_port", guest: 4200, host: 4200
    master.vm.network "forwarded_port", guest: 22, host: 1022
    master.vm.network "private_network", ip: "192.168.33.10"

    master.vm.synced_folder "/vagrant/ubuntu", "/project/apps"
    master.vm.provider "virtualbox" do |vb|
     vb.name = 'master'	
     vb.customize ["modifyvm", :id, "--memory", 2048]
    end
    master.vm.provision "shell", inline: <<-SHELL
     wget -qO- https://get.docker.com/ | sh
    SHELL
   end

   #worker1
   config.vm.define "worker1" do |worker1|
    worker1.vm.box = "ubuntu/bionic64"
    worker1.vm.hostname = "worker1"
    worker1.vm.network "forwarded_port", guest: 4300, host: 4300   
    worker1.vm.network "forwarded_port", guest: 22, host: 1122
    worker1.vm.network "private_network", ip: "192.168.33.11"

    worker1.vm.synced_folder "/vagrant/ubuntu", "/project/apps"
    worker1.vm.provider "virtualbox" do |vb|
     vb.name = 'worker1'	
     vb.customize ["modifyvm", :id, "--memory", 2048]
    end
    worker1.vm.provision "shell", inline: <<-SHELL
     wget -qO- https://get.docker.com/ | sh
    SHELL
   end

   #worker2
   config.vm.define "worker2" do |worker2|
    worker2.vm.box = "ubuntu/bionic64"
    worker2.vm.hostname = "worker2"
    worker2.vm.network "forwarded_port", guest: 4400, host: 4400
    worker2.vm.network "forwarded_port", guest: 22, host: 1222
    worker2.vm.network "private_network", ip: "192.168.33.12"

    worker2.vm.synced_folder "/vagrant/ubuntu", "/project/apps"
    worker2.vm.provider "virtualbox" do |vb|
     vb.name = 'worker2'	
     vb.customize ["modifyvm", :id, "--memory", 2048]
    end
    worker2.vm.provision "shell", inline: <<-SHELL
     wget -qO- https://get.docker.com/ | sh
    SHELL
   end

   #worker3
   config.vm.define "worker3" do |worker3|
    worker3.vm.box = "ubuntu/bionic64"
    worker3.vm.hostname = "worker3"
    worker3.vm.network "forwarded_port", guest: 4500, host: 4500
    worker3.vm.network "forwarded_port", guest: 22, host: 1322
    worker3.vm.network "private_network", ip: "192.168.33.13"

    worker3.vm.synced_folder "/vagrant/ubuntu", "/project/apps"
    worker3.vm.provider "virtualbox" do |vb|
     vb.name = 'worker3'	
     vb.customize ["modifyvm", :id, "--memory", 2048]
    end
    worker3.vm.provision "shell", inline: <<-SHELL
     wget -qO- https://get.docker.com/ | sh
    SHELL
   end
end

