/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

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