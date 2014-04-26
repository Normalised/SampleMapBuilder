package drumkitbuilder2

actions {
    action(scanAction, smallIcon: silkIcon('package_add'),toolTipText: "ReScan Source Folder",id:"scanAction",enabled:false)
    action(browseAction, smallIcon: silkIcon('folder'),toolTipText: "Browse for Source Folder")
    action(exportAction, smallIcon: silkIcon('package_go'),toolTipText: "Export",id:'exportAction',enabled:false)
    action(configureAction, smallIcon: silkIcon('wrench'),toolTipText: "Configuration")
    action(helpAction, smallIcon: silkIcon('help'),toolTipText:"Help")
}