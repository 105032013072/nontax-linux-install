#!/bin/bash
yum -y install pcre-devel
yum -y install zlib-devel

cd ${INSTALL_DIR}/nginx-1.13.0
chmod 755 ./configure
./configure --prefix=${INSTALL_DIR}/nginx-1.13.0/nginx && make && make install