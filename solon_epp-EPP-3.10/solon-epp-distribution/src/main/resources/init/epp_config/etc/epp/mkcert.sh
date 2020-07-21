#!/bin/bash
#
# Name 		: mkcert.sh
# Author 	: F. Barmes
# Version 	: 1.0
#
# Usage 	: mkcert.sh "<space separated list of certificate names>"
#
#
# Description :
#
# Test purposes only !!
#
# This script creates for each entry defined in $USERS
#	- a private key
#	- a self signed certificate
#	- a PKCS12 keystore containing the key and certificate
#
#
#
#


#---- Script variables
USERS=$1

if [ -z $1 ] ; then
	echo "usage" 
	exit 1;
fi

if [ -z ${JAVA_HOME} ] ; then
	echo JAVA HOME is not set
	exit 1;
fi


JAVA_BIN=${JAVA_HOME}/bin/
KEYTOOL=${JAVA_BIN}/keytool

DIR_CRT=ssl.cert.in
DIR_KEY=ssl.key
DIR_P12=ssl.p12
DIR_TMP=tmp

CA_FQDN=sword-solon-dev-ca.swordgroup.lan
CA_KEY=ca.key
CA_CRT=ca.crt 

SERVER_FQDN=sword-solon-dev-server.swordgroup.lan
SERVER_KEY=${DIR_KEY}/server.key
SERVER_CSR=${DIR_TMP}/server.csr
SERVER_SRL=${DIR_TMP}/server.srl
SERVER_CRT=${DIR_CRT}/server.crt

PASSWORD=secret




echo ""
echo '=========== Cleanup ==========='
mkdir $DIR_KEY
mkdir $DIR_P12
mkdir $DIR_CRT
mkdir $DIR_TMP

rm ${DIR_KEY}/*
rm ${DIR_P12}/*
rm ${DIR_CRT}/*
rm ${DIR_TMP}/*
rm *.key *.csr *.crt *.jks  *.p12




echo '=========== Generate root certificate key/cert ==========='
openssl genrsa \
	-out $CA_KEY \
	 2048	

openssl req \
	-new \
	-sha1 \
	-x509 \
	-days 365 \
	-key $CA_KEY \
	-out $CA_CRT \
	-subj "/O=Company/OU=Department/CN=${CA_FQDN}"


for user in $USERS; do 
	
	echo "=========== generate self signed key and certificate for client  ${user} ==========="

	key="${DIR_KEY}/${user}.key";
	p12="${DIR_P12}/${user}.p12";
	csr="${DIR_TMP}/${user}.csr";
	crt="${DIR_CRT}/${user}.crt";
	
	
	#---- generate RSA private key
	echo "    private key"
	openssl genrsa -out ${key} 2048 >& /dev/null
	
	#---- generate certificate signing request
	echo "    certificate signing request"
	openssl req -new -key ${key} -out ${csr} -subj "/O=Company/OU=Department/CN=${user}";
	
	
	#---- generate self signed certificate 
	echo " Self sign the csr to a self signed certificate"
	openssl x509 -signkey ${key} -req -in ${csr} -out ${crt} -days 365


	#---- generate crt signed with root certificate
	#echo " Sign the csr with root CA "
	#openssl x509 -req -in ${csr} -out ${crt} -CA ${CA_CRT} -CAkey ${CA_KEY} -CAcreateserial -days 365;

	#---- make a PKCS12 certificate from key and self signed public certificate.
	echo "    create pkcs12"
	openssl pkcs12 -export -out ${p12} -in ${crt} -inkey ${key}  -name "${user}"  -password pass:${PASSWORD}
		
	
done


echo ""
echo '=========== Cleanup ==========='
rm -r $DIR_TMP

