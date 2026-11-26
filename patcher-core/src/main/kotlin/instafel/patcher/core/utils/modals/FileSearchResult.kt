/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

package instafel.patcher.core.utils.modals

import java.io.File

sealed class FileSearchResult {
    data class Success(val file: File) : FileSearchResult()
    data class NotFound(
        val scannedFiles: Int,
        // .67 fix: the files that matched the coordinates, so a caller
        // can tie-break instead of failing blind. Empty when nothing
        // matched. (The `scannedFiles` name is historical — it carries
        // the matched count, not the scanned count.)
        val matchedFiles: List<File> = emptyList()
    ) : FileSearchResult()
}
