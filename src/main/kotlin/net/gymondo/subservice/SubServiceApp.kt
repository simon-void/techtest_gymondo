package net.gymondo.subservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SubServiceApp

fun main(args: Array<String>) {
    runApplication<SubServiceApp>(*args)
}
