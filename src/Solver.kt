import java.io.File
import java.util.*

fun Array<IntArray>.isSolved(): Boolean{
    this.forEach {
        if (0 in it) return false
    }
    return true
}

fun Array<IntArray>.col(i: Int): List<Int>{
    var c = mutableListOf<Int>()
    this.forEach {
        c.add(it[i])
    }
    return c
}

fun subIndex(i: Int, j: Int): Pair<Int, Int>{
    var row = 0
    var col = 0
    when(j){
        in 0..2 -> col = 0
        in 3..5 -> col = 3
        in 6..8 -> col = 6
    }
    when(i){
        in 0..2 -> row = 0
        in 3..5 -> row = 3
        in 6..8 -> row = 6
    }
    return Pair(row, col)
}

fun Array<IntArray>.sub(i: Int, j: Int): List<Int>{
    val s = mutableListOf<Int>()
    val index = subIndex(i, j)
    var row = index.first
    var col = index.second

    repeat(3){ r ->
        repeat(3){ c->
            s.add(this[row+r][col+c])
        }
    }
    return s
}

fun Array<IntArray>.candidati(i: Int, j: Int): List<Int>{
    return (1..9).toList().minus( this[i] union this.col(j) union this.sub(i, j))
}

fun Array<IntArray>.candidatiInSub(i: Int, j: Int): Array<Array<List<Int>>>{
    val index = subIndex(i, j)
    var candidatiInTab = Array(3){ Array(3){ listOf(0)} }
    repeat(3){i->
        repeat(3){j->
            if (this[i+index.first][j+index.second] == 0)
                candidatiInTab[i][j] = (this.candidati(i+index.first, j+index.second))
        }
    }
    return candidatiInTab
}

fun Array<IntArray>.esclusione(i: Int, j: Int): List<Int>{
    val r = i%3
    val c = j%3
    val tot = this.candidatiInSub(i, j)
    var set = listOf(0).toSet()

    for (i in 0..2){
        for(j in 0..2){
            if (i != r || j != c) {
                set = set union tot[i][j]
            }
        }
    }
    return this.candidati(i, j).minus(set)
}

fun readTab(fileName: String): Array<IntArray>{
    val f = File(fileName)
    val scan = Scanner(f)

    val tab = Array(9) {IntArray(9)}

    for (i in 0 until 9){
        val line = scan.nextLine().split(" ")
        tab[i] = line.map { it.trim().toInt() }.toIntArray()
    }
    return tab
}

fun risolvi(tab: Array<IntArray>){

    val timer = System.currentTimeMillis()
    var counter = 0

    var continua = true
    while (continua){
        continua = false
        for (i in 0 until 9){
            for (j in 0 until 9){
                if (tab[i][j] == 0){
                    val candidato = tab.candidati(i, j)
                    if (candidato.size == 1){
                        continua = true
                        tab[i][j] = candidato[0]
                    }
                }
            }
        }

        if (!continua){
            for (i in 0 until 9) {
                for (j in 0 until 9) {
                    if (tab[i][j] == 0){
                        val e = tab.esclusione(i, j)
                        if (e.size == 1){
                            continua = true
                            tab[i][j] = e[0]
                        }
                    }
                }
            }
            counter += 1
        }
        counter += 1
    }



    tab.forEach{
        it.forEach{ print("$it ")}
        println()
    }
    println("Risolto: ${tab.isSolved()}")
    println("finito in ${System.currentTimeMillis()-timer} ms, $counter ripetizioni")
}


fun main(args: Array<String>) {
    if(args.size == 1)
        risolvi(readTab(args[0]))
    else{
        val tab = readTab("difficile1.sudoku")
        for (i in 0 until tab.size){
            for(j in 0 until tab[i].size){
                if (tab[i][j] == 0)
                    println("$i, $j: ${tab.candidati(i, j)}")
            }
        }
    }
}