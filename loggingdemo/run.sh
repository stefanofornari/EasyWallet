#!/bin/sh

export CLASSPATH=.:EasyWallet-0.0-SNAPSHOT.jar:hsqldb-2.7.1.jar:ormlite-jdbc-6.1.jar

java -Djava.util.logging.config.file=./logging.properties -Dcom.j256.simplelogger.backend=JAVA_UTIL ste.w3.easywallet.ui.demo.LoggingDemo
