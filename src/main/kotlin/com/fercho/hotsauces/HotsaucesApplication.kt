package com.fercho.hotsauces

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HotsaucesApplication

fun main(args: Array<String>) {
	runApplication<HotsaucesApplication>(*args)
}
