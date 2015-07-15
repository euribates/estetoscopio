package es.euribates.estetoscopio;

/**
 * Created by juan on 12/7/15.
 */

class LogEntry {

    enum Level {
        INFO,
        WARNING,
        ERROR,
        PANIC,
    }

    String ip_address
    Level level
    Date timestamp
    String tag
    String message
    // def extra = [:]

    static Level parse_level(String s) {
        Level result
        switch (s) {
            case 'INFO': result = Level.INFO; break
            case 'WARNING': result = Level.WARNING; break
            case 'ERROR': result = Level.ERROR; break
            default: result = Level.PANIC
            }
        return result
        }

    LogEntry(String ip_address, String data) {
        this.ip_address = ip_address
        List l = data.tokenize(' ')
        switch (l[0]) {
            case 'INFO': this.level = Level.INFO; break
            case 'ERROR': this.level = Level.ERROR; break
            case 'WARNING': this.level = Level.WARNING; break
            default: this.level = Level.PANIC
        }
        String s = l[1].tokenize('T').join(' ')
        this.timestamp = new Date().parse('yyyy-d-M H:m:s', s)
        this.tag = l[2]
        this.message = l[3..-1].join(' ')
    }

    LogEntry(String ip_address, Level level, Date timestamp, String tag, String message) {
        this.ip_address = ip_address
        this.level = level
        this.timestamp = timestamp
        this.tag = tag
        this.message = message
    }

    static LogEntry info(String ip_address, Date timestamp, String tag, String message) {
        return new LogEntry(ip_address, Level.INFO, timestamp, tag, message)
    }

    String toString() {
        String ts =  timestamp.format("yyyy-d-M")+'T'+timestamp.format("H:m:s")
        return "${ip_address} ${level} ${ts} ${tag} ${message}"
    }
}


