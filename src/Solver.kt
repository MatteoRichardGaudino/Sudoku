import java.io.File
import java.util.*

fun Array<IntArray>.isSolved(): Boolean{
    this.forEach {
        if (0 in it) return false
    }
    return this.check()
}

// controlla che in un array non ci siano duplicati a parte 0
fun IntArray.check(): Boolean{
    return this.filter { it != 0 }.distinct().size == this.filter { it != 0 }.size
}

// controlla che in una colonna/riga/sezione non ci siano duplicati
fun Array<IntArray>.check(): Boolean{
    // per le righe
    this.forEach {
        if (!it.check()) return false
    }
    // per le colonne
    repeat(9){i ->
        if (
                !IntArray(9){
                 this[it][i];
                }.check()
        ) return false
    }
    // per le sezioni
    repeat(3){i ->
        repeat(3){j ->
            if (!this.sub(i*3, j*3).check())
                return false
        }
    }
    return true
}

// restituisce la colonna i esima
fun Array<IntArray>.col(i: Int): List<Int>{
    var c = mutableListOf<Int>()
    this.forEach {
        c.add(it[i])
    }
    return c
}

// dato una posizione nel riferimento della tabella restituisce l'indice nel riferimento della sezione
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

// restituisce la sezione in cui si trova ij
fun Array<IntArray>.sub(i: Int, j: Int): IntArray{
    val s = mutableListOf<Int>()
    val index = subIndex(i, j)
    var row = index.first
    var col = index.second

    repeat(3){ r ->
        repeat(3){ c->
            s.add(this[row+r][col+c])
        }
    }
    return s.toIntArray()
}

fun Array<IntArray>.candidati(i: Int, j: Int): List<Int>{
    return (1..9).toList().minus( this[i] union this.col(j) union this.sub(i, j).toList())
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

fun Array<IntArray>.copia(a: Array<IntArray>){
    repeat(9){i->
        repeat(9){j->
            this[i][j] = a[i][j]
        }
    }
}
// riduzione ad assurdo
fun Array<IntArray>.raa(){
    // fa una copia della tabella originale
    val tryTab = Array(9){IntArray(9){0}}
    // trova il primo 0 e lo sostituisce con uno dei candidati
    var continua = true
    var pos = Pair(0, 0)
    while (continua && pos.first < 9){
        val index = this[pos.first].indexOf(0)
        if (index != -1){
            continua = false
            pos = Pair(pos.first, index)
        } else pos = Pair(pos.first+1, index)
    }
    if (pos.first in 0..8 && pos.second in 0..8){
        val candidati = this.candidati(pos.first, pos.second)
        var risolto = false
        var i = 0
        // finche non è risolto prova tutti i candidati, almeno uno deve essere giusto eh!!
        while (!risolto && i < candidati.size){
            tryTab.copia(this)
            tryTab[pos.first][pos.second] = candidati[i]
            risolto = risolvi(tryTab)
            i++
        }
        if (risolto) {
            // copia il risultato nella tabella originale
            this.copia(tryTab)
        }
    }

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

fun risolvi(tab: Array<IntArray>): Boolean{

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
    if (!tab.isSolved() && tab.check()){
        tab.raa()
    }

    println("Steps: $counter")
    return tab.isSolved();
}


fun main(args: Array<String>) {
    if(args.size == 1) {
        val timer = System.currentTimeMillis()
        val tab = readTab(args[0])
        val solved = risolvi(tab)
        tab.forEach {
            it.forEach { print("$it ") }
            println()
        }
        val risolto = solved
        println("Risolto: $risolto")
        println("finito in ${System.currentTimeMillis()-timer} ms")
    }
    else{
        val tab = readTab("everest.sudoku")
        val timer = System.currentTimeMillis()
        val risolto = risolvi(tab)

        tab.forEach{
            it.forEach{ print("$it ")}
            println()
        }
        println("Corretto: ${tab.check()}")
        println("Risolto: $risolto")
        println("finito in ${System.currentTimeMillis()-timer} ms")
    }
}