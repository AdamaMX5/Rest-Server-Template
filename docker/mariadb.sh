#!/bin/sh

MYSQL_DIR=/var/lib/mysql
MYSQL_DATABASE=flussmark
MYSQL_PW=root
MYSQL_USER=root

echo "[Mariadb] initialize"

if [ -d "/run/mysqld" ]; then
	chown -R mysql:mysql /run/mysqld
else
	mkdir -p /run/mysqld
	chown -R mysql:mysql /run/mysqld
fi

if [ -d "$MYSQL_DIR/mysql" ]; then
	echo "[MariaDB] Data directory already present, skipping creation"
	chown -R mysql:mysql $MYSQL_DIR
else
	echo "[MariaDB] not found; initializing database"

  chown -R mysql:mysql $MYSQL_DIR

  mysql_install_db --user=mysql --ldata=/var/lib/mysql > /dev/null
fi

tmpfile=`mktemp`
if [ ! -f "$tmpfile" ]; then
  echo "[MariaDB] could not create tmp file ... exiting"
  exit 1
fi

cat << EOF > $tmpfile
USE mysql;
FLUSH PRIVILEGES ;
create database $MYSQL_DATABASE;
grant all privileges on $MYSQL_DATABASE.* to $MYSQL_USER@'%' identified by '$MYSQL_PW';
flush privileges;
EOF

echo "[MariaDB] initializing database $MYSQL_DATABASE and user privileges"
/usr/bin/mysqld --user=mysql --bootstrap --verbose=1 --skip-name-resolve --skip-networking=0 < $tmpfile
if [ $? -ne 0]; then
  echo "[MariaDB] could not initialize database ... exiting $?"
  exit 1
fi
rm -f $tmpfile

echo "[MariaDB] Starting up"
/usr/bin/mysqld --user=mysql --skip-name-resolve --skip-networking=0 &

# todo: better solution to wait for startup
sleep 2

echo "[MariaDB] Running"