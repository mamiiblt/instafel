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
    data class NotFound(val scannedFiles: Int) : FileSearchResult()
}
