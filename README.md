# android_image_extraction
Java Image Extraction tool

This tool is used to extract the new Lollipop system.new.dat file.  It is based off the code from the following

 /bootable/recovery/updater/blockimg.c

and

howellzhu and luxi78
https://github.com/xpirt/sdat2img/blob/master/sdat2img.py


Usage:
java -jar SDA2Image system.transfer.list system.new.dat system.img

When decompressing cm-12 image use the sda2img
sda2img /path/to/cm-12-IMAGE.zip


