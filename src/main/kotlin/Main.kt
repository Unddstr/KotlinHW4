import java.io.File

fun main() {
    var flag = true

    while (flag) {
        val command: Command = readCommand()
        if (command.isValid()) {
            when (command) {
                is Exit -> flag = false
                is Help -> Help.printInstruction()
                is Export -> Export.exportJSON()
                is Add -> {
                    if (phonebook.any { it.name == command.message[1] }) {
                        if (command.isValidPhone()) {
                            for (person in phonebook) {
                                if (person.name == command.message[1]) {
                                    person.phone.add(command.message[3])
                                }
                            }
                        } else if (command.isValidEmail()) {
                            for (person in phonebook) {
                                if (person.name == command.message[1]) {
                                    person.email.add(command.message[3])
                                }
                            }
                        }
                    } else {
                        if (command.isValidPhone()) {
                            phonebook.add(
                                Person(
                                    name = command.message[1],
                                    phone = arrayListOf(command.message[3]),
                                    email = arrayListOf()
                                )
                            )
                        } else if (command.isValidEmail()) {
                            phonebook.add(
                                Person(
                                    name = command.message[1],
                                    phone = arrayListOf(),
                                    email = arrayListOf(command.message[3])
                                )
                            )
                        }
                    }
                }

                is Show -> {
                    if (phonebook.any { it.name == command.message[1] }) {
                        println(phonebook.filter { it.name == command.message[1] })
                    } else {
                        println("Запись не найдена")
                    }

                }

                is Find -> {
                    if (command.message[1].matches(Regex("[0-9]+"))) {
                        println(phonebook.filter { it.phone.contains(command.message[1]) })
                    } else if (command.message[1].matches(Regex("[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}"))) {
                        println(phonebook.filter { it.email.contains(command.message[1]) })
                    } else {
                        Help.printInstruction()
                    }
                }
            }
        } else {
            Help.printInstruction()
        }
    }
}

val phonebook = ArrayList<Person>()

fun readCommand(): Command {
    val message = readln().split(" ")
    return when (message[0]) {
        "exit" -> Exit(message)
        "export" -> Export(message)
        "help" -> Help(message)
        "show" -> Show(message)
        "find" -> Find(message)
        "add" -> Add(message)
        else -> Help(listOf("help"))
    }
}

sealed interface Command {
    fun isValid(): Boolean
}

class Exit(private val message: List<String>) : Command {
    override fun isValid(): Boolean {
        return message.size == 1 && message[0] == "exit"
    }
}

class Export(private val message: List<String>) : Command {
    override fun isValid(): Boolean {
        return message.size == 1 && message[0] == "export"
    }

    companion object {
        fun exportJSON() {
            File("src/main/kotlin/file.txt").writeText(phonebook.toString())
        }
    }
}

class Help(private val message: List<String>) : Command {
    override fun isValid(): Boolean {
        return message.size == 1 && message[0] == "help"
    }

    companion object {
        fun printInstruction() {
            println("Инструкция...")
        }
    }
}

class Show(val message: List<String>) : Command {
    override fun isValid(): Boolean {
        return message.size == 2 && message[0] == "show"
    }
}

class Find(val message: List<String>) : Command {
    override fun isValid(): Boolean {
        return message.size == 2 && message[0] == "find"
    }
}

class Add(val message: List<String>) : Command {
    override fun isValid(): Boolean {
        return message.size == 4
                && message[0] == "add"
                && (message[2] == "phone"
                || message[2] == "email")
                && (isValidPhone()
                || isValidEmail())
    }

    fun isValidPhone(): Boolean {
        return message[3].matches(Regex("[0-9]+"))
    }

    fun isValidEmail(): Boolean {
        return message[3].matches(Regex("[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}"))
    }
}
