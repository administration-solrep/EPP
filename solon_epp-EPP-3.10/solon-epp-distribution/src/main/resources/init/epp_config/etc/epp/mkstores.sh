#!/bin/bash
#
# Name 		: mkstores.sh
# Author 	: F. Barmes
# Version 	: 1.0
#
# Usage 	: mkstores.sh 
#
# Description
#-----------------------------------
# Ce script génère un keystore et un truststore.
#
# Le keystore est généré en utilisant :
# - les paires de clés/certificats dans les repertoires ssl.key/*.key et ssl.crt/*.crt
# - les keystores au format PKCS12 depuis le repertoire ssl.p12/*.p12
#
# Le truststore est généré en utilisant :
# - les certificats x509 depuis le certificat ssl.crt/*.crt
#
# Les repertoires sont crées à la première utilisation du script
#
#

#---
PASSWORD=secret


#------------------------- DO NOT EDIT PAST THIS POINT  ------------------------

#--- Check JAVA_HOME
if [ -z ${JAVA_HOME} ] ; then
	echo JAVA HOME is not set
	exit 1;
fi

#--- Set script variables
JAVA_BIN=${JAVA_HOME}/bin/
KEYTOOL=${JAVA_BIN}/keytool

KEYSTORE_FILE=keystore.jks
TRUSTSTORE_FILE=truststore.jks

DIR_KEY=ssl.key
DIR_CRT_INT=ssl.crt.int
DIR_CRT_EXT=ssl.crt.ext

DIR_PEM_INT=ssl.pem.int
DIR_PEM_EXT=ssl.pem.ext

DIR_P12=ssl.p12
DIR_P12_TMP=ssl.p12.tmp


#---- create directory structure

DIR_LIST="${DIR_KEY} ${DIR_CRT_INT} ${DIR_CRT_EXT} ${DIR_PEM_INT} ${DIR_PEM_EXT} ${DIR_P12} ${DIR_P12_TMP}"

for dir in $DIR_LIST; do
	if [ ! -d $dir ] ; then
		echo "create ${dir}"
		mkdir $dir;
	fi	
done

#---- cleanup
if [ -f ${KEYSTORE_FILE} ]; then rm ${KEYSTORE_FILE}; fi
if [ -f ${TRUSTSTORE_FILE} ]; then rm ${TRUSTSTORE_FILE}; fi
rm *.log


#---- convert all crt to pem
for file in `ls ${DIR_CRT_INT}/*.crt`; do

	basename=`basename ${file} .crt`
	crt=${DIR_CRT_INT}/${basename}.crt
	pem=${DIR_PEM_INT}/${basename}.pem
	
	
	echo "convert ${crt} to ${pem}"
	openssl x509 -in ${crt} -out ${pem} -outform PEM 

done

for file in `ls ${DIR_CRT_EXT}/*.crt`; do

	basename=`basename ${file} .crt`
	crt=${DIR_CRT_EXT}/${basename}.crt
	pem=${DIR_PEM_EXT}/${basename}.pem
	
	
	echo "convert ${crt} to ${pem}"
	openssl x509 -in ${crt} -out ${pem} -outform PEM 

done



#---- copy key/pem pairs into p12
echo ""
echo "---------- Build PKCS12 keystore from key/crt pairs"
for file in `ls ${DIR_KEY}/*.key`; do

	base=`basename $file`
	base=`echo "${base/%.key/}"`
	name=${base}

	key=${DIR_KEY}/${base}.key
	pem=${DIR_PEM_INT}/${base}.pem
	p12=${DIR_P12_TMP}/${base}.p12
	p12_check=${DIR_P12}/${base}.p12
	
	echo "handling ${key} | ${pem} as ${name}"
	
	#--- check there is not already a p12 file
	if [ -f ${p12_check} ] ; then
		echo "WARNING : file ${p12_check} already exists. Ignoring";
		continue; 
	fi
	
	#--- check there is a crt file for this key
	if [ ! -f $pem ] ; then
		echo "ERROR : could not find certificate ${pem} for file ${key}"
		continue; 
	fi
	
	#--- make PKCS12 keystore from key/crt pair
	openssl pkcs12 -export -out ${p12} -in ${pem} -inkey ${key}  -name "${name}"  -password pass:${PASSWORD}
	
done




#---- merge all p12 into PKCS12 keystore
echo "---------- Import all p12 into keystore"

for file in `ls ${DIR_P12}/* ${DIR_P12_TMP}/*`; do

	base=`basename $file`
	name=`echo "${base/%.p12/}"`

	p12=${file}
	
	
	echo "handling file ${file} as ${name}"
	
	${KEYTOOL} \
		-importkeystore \
		-deststorepass ${PASSWORD} \
		-destkeypass ${PASSWORD} \
		-deststoretype JKS \
		-destkeystore ${KEYSTORE_FILE} \
		-srckeystore ${p12} \
		-srcstoretype PKCS12 \
		-srcstorepass ${PASSWORD} \
		-alias ${name}
	
done


#---- merge all crt into truststore
echo "---------- Import all crt into keystore"

for file in `ls ${DIR_PEM_EXT}/*.pem`; do
	
	base=`basename $file`
	name=`echo "${base/%.pem/}"`

	pem=${file}
	echo "handling file ${pem} as ${name}"

	${KEYTOOL} -import -alias ${name}  -file ${pem} -storepass ${PASSWORD} -noprompt -keystore ${TRUSTSTORE_FILE}

done



#---- Print keystore content

if [ -f ${KEYSTORE_FILE} ]; then 
	echo "print keystore content to " ${KEYSTORE_FILE}.log
	${KEYTOOL} -list -v -keystore ${KEYSTORE_FILE}  -storepass ${PASSWORD} >& ${KEYSTORE_FILE}.log 
fi


if [ -f ${TRUSTSTORE_FILE} ]; then 
	echo "print truststore content to " ${TRUSTSTORE_FILE}.log
	${KEYTOOL} -list -v -keystore ${TRUSTSTORE_FILE}  -storepass ${PASSWORD} >& ${TRUSTSTORE_FILE}.log
fi



#---- cleanup
rm -r ${DIR_P12_TMP}
rm -r ${DIR_CRT_INT}
rm -r ${DIR_CRT_EXT}
rm -r ${DIR_P12}
