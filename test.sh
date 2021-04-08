#!/bin/bash
# Testing our annotation processor
# Test is very simple:
# 1. We assume annotation-processor is already compiled into a 
#    "annotation-processor.jar" in the same directory as this script.
# 2. We compile annotation-client/src against the aformentioned jar with
#    annotation processing using javac.
# 3. Successful compilation means that the TypeProvider component was able
#    to produce syntactically correct types, and that these types contain
#    fields with the same type as their usage in
#    "annotation-client/src/edu/brown/Main.java"
# 4. We run the compiled version of "annotation-client/src/edu/brown/Test.java"
#    which dumps back all the parsed JSON file (during type providing) into
#    output json files. This tests that the said TypeProvider was able to also
#    inject syntactically correct types during compilation into the provided
#    types.
# 5. Finally, we check that the output and input Json file contain identical
#    data, modulo re-ordering of keys within unordered objects, and
#    white-spaces/indentations. This ensures that TypeProvider loaded and parsed
#    correct data at compile time, and then was able to dump it back to a JSON
#    at runtime correctly.

ERROR=0

# Create temporary output directory
rm -rf out && mkdir out && mkdir out/json

# Compile client into the output directory and perform annotations processing.
echo ""
echo ""
echo "Compiling..."
javac -cp annotation-processor.jar -Aprovidej_path=$(pwd) -d out/ annotation-client/src/edu/brown/*

# Run test.java dumping all the TypeProvided JSON into files under out/json/
echo ""
echo ""
echo "Running..."
java -cp annotation-processor.jar:out/ edu.brown.Test out/json/

# Check output files equal to input files.
echo ""
echo ""
echo "Checking..."
for OUTFILE in out/json/*
do
  echo ""
  echo ""
  echo "Checking $(basename $OUTFILE)"

  INFILE=annotation-client/samples/$(basename "$OUTFILE")
  python eqtest.py "$INFILE" "$OUTFILE"

  if [ $? -eq 0 ]
  then
    echo "Test ok!"
  else
    ERROR=1
  fi
done

exit $ERROR

