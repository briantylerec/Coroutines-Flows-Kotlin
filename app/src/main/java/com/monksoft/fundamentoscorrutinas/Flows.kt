package com.monksoft.fundamentoscorrutinas

import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.random.Random

fun main(){
    //coldFlow()
    //cancelFlow()
    //flowOperators()
    //terminalFlowOperators()
    //bufferFlow()
    //confrationFlow()
    //multiFlow()
    //flatFlows()
    //flowExceptions()
    completions()
}

fun completions() {
    runBlocking{
        newTopic("flujo de un flujo (onCOmpletion)")
//        getMatchResultFlow()
//            .onCompletion { println("muestra algo proque ya se completo un flow") }
//            .catch{emit("Error: $this")}
//            .collect{println(it)}

        newTopic("cancelar flow")
        getDatabyFlowStatic()
            .onCompletion { println("ya no quiere el usuario") }
            .cancellable()
            .collect {
                if (it >22.5f) cancel()
                println(it)
            }
    }
}

fun flowExceptions() {
    runBlocking {
        newTopic("control de errores")
        newTopic("try/catch")
//        try {
//            getMatchResultFlow()
//                .collect {
//                    println(it)
//                    if (it.contains("2")) throw Exception("habian acrodado 1:1 :v")
//                }
//        } catch (e: Exception){
//            e.printStackTrace()
//        }

        newTopic("transparencia")
        getMatchResultFlow()
            .catch { it->
                emit("Error: $it")
            }
            .collect{
                println(it)
                if(!it.contains("-")) println("notificar al programador")
            }

    }
}

fun flatFlows() {
    runBlocking {
        newTopic("flujos de aplanamiento")

        newTopic("FlatMapConcat")
        getCitiesFlow()
            .flatMapConcat { cities ->
                getDataToFlatFlow(cities)
            }
            .map { setFormat(it) }
            //.collect{ println(it) }

        newTopic("FlatMapMerge")
        getCitiesFlow()
            .flatMapMerge { cities ->
                getDataToFlatFlow(cities)
            }
            .map { setFormat(it) }
            .collect{ println(it) }
    }
}

fun getDataToFlatFlow(city: String): Flow<Float> = flow {
    (1..3).forEach {
        println("Temperatura de ayer en $city")
        emit(Random.nextInt(10,30).toFloat())

        println("Temperatura actual en $city")
        delay(100)
        emit(20 + it +Random.nextInt().toFloat())
    }
}

fun getCitiesFlow() : Flow<String> = flow {
    listOf("Santander", "CDMX", "Lima")
        .forEach { city ->
            println("consultando ciudad")
            delay(1000)
            emit(city)
        }
}

fun multiFlow() {
    runBlocking {
        newTopic("zip y combine")
        getDatabyFlowStatic()
            .map{ setFormat(it) }
            //.zip(getMatchResultFlow()){ degrees, result ->
            .combine(getMatchResultFlow()){ degrees, result ->
                "$result with $degrees"
            }
            .collect{ println(it) }

    }
}

fun confrationFlow() {
    runBlocking {
        newTopic("Fusion")
        val time = getDatabyFlowStatic()
        getMatchResultFlow()
            .conflate()
            .collect{
                delay(100)
                println(it)
            }
        println("time $time")
        }
    }

fun getMatchResultFlow(): Flow<String> {
    return flow {
        var homeTeam = 0
        var awayTeam = 0
        (0..45).forEach {
            println("minuto: $it")
            delay(50)
            homeTeam += Random.nextInt(0,21)/20
            awayTeam += Random.nextInt(0,21)/20
            emit("$homeTeam - $awayTeam")
            if ( homeTeam == 2 || awayTeam == 2 ) throw Exception("habian acrodado 1:1 :v")
        }
    }
}

fun bufferFlow() {
    runBlocking {
        newTopic("buffer para flow")
        val time = getDatabyFlowStatic()
            .map { setFormat(it) }
            .buffer()
            .collect {
                delay(500)
                println(it)
            }
        println("time $time")
    }
}

fun getDatabyFlowStatic() : Flow<Float>{
    return flow {
        (1..5).forEach{
            println("procesando datos")
            delay(300)
            emit(20+it+ Random.nextFloat())
        }
    }
}

fun terminalFlowOperators() {
    runBlocking {
        newTopic("oepradores flow terminales")
        newTopic("list")
        val list = getDatabyFlow()
            //.toList()
        //print("list $list")

        newTopic("single")
        val single = getDatabyFlow()
//            .take(1)
//            .single()
        //print("single $single")

        newTopic("first")
        val first = getDatabyFlow()
            //.first()
        //print("first $first")

        newTopic("last")
        val last = getDatabyFlow()
            //.last()
        //print("last $last")

        newTopic("reduce")
        val reduce = getDatabyFlow()
            .reduce { acumulador, valor ->
                println("acumulador $acumulador")
                println("valor $valor")
                println("current $acumulador + $valor")
                acumulador + valor
            }
        //print("reduce $reduce")

        newTopic("fold")

        val lastSaving = reduce
        val totalSaving = getDatabyFlow()
            .fold(lastSaving) { acc, value ->
                println("acumulador $acc")
                println("valor $value")
                println("current $acc + $value")
                acc + value
            }
        print("totalSaving $totalSaving")

    }
}

fun flowOperators() {
    runBlocking {
        newTopic("Operadores intermediarios")
        newTopic("Map")
        getDatabyFlow()
            .map {
                //setFormat( it )
                convertCelsToFahr(it)
            }
            //.collect{ println(it) }

        newTopic("filter")
        getDatabyFlow()
            .filter {
                it < 23
            }
            .map {
                setFormat(it)
            }
            //.collect{ println(it)}
        newTopic("trnasform")
        getDatabyFlow()
            .transform {
                emit(setFormat(it))
                emit(setFormat(convertCelsToFahr(it), "F"))
            }
            //.collect{ println(it)}
        newTopic("take")
        getDatabyFlow()
            .take(3)
            .map {
                setFormat(it)
            }
            .collect{ println(it)}
    }
}

fun setFormat(temp: Float, degree: String = "C") : String {
    return String.format(Locale.getDefault(), "%.1f $degree", temp)
}

fun convertCelsToFahr(cels: Float): Float = (cels * 9 / 5) + 32

fun cancelFlow() {
    runBlocking {
        newTopic("cancel Floe")
        val job = launch {
            getDatabyFlow().collect{ print( it ) }
        }
        delay(someTime()*2)
        job.cancel()
    }
}

fun coldFlow() {
    newTopic("ColdFlow")
    runBlocking {
        val dataFlow = getDatabyFlow()
        println("Esperando")
        delay(10000)
        dataFlow.collect{ println(it) }
    }
}