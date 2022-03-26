package hellothere

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HellothereApplication

fun main(args: Array<String>) {
	runApplication<HellothereApplication>(*args)
}
