#!/bin/sh

INPUT_DIR=$1
OUTPUT_DIR_1=$2
OUTPUT_DIR_2=$3
SCRIPT_DIR="./scripts/"

for file in `ls $INPUT_DIR`
do
	output_file=$(basename $file .plotdata)_graph
	$SCRIPT_DIR/makeGraph $INPUT_DIR/$file $OUTPUT_DIR_1/${output_file} $OUTPUT_DIR_2/${output_file}
done

