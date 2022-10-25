package com.monksoft.fundamentoscorrutinas

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread
import kotlin.random.Random

fun main(){
    //lambda()
    //threads()
    //coroutinesVsThreads()
    secuences()
}

fun secuences() {
    newTopic("Secuences")
    getDatabySec().forEach { println("$it.") }
}

fun getDatabySec() : Sequence<Float>{
    return sequence {
        (1..5).forEach{
            println("procesando datos")
            Thread.sleep(someTime())
            yield(20+it+Random.nextFloat())
        }
    }
}

fun lambda() {
    newTopic("Lambda")
    //ejecucion normal
    println(multi(2,3))

    //ejecucion de lamba, funcion de orden superior
    println(multiLambda(2,3) { result ->
        print(result)
    })
}

fun threads() {
    newTopic("Threads")
    //manejo de hilos normal
    println("thread ${multiThread(2, 3)}")

    //con lambda
    multiThreadLambda(2,3) { result ->
        print("Thread+lambda $result")
    }
}

fun coroutinesVsThreads() {
    newTopic("Corrutinas vs Threads")

    runBlocking {
        (1..1000000).forEach {
            launch {
                delay(someTime())
                println("$it")
            }
        }
    }

//    (1..1000000).forEach{
//        thread{
//            Thread.sleep(someTime())
//            println("$it")
//        }
//    }
}

private const val SEPARATION = "===================="
fun newTopic(topic: String) {
    println("$SEPARATION $topic $SEPARATION")
}

fun multiThread(x: Int, y: Int): Int {
    var result = 0

    thread {
        Thread.sleep(someTime()) // aqui le damos un retardo para simular una respuesta de por ejemplo un servidor
        result = x * y
    }
    Thread.sleep(2000)//le volvemos a dar tiempo que seria lo que se va a demorar el tener respuesta del servidor
    //pero este valor puede ser mayor a 2 segundos que es lo que se parametrizo
    return result
}

fun multiThreadLambda(x: Int, y: Int, callback: (result: Int) -> Unit) {
    var result = 0

    thread {
        Thread.sleep(someTime()) // aqui le damos un retardo para simular una respuesta de por ejemplo un servidor
        result = x * y
        callback(result)
    }
}

fun someTime(): Long = Random.nextLong(500, 2000)

fun multi(x: Int, y: Int): Int? {
    return x*y
}

fun multiLambda(x: Int, y: Int, callback: (result: Int) -> Unit) {
    callback(x*y)
}

