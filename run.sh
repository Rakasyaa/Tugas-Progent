#!/bin/sh
mkdir -p out
javac -d out src/DownloadFileGUI.java && java -cp out DownloadFileGUI
