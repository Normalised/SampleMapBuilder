package drumkitbuilder2

import ca.odell.glazedlists.BasicEventList
import ca.odell.glazedlists.EventList
import com.relivethefuture.dkb.ConfigModel
import com.relivethefuture.dkb.ExportConfigs
import com.relivethefuture.dkb.LayoutStyle
import com.relivethefuture.dkb.OutputLayout

import javax.swing.SpinnerNumberModel

class FormatOptionsModel {
    @Bindable String title = "Format Options"
    @Bindable int width = 800
    @Bindable int height = 600
    @Bindable boolean resizable = true
    @Bindable boolean modal = true

    def configModelMap

    @Bindable boolean reaktorFixedRoot = false
    @Bindable String reaktorRootNote = "64"

    final EventList filenameDepthOptions = new BasicEventList()
    final EventList outputDepthOptions = new BasicEventList()
    final EventList layoutSelectOptions = new BasicEventList()
    @Bindable String selectedLayout
    @Bindable String selectedFilenameDepth
    @Bindable String selectedOutputDepth
}
