import grails.util.BuildSettings
import grails.util.Environment
import org.springframework.boot.logging.logback.ColorConverter
import org.springframework.boot.logging.logback.WhitespaceThrowableProxyConverter

import java.nio.charset.Charset

conversionRule 'clr', ColorConverter
conversionRule 'wex', WhitespaceThrowableProxyConverter

// See http://logback.qos.ch/manual/groovy.html for details on configuration
appender('STDOUT', ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        charset = Charset.forName('UTF-8')

        /*
        I find the default pattern overkill/too wide in development and want something
        slimmed down, like Grails 2.x-style console output

        -Rinehart
         */
        if ( Environment.isDevelopmentMode() ) {
            pattern =
                    '%clr(%d{HH:mm:ss.SSS}){faint} ' + // Date
                            '%clr(%-40.40logger{0}){cyan} %clr(:){faint} ' + // Logger
                            '%m%n%wex' // Message
        } else {
            pattern =
                    '%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} ' + // Date
                            '%clr(%5p) ' + // Log level
                            '%clr(---){faint} %clr([%15.15t]){faint} ' + // Thread
                            '%clr(%-40.40logger{39}){cyan} %clr(:){faint} ' + // Logger
                            '%m%n%wex' // Message
        }
    }
}

def targetDir = BuildSettings.TARGET_DIR
if (Environment.isDevelopmentMode() && targetDir != null) {
    appender("FULL_STACKTRACE", FileAppender) {
        file = "${targetDir}/stacktrace.log"
        append = true
        encoder(PatternLayoutEncoder) {
            pattern = "%level %logger - %msg%n"
        }
    }
    logger("StackTrace", ERROR, ['FULL_STACKTRACE'], false)
}

/*
Development? Tell me stuff. Not development? Quiet.

-Rinehart
 */
if ( Environment.isDevelopmentMode() ) {
    root(INFO, ['STDOUT'])
} else {
    root(ERROR, ['STDOUT'])
}