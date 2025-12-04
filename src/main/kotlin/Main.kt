import kotlin.math.*
import java.util.stream.IntStream

var width: Int = 0;
var length: Int = 0;
var nanos: Long = 0;
var sum: Double = 0.0;
val threadList = mutableListOf<Thread>();
val benchmark = mutableListOf<Long>();

suspend fun main(args: Array<String>) {

    length = readln().toInt();
    width = readln().toInt();

//                              8.29441036800288E20
    for (i in 0..10) {
        val startTime = System.nanoTime();
        //threadSep(12);   // 1200000 x 100000 <- матрица
// 5275.9946 ms 5276.2065 ms    50956.535 ms 6
// 3386.0022 ms 3383.0708 ms    26540.396 ms 12
// 2795.0254 ms 2693.6846 ms    23471.627 ms 24
//                              14822.52 ms 48
//                              12244.547 ms 96
//                              6131.7764 ms 192        При 1200000 х 1000000:    при 1200000x100000 ПАР   при 1200000x100000 12
//                              4476.197 ms 384           384   90203.4 ms          3.035784655675035E13    3.035788450408234E13
//                                                                                  9194.477 ms             26812.115 ms
//                              4758.289 ms 768

        println(multiThread(length, width));
        //threadList.forEach { it.join() }

        val endTime = System.nanoTime();
        nanos = max(nanos, endTime - startTime);
        benchmark += (nanos);
        println(sum);
        sum = 0.0;
    }
    println("\nРезультат бенчмарка.\nСредняя длительность вычисления: ${(benchmark.sum()/benchmark.size)/1_000_000F}")
}

fun multiThread(length: Int, width: Int): Double {
    return IntStream.range(0, length)
        .parallel()
        .mapToDouble { i ->
            var firstSum = 0.0
            val currentNum = sqrt(12.0 * i / 100.0)

            // Второй цикл — обязательно оставить
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
                nanos = max(nanos, endTime - startTime);
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
            currentNum = sqrt(12.0 * i /100.0);
            firstSum += currentNum;
        }
        secondSum += firstSum;
        firstSum = 0.0;
    }
    sum += secondSum;
    //println("$secondSum start: $start; end: $end");
}
