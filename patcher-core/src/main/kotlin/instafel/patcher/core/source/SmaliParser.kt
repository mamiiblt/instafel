package instafel.patcher.core.source

import instafel.patcher.core.utils.modals.SmaliInstruction

object SmaliParser {
    @Throws(IllegalArgumentException::class)
    fun parseInstruction(line: String, num: Int): SmaliInstruction {
        val parts = line.trim().split("\\s+".toRegex(), limit = 2)
        require(parts.size >= 2) { "Invalid smali format: ${line.trim()}" }

        val opcode = parts[0]
        val remainder = parts[1]

        val braceCloseIndex = remainder.indexOf('}')
        require(braceCloseIndex != -1) { "Invalid register format: ${line.trim()}" }

        val registersPart = remainder.take(braceCloseIndex + 1)
        val methodPart = remainder.substring(braceCloseIndex + 2)

        val registersContent = registersPart.substring(
            registersPart.indexOf('{') + 1,
            registersPart.indexOf('}')
        )
        val registers = registersContent.split("\\s*,\\s*".toRegex()).toTypedArray()

        val arrowIndex = methodPart.indexOf("->")
        require(arrowIndex != -1) { "Invalid method format: ${line.trim()}" }

        val className = methodPart.take(arrowIndex)
        val methodAndReturn = methodPart.substring(arrowIndex + 2)

        val paramsStart = methodAndReturn.indexOf('(')
        val paramsEnd = methodAndReturn.indexOf(')')
        require(paramsStart != -1 && paramsEnd != -1 && paramsEnd > paramsStart) {
            "Invalid method parameter format: ${line.trim()}"
        }

        val methodName = methodAndReturn.take(paramsStart)
        val returnType = methodAndReturn.substring(paramsEnd + 1)

        return SmaliInstruction(
            opcode.trim(),
            registers,
            className.trim(),
            methodName,
            returnType.trim(),
            num
        )
    }

}