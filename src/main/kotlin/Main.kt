import kotlin.math.*
import java.util.stream.IntStream

var width: Int = 0;
var length: Int = 0;
var nanos: Long = 0;
var sum: Double = 0.0;
val threadList = mutableListOf<Thread>();
val benchmark = mutableListOf<Long>();

val threadTimings = mutableMapOf<Int, Long>();
var numberOfThreads = 12;

fun main(args: Array<String>) {

    length = readln().toInt();
    width = readln().toInt();

    var parallelMode = true;

    var choice: String;

    println("Введите режим: manual/parallel")
    choice = readln();

    while(true) {
        when (choice) {
            "manual" -> {
                parallelMode = false;
                print("Введите количество потоков > ");
                numberOfThreads = readln().toInt();
                break;
            }
            "parallel" -> {
                parallelMode = true; break;
            }
            else -> println("Неверный ввод")
        }
    }
println("Будет произведено 10 прогонов, по итогам которых выявится среднее время выполнения");
//                              8.29441036800288E20
    for (i in 1..10) {
        var startTime: Long;
        var endTime: Long;

        if (parallelMode) {
            startTime = System.nanoTime();
            println(multiThread(length, width));
            endTime = System.nanoTime();
        }
        else {
            startTime = System.nanoTime();
            threadSep(numberOfThreads);   // 1200000 x 100000 <- матрица
            threadList.forEach { it.join() }

            endTime = System.nanoTime();
        }
        nanos = max(nanos, endTime - startTime);
        benchmark += (nanos);
        println(sum);
        sum = 0.0;

        for (i in 1..numberOfThreads) {
            //println("Затраченное время на поток $i: ${threadTimings[i]?.div(1_000_000F)}")
        }
    }
    println("\nРезультат бенчмарка.\nСредняя длительность вычисления: ${(benchmark.sum()/benchmark.size)/1_000_000F} ms")
}

fun multiThread(length: Int, width: Int): Double {
    return IntStream.range(0, length)
        .parallel()
        .mapToDouble { i ->
            var firstSum = 0.0
            val currentNum = Math.pow(12.0 * i / 100.0, 2.0)

            for (j in 0 until width) {
                firstSum += currentNum
            }

            firstSum
        }
        .sum()
}
fun threadSep (threads: Int) {

    var startPos: Int;
    var endPos = 0;
    if (length % threads == 0) {
        for (i in 1..threads) {
            startPos = endPos+1;
            endPos = (length / threads) * i;

            val e = endPos; val s = startPos;
            val task = Runnable {
                val startTime = System.nanoTime();
                threadTask(s, e);
                val endTime = System.nanoTime();
                threadTimings.put(i, endTime - startTime);

            }
            val t = Thread(task);
            t.start();
            threadList += t;
        }
    }
}

fun threadTask(start: Int, end: Int)
{
    var firstSum = 0.0;
    var secondSum = 0.0;
    var currentNum: Double;
    for (i in start..end)
    {
        for (j in 1..width)
        {
            currentNum = Math.pow(12.0 * i /100.0, 2.0);
            firstSum += currentNum;
        }
        secondSum += firstSum;
        firstSum = 0.0;
    }
    sum += secondSum;
    //println("$secondSum start: $start; end: $end");
}

// 26862.97 ms 12 потоков
// 8936.113 ms parallel