#!/bin/bash

dir1=$1
dir2=$2

files=`comm -12 <(ls $dir1 | grep .wav$) <(ls $dir2)`

if [ $3 -eq 1 ]
then echo echo "Removing files..."
     for str in ${files[@]}; do
	 rm -i "$dir1/$str"
     done;
else echo echo "Printing files:"
     for str in ${files[@]}; do
	 echo "$dir1/$str"
     done;
fi


    

	   
