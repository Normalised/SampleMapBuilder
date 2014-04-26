package drumkitbuilder2

import ca.odell.glazedlists.BasicEventList
import ca.odell.glazedlists.EventList
import ca.odell.glazedlists.SortedList
import ca.odell.glazedlists.swing.DefaultEventSelectionModel
import ca.odell.glazedlists.swing.EventTableModel
import com.relivethefuture.dkb.SampleTableFormat
import com.relivethefuture.generic.Sample
import com.relivethefuture.kitbuilder.model.FolderOfSamples

class DrumKitBuilder2Model {
    @Bindable String status = ''

    EventList folders = new SortedList(new BasicEventList(), {a, b -> a.name <=> b.name} as Comparator)
    DefaultEventSelectionModel<FolderOfSamples> selectedFolders = new DefaultEventSelectionModel<FolderOfSamples>(folders)

    EventList<Sample> samples = new SortedList(new BasicEventList(), {a, b -> a.name <=> b.name} as Comparator<Sample>)
    DefaultEventSelectionModel<Sample> selectedSamples = new DefaultEventSelectionModel<Sample>(samples)

    SampleTableFormat sampleTableFormat = new SampleTableFormat()
    EventTableModel<Sample> samplesTableModel = new EventTableModel<Sample>(samples, sampleTableFormat)

}
