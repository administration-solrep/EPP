#!/bin/sh

INPUT_DIR=$1
OUTPUT_DIR=$2


for file in `ls $INPUT_DIR`
do
	output_file=$(basename $file .rawdata).plotdata
	python ./scripts/createPlotData.py $INPUT_DIR/$file > $OUTPUT_DIR/${output_file}
done

