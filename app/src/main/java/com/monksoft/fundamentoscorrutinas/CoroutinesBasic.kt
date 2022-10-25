package com.monksoft.fundamentoscorrutinas

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce

fun main(){
    //globalScope()
    //suspendFun()

    newTopic("Constructores de corrutinas")
    //cRunBlocking()
    //cLaunch()
    //cAsync()
    //job()
    //deferred()
    cProduce()
    readLine()
}

fun cProduce() = runBlocking {
    newTopic("Produce")
    val names = produceNames()
    names.consumeEach { print(it) }
}

fun CoroutineScope.produceNames(): ReceiveChannel<String> = produce {
    (1..5).forEach{ send("name$it")}
}

fun deferred() {
    runBlocking {
        newTopic("Deferred")
        val deferred = async {
            startMsg()
            delay(2_100)
            println("deferred...")
            endMsg()
            multi(5,2)
        }
        println("Deferred: $deferred")
        println("Valor del deferred.await: ${deferred.await()}")

        val result = async {
            multi(5,2)
        }.await()

        println("Result: $result")
    }
}

fun job() {
    runBlocking {
        newTopic("Job")
        val job = launch {
            startMsg()
            delay(2_100)
            println("job...")
            endMsg()
        }
        //flujo normal a completar job
        delay(2200) // si se comenta el job no se ha terminado
        println("Result: $job")
        println("isActive: ${job.isActive}")
        println("isCancelled: ${job.isCancelled}")
        println("isComplete: ${job.isCompleted}")

        //si se cancela
        delay(someTime())
        println("Tarea cancelada o interrumpida")
        job.cancel()

        println("Result: $job")
        println("isActive: ${job.isActive}")
        println("isCancelled: ${job.isCancelled}")
        println("isComplete: ${job.isCompleted}")
    }
}

fun cAsync() {
    runBlocking {
        newTopic("Async")
        val result = async {
            startMsg()
            delay(someTime())
            println("async...")
            endMsg()
        }
        println("Result: ${result.await()}")
    }
}

fun cLaunch() {
    runBlocking {
        newTopic("Launch")
        launch {
            startMsg()
            delay(someTime())
            println("Launch...")
            endMsg()
        }
    }
}

fun cRunBlocking() {
    newTopic("RunBlocking")
    runBlocking {
        startMsg()
        delay(someTime())
        println("RunBlocking...")
        endMsg()
    }
}

fun suspendFun() {
    newTopic("Suspend")
    Thread.sleep(someTime())
    //delay(someTime())
    GlobalScope.launch { delay(someTime()) }
}

fun globalScope() {
    newTopic("Global Scope")
    GlobalScope.launch {
        startMsg()
        delay(someTime())
        println("mi cortina")
        endMsg()
    }
}

fun startMsg() {
    println("--> Inicio corrutina -${Thread.currentThread().name}-")
}

fun endMsg() {
    println("--> Corrutina -${Thread.currentThread().name}- finalizada")
}
