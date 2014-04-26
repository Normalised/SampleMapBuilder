package drumkitbuilder2

import com.relivethefuture.formats.SamplerFormat

import javax.swing.UIManager
import java.awt.Font

Font labelFont = UIManager.getDefaults().getFont("Label.font")
labelFont = labelFont.deriveFont(Font.BOLD)

actions {
    action(id: 'cancelAction',
       name: 'Cancel',
       closure: controller.hide,
       mnemonic: 'C',
       shortDescription: 'Cancel'
    )
    action(id: 'okAction',
       name: 'Apply',
       closure: controller.okClicked,
       mnemonic: 'K',
       shortDescription: 'Apply'
    )
}

def createPathChooser(name, noActive = false, labelText = "Output") {

    if(!noActive) {
        label(text:labelText,constraints: "gap para")
        checkBox(id:'activeBox' + name)
    } else {
        label(text:labelText,constraints: "gap para, span 2")
    }
    button(icon: silkIcon('folder'),id:'browse' + name, actionPerformed:{controller.choosePath(name)})
    textField(columns: 80, id:'pathText' + name, text:"", constraints:"span,growx,wrap")
}

panel(id: 'content') {
    borderLayout()
    panel(constraints: CENTER) {
        migLayout(layoutConstraints: '',columnConstraints:"",rowConstraints:"")
        label(text:"Layout", font:labelFont, constraints:"gap para")
        comboBox(id:'layoutSelector', model: eventComboBoxModel(source: model.layoutSelectOptions),
                actionPerformed: controller.layoutSelected,
                selectedItem: bind(target:model, targetProperty:'selectedLayout',mutual:true),constraints: "span 2"
        )
        label(text:"Filename Depth",constraints: "gap para")
        comboBox(id:'filenameDepth', model: eventComboBoxModel(source: model.filenameDepthOptions),
                selectedItem: bind(target:model, targetProperty:'selectedFilenameDepth',mutual:true))
        label(text:"Output Structure Depth",constraints: "gap para")
        comboBox(id:'outputDepth', model: eventComboBoxModel(source: model.outputDepthOptions),
                selectedItem: bind(target:model, targetProperty:'selectedOutputDepth',mutual:true),constraints: "wrap")
        separator(constraints:"span,growx,wrap")
        // Base
        label(text:"Base",font:labelFont, constraints:"wrap")
        createPathChooser('base', true)
        separator(constraints:"span,growx,wrap")

        // Samples
        label(text:"Samples",font:labelFont, constraints:"wrap")
        createPathChooser('samples')
        separator(constraints:"span,growx,wrap")

        // Reaktor
        label(text:"Reaktor",font:labelFont, constraints:"wrap")
        label(text:"Fixed Root",constraints:"gap para")
        checkBox(selected: bind(target: model, 'reaktorFixedRoot',mutual:true))
        textField(columns:4,text:bind(target:model,'reaktorRootNote',mutual:true))
        label(text:"Note (0-127)",constraints:"wrap")
        createPathChooser('reaktor')
        separator(constraints:"span,growx,wrap")

        // Shortcircuit
        label(text:"Short Circuit",font:labelFont, constraints:"wrap")
        createPathChooser('shortcircuit')
        separator(constraints:"span,growx,wrap")

        // Renoise
        label(text:"Renoise",font:labelFont, constraints:"wrap")
        createPathChooser('renoise')
        separator(constraints:"span,growx,wrap")

        // SFZ
        label(text:"SFZ",font:labelFont, constraints:"wrap")
        createPathChooser('sfz')
        separator(constraints:"span,growx,wrap")

        // Live
        label(text:"Ableton Live",constraints:"wrap",font:labelFont)
        createPathChooser('rackSamplers',false,"Rack Sampler")
        createPathChooser('rackSimplers', false, "Rack Simpler")
        createPathChooser('samplerZones', false, "Sampler")

    }
    panel(constraints: SOUTH) {
        gridLayout(cols: 2, rows: 1)
        button(cancelAction)
        button(okAction)
    }
    
    keyStrokeAction(component: current,
        keyStroke: "ESCAPE",
        condition: "in focused window",
        action: cancelAction)
}
