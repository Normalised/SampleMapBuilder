application {
    title = 'Sample Map Builder'
    startupGroups = ['drumKitBuilder2']

    // Should Griffon exit when no Griffon created frames are showing?
    autoShutdown = true

    // If you want some non-standard application class, apply it here
    //frameClass = 'javax.swing.JFrame'
}
mvcGroups {
    // MVC Group for "exportProgress"
    'exportProgress' {
        model      = 'drumkitbuilder2.ExportProgressModel'
        view       = 'drumkitbuilder2.ExportProgressView'
        controller = 'drumkitbuilder2.ExportProgressController'
    }

    // MVC Group for "formatOptions"
    'formatOptions' {
        model      = 'drumkitbuilder2.FormatOptionsModel'
        view       = 'drumkitbuilder2.FormatOptionsView'
        controller = 'drumkitbuilder2.FormatOptionsController'
    }

    // MVC Group for "drumKitBuilder2"
    'drumKitBuilder2' {
        model      = 'drumkitbuilder2.DrumKitBuilder2Model'
        view       = 'drumkitbuilder2.DrumKitBuilder2View'
        controller = 'drumkitbuilder2.DrumKitBuilder2Controller'
    }

    // MVC Group for "about"
    'about' {
        model      = 'drumkitbuilder2.AboutModel'
        view       = 'drumkitbuilder2.AboutView'
        controller = 'drumkitbuilder2.AboutController'
    }
}
