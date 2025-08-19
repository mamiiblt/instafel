package instafel.patcher.cli.utils

import java.util.logging.*

object Log {
    val LOGGER: Logger = Logger.getLogger(Log::class.java.name)

    fun setupLogger() {
        val logger = Logger.getLogger("")
        for (handler: Handler in logger.handlers) {
            logger.removeHandler(handler)
        }
        LogManager.getLogManager().reset();

        val handler = object : Handler() {
            override fun publish(record: LogRecord?) {
                if (formatter == null) {
                    formatter = (object : Formatter() {
                        override fun format(record: LogRecord): String {
                            return (record.level.toString()[0].toString() + ": "
                                    + record.message
                                    + System.lineSeparator())
                        }
                    })
                }

                try {
                    val message = formatter.format(record)
                    if (record!!.level.intValue() >= Level.WARNING.intValue()) {
                        System.err.write(message.toByteArray())
                    } else {
                        if (record.level.intValue() >= Level.INFO.intValue()) {
                            System.out.write(message.toByteArray())
                        }
                    }

                } catch (e: Exception) {
                    reportError(null, e, ErrorManager.FORMAT_FAILURE)
                }
            }

            override fun flush() { }
            override fun close() { }
        }

        logger.addHandler(handler)
        handler.level = Level.ALL
        logger.level = Level.ALL
    }

    fun info(msg: String?) {
        LOGGER.info(msg)
    }

    fun warning(msg: String?) {
        LOGGER.warning(msg)
    }

    fun severe(msg: String?) {
        LOGGER.severe(msg)
    }
}