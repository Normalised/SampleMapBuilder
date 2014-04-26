package drumkitbuilder2

import javax.swing.SwingConstants
import java.awt.Color

toolBar(id: 'toolbar', rollover: true,floatable:false,border:matteBorder(color:Color.BLACK,top:1,left:0,right:0,bottom:0)) {
    button(browseAction, text: null)
    button(scanAction, text: null)
    button(exportAction, text: null)
    separator(orientation: SwingConstants.VERTICAL)
    button(configureAction, text: null)
    button(helpAction, text: null)
}
