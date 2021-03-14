import java.awt.Color
import java.awt.GridLayout
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.io.File
import java.io.FileOutputStream
import java.util.*
import javax.swing.*

class CreaTab: JFrame("Crea Tabella") {

    val gui = TabGui()
    val field = Array(9){Array(9){JTextField(1)} }
    var i = 0
    var j = 0

    init {
        setSize(400, 600)
        defaultCloseOperation = 3


        gui.panel1.layout = GridLayout(9, 9)

        field.forEachIndexed {i, f->
            f.forEachIndexed {j, it->
                if ((j in 3..5 && (i in 0..2 || i in 6..8))
                        || ((j in 0..2 || j in 6..8) && i in 3..5)){
                    it.background = Color.lightGray
                }
                gui.panel1.add(it)
            }
        }
        contentPane = gui.content

        with(gui){
            numeri.addKeyListener(object : KeyListener{
                override fun keyTyped(e: KeyEvent?) {}

                override fun keyPressed(e: KeyEvent) {
                    field[i][j].text = e.keyChar.toString()
                    numeri.text = ""
                    next()
                }

                override fun keyReleased(e: KeyEvent?) {}

            })

            cancella.addActionListener {
                field.forEach { it.forEach { it.text = " " } }
                i = 0
                j = 0
            }

            salva.addActionListener {
                val nome = nomeFile.text
                var s = ""
                field.forEach {
                    it.forEachIndexed{ i, it ->
                        if (it.text.trim() == "") s += "0"
                        else s += it.text.trim()

                        if (i < 8) s += " "
                    }
                    s += "\n"
                }

                val f = File(nome + ".sudoku")
                if (f.exists()){
                    JOptionPane.showMessageDialog(null, "Il file è gia esistente", "Errore",
                    JOptionPane.WARNING_MESSAGE)
                } else{
                    f.createNewFile()
                    val out = FileOutputStream(f)
                    out.write(s.map { it.toByte() }.toByteArray())
                    out.close()
                }
            }

            risolviButton.addActionListener {
                val tab = field.map { it.map {
                    if (it.text.trim() == "") 0
                    else it.text.trim().toInt()
                }.toIntArray() }.toTypedArray()

                risolvi(tab)
                riempi(tab)
            }

            apriButton.addActionListener {
                val jfc = JFileChooser()
                jfc.showOpenDialog(null)

                val tab = readTab(jfc.selectedFile.name)

                riempi(tab)
            }
        }

        isVisible = true
    }

    private fun riempi(tab: Array<IntArray>){
        for (i in 0 until 9){
            for (j in 0 until 9){
                if(tab[i][j] != 0)
                    field[i][j].text = tab[i][j].toString()
            }
        }
    }

    private fun next(){
        j += 1
        if (j > 8){
            j = 0
            i += 1
        }

        if (i > 8){
            i = 0
            j = 0
        }
    }
}

fun main(args: Array<String>) {
    CreaTab()
}