#!/bin/bash

rm -rf wildfly-8.0.0.Alpha1
unzip wildfly-8.0.0.Alpha1.zip

# move setup script to a better location
cp setup-wildfly.cli wildfly-8.0.0.Alpha1/bin

# add a Management User so we can use the console
cd wildfly-8.0.0.Alpha1/bin
./add-user.sh --silent --user $USER --password Password1! --realm ManagementRealm

# start the domain
./domain.sh


