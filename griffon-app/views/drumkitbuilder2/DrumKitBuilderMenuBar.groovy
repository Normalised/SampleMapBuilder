package drumkitbuilder2

import javax.swing.event.PopupMenuListener

import static griffon.util.GriffonApplicationUtils.getIsMacOSX

menuBar = menuBar {
    menu(text: app.getMessage('application.menu.File.name', ' File'),
            mnemonic: app.getMessage('application.menu.File.mnemonic', 'F')) {
        menuItem(browseAction)
        menuItem(configureAction)
        menuItem(exportAction)
        menu(id: 'openRecentMenu', enabled: false,
                text: app.getMessage('application.menu.OpenRecent.name', ' Open Recent...'),
                mnemonic: app.getMessage('application.menu.OpenRecent.mnemonic', 'E'))
        separator()
        if (!isMacOSX) {
            separator()
            menuItem(quitAction)
        }
    }

    if (!isMacOSX) glue()
    menu(text: app.getMessage('application.menu.Help.name', 'Help'),
            mnemonic: app.getMessage('application.menu.Help.mnemonic', 'H')) {
        if (!isMacOSX) {
            menuItem(aboutAction)
        }
        menuItem(helpAction)
    }
}

openRecentMenu.popupMenu.addPopupMenuListener([
        popupMenuWillBecomeVisible: {
            println openRecentMenu
            openRecentMenu.removeAll()
            model.recentFolders.eachWithIndex { file, int i ->
                openRecentMenu.add(action(
                        name: "${i + 1}. ${file.name}".toString(),
                        mnemonic: 1 + i,
                        closure: { controller.scan(file) }
                ))
            }
            if (model.recentScripts.size()) {
                openRecentMenu.add(separator())
                openRecentMenu.add(clearRecentFoldersAction)
            }
        },
        popupMenuWillBecomeInvisible: {/*empty*/},
        popupMenuCanceled: {/*empty*/}
] as PopupMenuListener)

return menuBar