#!/usr/bin/sh

INPUT_DIR=$1
OUTPUT_DIR=$2

for file in `ls $INPUT_DIR`
do
	output_file=$(basename $file .out).rawdata
	cat $INPUT_DIR/$file | grep 'start inject Dossier' | awk '{ print $1,$2,$11 }' | sed -e 's/=//g' | sed -e 's/>//g' > $OUTPUT_DIR/${output_file}
done


