import static griffon.util.GriffonApplicationUtils.isMacOSX

log4j = {
    appenders {
        console name: 'stdout', layout: pattern(conversionPattern: '%d [%t] %-5p %c - %m%n'),threshold: org.apache.log4j.Level.DEBUG
        file name:'file', file:'smb_debug.log',  threshold: org.apache.log4j.Level.DEBUG
    }

    error  'org.codehaus.griffon'

    info   'griffon.util',
           'griffon.core',
           'griffon.swing',
           'griffon.app'

    debug  'griffon.app.controller',
           'griffon.app.service',
           'com.relivethefuture'
}

lookandfeel {
    if(!isMacOSX) {
        lookAndFeel = 'Pagosoft'
        theme = 'NativeColor'
    } else {
        lookAndFeel = 'system'
    }
}
