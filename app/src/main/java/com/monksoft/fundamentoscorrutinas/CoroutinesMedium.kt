package com.monksoft.fundamentoscorrutinas

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

fun main(){
    //dispatchers()
    //nested()
    //changeWithContext()
    basicFlow()
}

fun basicFlow() {
    newTopic("Flows Basicos")
    runBlocking {
        launch { getDatabyFlow().collect { println(it) } }
        launch {
            (1..50).forEach{
                delay(someTime())
                println("tarea 2 $it")
            }
        }
    }

}

fun getDatabyFlow() : Flow<Float>{
    return flow {
        (1..5).forEach{
            println("procesando datos")
            delay(someTime())
            emit(20+it+ Random.nextFloat())
        }
    }
}

fun changeWithContext() {
    runBlocking {
        newTopic("withCOntext")
        startMsg()
        withContext(newSingleThreadContext("Cursos")) {
            startMsg()
            delay(someTime())
            println("tarea cursos")
            endMsg()
        }
        endMsg()
    }
}

fun nested() {
    runBlocking {
        newTopic("Anidar")
        val job = launch {
            startMsg()
            launch {
                startMsg()
                delay(someTime())
                println("Otra tarea")
                endMsg()
            }

            launch (Dispatchers.IO) {
                startMsg()
                launch(newSingleThreadContext("Cursos")) {
                    startMsg()
                    println("tarea cursos")
                    endMsg()
                }
                this.cancel()
                println("cancelado")
                delay(someTime())
                println("tarea en el servidor")
                endMsg()

            }
            var sum = 0
            (1..100).forEach {
                sum += it
                delay(someTime()/100)
            }
            println("suma: $sum")
            endMsg()
        }
        delay(someTime()/2)
        job.cancel()
        println("job cancelado...")
    }
}

fun dispatchers() {
    runBlocking {
        newTopic("Distpacher")
        launch {
            startMsg()
            println("NONE")
            endMsg()
        }
        launch (Dispatchers.IO) {
            startMsg()
            println("IO")
            endMsg()
        }
        launch (Dispatchers.Unconfined) {
            startMsg()
            println("Unconfined")
            endMsg()
        }
        //main solo para android
//        launch (Dispatchers.Main) {
//            startMsg()
//            println("Main")
//            endMsg()
//        }
        launch (Dispatchers.Default) {
            startMsg()
            println("Default")
            endMsg()
        }
        //personalizado 1
        launch (newSingleThreadContext("Cursos Android")) {
            startMsg()
            println("Mi Corrutina personalizada")
            endMsg()
        }
        //personalizado 2
        newSingleThreadContext("Cursos Android").use { myContext ->
            launch (myContext) {
                startMsg()
                println("Mi Corrutina personalizada 2")
                endMsg()
            }
        }
    }
}
