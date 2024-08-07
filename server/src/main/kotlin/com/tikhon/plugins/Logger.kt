package com.tikhon.plugins

import org.slf4j.LoggerFactory
import org.slf4j.Logger

fun getLogger(forClass: Class<*>): Logger =
    LoggerFactory.getLogger(forClass)
