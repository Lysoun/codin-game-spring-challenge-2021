fun readLineToInt(): Int = readLine()!!.toInt()

fun readLineToInts(): List<Int> = readLine()!!.split(" ").map { it.toInt() }

data class Cell(
    var richness: Int,
    val neighbours: List<Int>
)

fun readCells(numberOfCells: Int): Map<Int, Cell> {
    val cells = HashMap<Int, Cell>()

    for(i in 0 until numberOfCells) {
        val values = readLineToInts()
        cells[values[0]] = Cell(values[1], values.subList(2, 8))
    }

    return cells
}

data class Tree(
    val index: Int,
    var size: Int,
    val isMine: Boolean,
    val isDormant: Boolean
)

abstract class Action {
    fun printAction() {
        println(toString())
    }

    abstract override fun toString(): String

    fun performActionFluent(trees: MutableMap<Int, Tree>): Action {
        performAction(trees)
        return this
    }

    abstract fun performAction(trees: MutableMap<Int, Tree>)
}

class Wait: Action() {
    override fun toString() = "WAIT"

    override fun performAction(trees: MutableMap<Int, Tree>) {

    }
}

abstract class ActionOnTree(protected val index: Int, private val keyWord: String): Action() {
    override fun toString(): String = "$keyWord $index"
}

class Complete(index: Int): ActionOnTree(index, "COMPLETE") {
    override fun performAction(trees: MutableMap<Int, Tree>) {
        trees.remove(index)
    }
}

class Grow(index: Int): ActionOnTree(index, "GROW") {
    override fun performAction(trees: MutableMap<Int, Tree>) {
        ++trees[index]!!.size
    }
}

data class Turn(
    val day: Int,
    val nutrients: Int,
    val sun: Int,
    val score: Int,
    val oppSun: Int,
    val oppScore: Int,
    val oppIsWaiting: Boolean,
    val trees: MutableMap<Int, Tree>
)

fun readTurn(): Turn {
    val day = readLineToInt() // the game lasts 24 days: 0-23
    val nutrients = readLineToInt() // the base score you gain from the next COMPLETE action

    var values = readLineToInts()
    val sun = values[0] // your sun points
    val score = values[1] // your current score

    values = readLineToInts()
    val oppSun = values[0] // opponent's sun points
    val oppScore = values[1] // opponent's score
    val oppIsWaiting = values[2] != 0 // whether your opponent is asleep until the next day

    val numberOfTrees = readLineToInt() // the current amount of trees

    val trees = List(numberOfTrees) {
        values = readLineToInts()
        Tree(values[0], values[1], values[2] != 0, values[3] != 0)
    }.map { it.index to it }.toMap().toMutableMap()

    return Turn(day, nutrients, sun, score, oppSun, oppScore, oppIsWaiting, trees)
}


fun chooseAction(cells: Map<Int, Cell>, turn: Turn): Action {
    if(turn.sun >= 4) {
        val treeToComplete = turn.trees
            .filter { it.value.isMine }
            .filter { !it.value.isDormant }
            .map { it.key to cells[it.key]!!.richness }
            .maxBy { it.second }!!
            .first
        return Complete(treeToComplete)
    } else {
        return Wait()
    }
}

fun main(args: Array<String>) {
    val numberOfCells = readLineToInt() // 37
    val cells = readCells(numberOfCells)

    // game loop
    while (true) {
        val turn = readTurn()

        // Ignore further input because I don't care
        repeat((0 until readLineToInt()).count()) { readLine() }

        chooseAction(cells, turn).performActionFluent(turn.trees).printAction()
    }
}