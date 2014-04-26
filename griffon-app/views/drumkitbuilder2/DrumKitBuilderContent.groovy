package drumkitbuilder2

import ca.odell.glazedlists.gui.AbstractTableComparatorChooser
import ca.odell.glazedlists.gui.TableFormat
import ca.odell.glazedlists.swing.EventTableModel
import ca.odell.glazedlists.swing.TableComparatorChooser
import com.relivethefuture.generic.Sample
import com.relivethefuture.dkb.SampleTableFormat
import com.relivethefuture.kitbuilder.model.FolderOfSamples

import javax.swing.DropMode
import javax.swing.JSplitPane
import javax.swing.JTabbedPane
import java.awt.Color

actions {
    action(id: 'toggleSampleEnabled',
            closure: { controller.toggleSelectedSample() })
}

def createFoldersTableModel() {
    def columnNames = ["Name", "Samples", "Path"]
    new EventTableModel(model.folders, [
            getColumnCount: { columnNames.size() },
            getColumnName: { index -> columnNames[index] },
            getColumnValue: { object, index ->
                switch(index) {
                    case 0: return object.name;break;
                    case 1: return object.samples.size();break;
                    case 2: return object.sourceDirectory.absolutePath; break;
                    default : return ""
                }
            }] as TableFormat)
}

    splitPane(id: 'mainContent', resizeWeight: 0.45f, border: emptyBorder(0),orientation: JSplitPane.HORIZONTAL_SPLIT) {
        jxtitledPanel(title: "Folders", border: emptyBorder(0),
                constraints: context.CENTER) {
        scrollPane(border:matteBorder(color: Color.BLACK,top:0,left:0,bottom:0,right:1)) {
            table(id: "foldersTable", model: createFoldersTableModel(),selectionModel:model.selectedFolders, autoCreateRowSorter:false)
            def tableSorter = new TableComparatorChooser(foldersTable, model.folders, AbstractTableComparatorChooser.SINGLE_COLUMN)

        }
        }
        jxtitledPanel(title: "Kit", border:emptyBorder(0),
                constraints: context.CENTER) {
        scrollPane(border:matteBorder(color: Color.BLACK,top:0,left:1,bottom:0,right:0)) {
            table(id: "samplesTable", model: model.samplesTableModel, selectionModel:model.selectedSamples, autoCreateRowSorter:false, dragEnabled:true,dropMode:DropMode.INSERT_ROWS)
        }
        keyStrokeAction(component: samplesTable,
                keyStroke: 'SPACE',
                action: toggleSampleEnabled)
        }
    }
mainContent
