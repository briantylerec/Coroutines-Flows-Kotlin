package com.monksoft.fundamentoscorrutinas

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import java.util.concurrent.TimeoutException

val countries = listOf("Santander", "CDMX", "Lima", "buenos aires", "Cuenca")

fun main(){
    //basicChannel()
    //closeChannel()
    //produceChannel()
    //pipeLine()
    //bufferChannel()
    exceptions()
    readLine()
}

fun exceptions() {
    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("notifica al programador $throwable in $coroutineContext")

        if(throwable is ArithmeticException) println("mostrar mensaje reintenta...")
        print(0)
    }
    runBlocking {
        newTopic("manejo de excepciones")

        val globalScope = CoroutineScope(Job() + exceptionHandler)
        globalScope.launch {
            delay(200)
            throw TimeoutException()
        }


        CoroutineScope(Job() + exceptionHandler).launch {
            val result = async {
                delay(500)
                multiLambda(2,3){
                    if(it>5) throw java.lang.ArithmeticException()
                }
            }
            println("Result: ${result.await()}")
        }



        val channel = Channel<String>()
        CoroutineScope(Job()).launch (exceptionHandler) {
            delay(800)
            countries.forEach {
                channel.send(it)
                if(it == "Lima") channel.close()
            }
        }
        channel.close()
        channel.consumeEach { println(it) }

    }
}

fun bufferChannel() {
    runBlocking {
        newTopic("buffer para channel")
        val time = System.currentTimeMillis()
        val channel = Channel<String>()
        launch{
            countries.forEach {
                delay(100)
                channel.send(it)
            }
            channel.close()
        }

        launch{
            delay(1000)
            channel.consumeEach { println(it) }
            println("time: ${System.currentTimeMillis() - time}")
        }

        //aplicando buffer

        val bufferTime = System.currentTimeMillis()
        val bufferChannel = Channel<String>(3)//se le pasa cuantos datos van a estar en el buffer
        launch{
            countries.forEach {
                delay(100)
                bufferChannel.send(it)
            }
            bufferChannel.close()
        }

        launch{
            delay(1000)
            bufferChannel.consumeEach { println(it) }
            println("buffer time: ${System.currentTimeMillis() - bufferTime}")
        }
    }
}

fun pipeLine() {
    runBlocking {
        newTopic("pipeLines")
        val citiesChannel = produceCities()
        val foodsChannel = produceCFoods(citiesChannel)
        foodsChannel.consumeEach { println(it) }
        citiesChannel.cancel()
        foodsChannel.cancel()
        println("todo ok")
    }
}

fun CoroutineScope.produceCFoods(cities: ReceiveChannel<String>): ReceiveChannel<String> = produce {
    for (city in cities){
        val food = getFoodByCity(city)
        send("$food desde $city")
    }
}

suspend fun getFoodByCity(city: String): String {
    delay(300)
    return when(city){
        "Santander" -> "papas,"
        "CDMX" -> "arroz"
        "Lima" -> "empanadas"
        "buenos aires" -> "pollo"
        "Cuenca" -> "cuy"
        else -> "sin datos"
    }
}

fun produceChannel() {
    runBlocking {
        newTopic("canales y patron productor consumidor")
        val names = produceCities()
        names.consumeEach { println(it) }
    }
}

//funcion extendida
fun CoroutineScope.produceCities(): ReceiveChannel<String> = produce {
    countries.forEach { send(it) }
}

fun closeChannel() {
    runBlocking {
        val channel = Channel<String>()
        launch {
            countries.forEach {
                channel.send(it)
                if(it == "Lima") {
                    channel.close()
                    return@launch
                }
            }
        }

        while(!channel.isClosedForSend){ // si esta abierto
            println(channel.receive())
        }
    }
}

fun basicChannel() {
    runBlocking {
        newTopic("canal basico")

        val channel = Channel<String>()
        launch {
            countries.forEach {
                channel.send(it)
            }
        }

        repeat(5){
            println(channel.receive())
        }
    }
}
