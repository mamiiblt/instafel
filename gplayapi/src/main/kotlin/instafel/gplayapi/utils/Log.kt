package instafel.gplayapi.utils

class Log {
    companion object {
        fun print(tag: String, msg: String) {
            print("$tag : $msg")
        }

        fun println(tag: String, msg: String) {
            println("$tag : $msg")
        }
    }
}