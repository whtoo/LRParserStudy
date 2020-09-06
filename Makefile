#!/usr/bin/sh

default:
	mvn compile
	mvn exec:java -Dexec.mainClass="com.blitz.demo.AppMain" -Dfile.encoding=UTF-8
