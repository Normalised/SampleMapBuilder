package com.relivethefuture.dkb

import griffon.core.GriffonApplication
import griffon.util.ApplicationHolder

import javax.swing.JFileChooser

import static griffon.util.GriffonNameUtils.isBlank

final class DKBUtils {

    static File selectDirectory(File location, String name = null) {
        GriffonApplication app = ApplicationHolder.application
        if (isBlank(name)) name = app.getMessage('application.dialog.Open.title', 'Open')
        JFileChooser fc = new JFileChooser(location)
        fc.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
        fc.acceptAllFileFilterUsed = true
        fc.dialogTitle = name
        fc.setMultiSelectionEnabled(false);
        int returnVal = fc.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile();
        }

        return null
    }
}
