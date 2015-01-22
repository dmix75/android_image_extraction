# android_image_extraction
Java Image Extraction tool

This tool is used to extract the new Lollipop system.new.dat file.  It is based off the code from the following

[/bootable/recovery/updater/blockimg.c] (http://click.xda-developers.com/api/click?format=go&jsonp=vglnk_142190470365116&key=f0a7f91912ae2b52e0700f73990eb321&libId=5cae16e8-9301-4958-a825-1b2e22d175a3&loc=http%3A%2F%2Fforum.xda-developers.com%2Fandroid%2Fsoftware-hacking%2Fhow-to-conver-lollipop-dat-files-to-t2978952&v=1&out=https%3A%2F%2Fandroid.googlesource.com%2Fplatform%2Fbootable%2Frecovery%2F%2B%2Fandroid-5.0.2_r1%2Fupdater%2Fblockimg.c&ref=https%3A%2F%2Fwww.google.com%2F&title=%5BHOW%20TO%5D%20Decompress%20Lollipop%20.dat%20files%20%5BWin%E2%80%A6%20%7C%20Android%20Development%20and%20Hacking%20%7C%20XDA%20Forums&txt=%2Fbootable%2Frecovery%2Fupdater%2Fblockimg.c)

and

howellzhu and luxi78 
https://github.com/xpirt/sdat2img/blob/master/sdat2img.py


Usage: 
java -jar SDA2Image system.transfer.list system.new.dat system.img

When decompressing cm-12 image use the sda2img script 
sda2img /path/to/cm-12-IMAGE.zip


