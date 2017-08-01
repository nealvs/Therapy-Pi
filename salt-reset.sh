#!/bin/bash

service salt-minion stop
rm /etc/salt/pki/minion/minion_master.pub
rm /etc/salt/pki/minion/minion.*
cat /dev/null >/etc/salt/minion_id
