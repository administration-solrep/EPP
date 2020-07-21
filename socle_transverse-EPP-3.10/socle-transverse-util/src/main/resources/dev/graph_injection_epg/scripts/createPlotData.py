#!/usr/bin/python
  # -*- coding: utf-8 -*-

''' Extrait dans un format directement utilisable par gnuplot les informations
d'une injection EPG : durée d'une injection d'un dossier, temps de l'injection d'un dossier
'''

from datetime import datetime
import sys

raw_data_from_injection = sys.argv[1]
file_handle = open(raw_data_from_injection)

last_dt = False
total,index = 0, 0
print "%s\t%s\t%s\t%s" % ("# Index","Nor","Durée d'injection (s)","Temps depuis le début de l'injection (s)" ) 
for line in file_handle.readlines():
	dt_str, time_str, nor = line.split(" ")
	time_str, milliseconds = time_str.split(",")
	dt = datetime.strptime(dt_str + " " + time_str, "%Y-%m-%d %H:%M:%S") 
	if not last_dt:
		diff = 0
	else:
		diff_delta = dt - last_dt
		diff = diff_delta.seconds + float(milliseconds)/1000
	last_dt = dt
	total += diff
	index += 1
	print "%s\t%s\t%s\t%s\t%s" % (index,nor.strip(), diff, total,dt.strftime(date_format_write)) 
