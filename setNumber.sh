#/bin/bash

if [ ! -n "$1" ]
then
	echo 'Missed argument : machine_number'
	exit 1
fi

if [ "$(id -u)" != "0" ]; then
	echo "You need to be root."
	exit 1
fi

hostname "genuease-handcrank-$1"
chmod 777 /etc/hostname /etc/hosts
sed "s/template/$1/g" /home/pi/hosts.template > /home/pi/hosts.out
rm /etc/hosts
mv /home/pi/hosts.out /etc/hosts
echo "genuease-handcrank-$1" > /etc/hostname
chmod 644 /etc/hostname /etc/hosts

/etc/init.d/hostname.sh restart

rm /etc/salt/minion_id
rm /etc/salt/pki/minion/minion.pem
rm /etc/salt/pki/minion/minion.pub

/etc/init.d/salt-minion restart

rm -r /home/pi/chrome-data

