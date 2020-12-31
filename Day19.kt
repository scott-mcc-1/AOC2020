package com.scott.aoc2020
import java.io.File

/***
 *
 *
 *      part1:
 *      part2:
 */

//Part1: 182
//Part2:                        355 too high (other input answer)

class Day19 {
    open class Input
    data class Message(val message:String) : Input()
    data class Rule(val num:Int,  val ab: String, val to: List<List<Int>>) : Input()

    private operator fun List<String>.component2() = this[1].toIntOrNull()
    private operator fun List<String>.component3() = this[2].toIntOrNull()
    private operator fun List<String>.component4() = this[3].toIntOrNull()
    private operator fun List<String>.component5() = this[4].toIntOrNull()
    private operator fun List<String>.component6() = this[5].toIntOrNull()
    private operator fun List<String>.component7() = this[6]
    private operator fun List<String>.component8() = this[7]

    private val input =
        File("""data\y2020\day19.txt""")
            .readLines()
            .mapNotNull{ line ->
                val (_,num, a, b, c, d, ch, message) =
                    Regex("""(?:(\d+): (?:(\d+)(?: (\d+))*(?: \| (\d+)(?: (\d+))*)*)*)*(?:"([ab])")*|([ab]+)""")
                            .matchEntire(line)!!
                            .groupValues
                when {
                    message != ""   -> Message(message)
                    num     == null -> null
                    else            -> Rule(num, ch, listOf(listOfNotNull(a, b), listOfNotNull(c, d)))
                }
            }
    private val rules    = input.filterIsInstance<Rule>().associateBy {it.num}.toSortedMap()
    private val messages = input.filterIsInstance<Message>().map{it.message}

    private fun validate(message:String, rulesToApply:List<Int>):Boolean {
        if (rulesToApply.isEmpty()) return message.isEmpty()
        val rule = rules[rulesToApply.first()]!!
        return if (rule.ab != "") message.startsWith(rule.ab) && validate(message.drop(1), rulesToApply.drop(1))
               else rule.to.any{ validate(message, it + rulesToApply.drop(1)) }
    }
    private fun getPattern(key:Int):String {
        val rule = rules[key]!!
        return if (rule.ab != "") rule.ab
                else rule.to.joinToString("|","(?:",")") { branch ->
                                     branch.joinToString(""){getPattern(it) } }
    }

    fun part1() {
        val pattern = getPattern(0).toRegex()
        println("Part1: Validate: ${messages.count{validate(it, listOf(0))}
                       }   RegEx: ${messages.count(pattern::matches)
        }")
    }
    fun part2() {
        // for validate
        rules[8] = Rule(8, "", listOf(listOf(42), listOf(42,8)))
        rules[11] = Rule(11, "", listOf(listOf(42,31), listOf(42,11,31)))

        // for Regex:
        val pattern31 = getPattern(31)
        val pattern42 = getPattern(42)

        val pattern0 = (1..4).joinToString("|") { i ->
            "(?:$pattern42){${i + 1},}(?:$pattern31){$i}"
        }.toRegex()

        // now compute
        println("Part2: Validate: ${messages.count{validate(it, listOf(0))}
                       }   RegEx: ${messages.count(pattern0::matches)
       
        }")
    }
}
