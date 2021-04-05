# TDF02-145 Remote {#mainpage}

## Introduction

This is the repository for Android remote app related matters for robot for Autism Spectrum Disorder therapy development funded through HEC TDF. The documentation folder contains all the code for Android remote division of HEC funded project TDF 02-145. There are multiple files contained in this folder. This code will run on most Android phones and Tablets.

## Things to know

This section details the things one must know before using this code.

### MAC Address 

* You'll need the MAC Address of whatever device the remote will be connecting to. You have to update that strings.xml
* This code, for now, only works if the other device is paired with the remote device. 
* It only supports Serial Communication.

### What works how

* There are three section - Expression, Locomotion, and Speaking
* Expressions will change on short presses. Long presses play audio files. For now, these mp3s have to be present in the "face device".
* Locomotion simply moves the robot by a "1000" steps of the encoder.
* Speaking features custom text message sending, pitch control, and pre-written most used phrases.

#### Author

Taha Shaheen, Saifullah, Muhammad Wajahat Qureshi

#### Version

chotuX
