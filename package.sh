#!/bin/sh
export JAVA_HOME="D:/dev/apps/jdk1.6.0_45"
griffon package zip
cp dist/zip/lib/* packaged/lib/ 
cp dist/zip/lib/* "packaged/Sample Map Builder.app/Contents/Resources/Java/"
cd packaged
zip -r SampleMapBuilder-Windows.zip SampleMapBuilder.exe SampleMapBuilder.pdf lib/
zip -r SampleMapBuilder-OSX.zip "Sample Map Builder.app" SampleMapBuilder.pdf

