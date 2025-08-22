package instafel.patcher.core.utils.modals

class SmaliInstruction(
    val opcode: String,
    val registers: Array<String>,
    val className: String,
    val methodName: String,
    val returnType: String,
    val num: Int
) {
    override fun toString(): String {
        return """
        Opcode: $opcode
        Registers: ${registers.joinToString(", ")}
        Class Name: $className
        Method Name: $methodName
        Return Type: $returnType
    """.trimIndent()
    }
}