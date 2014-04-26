package drumkitbuilder2

import ca.odell.glazedlists.EventList
import ca.odell.glazedlists.SortedList
import ca.odell.glazedlists.gui.AbstractTableComparatorChooser
import ca.odell.glazedlists.impl.sort.ComparatorChain
import ca.odell.glazedlists.impl.sort.ReverseComparator
import ca.odell.glazedlists.impl.sort.TableColumnComparator
import ca.odell.glazedlists.swing.DefaultEventSelectionModel
import ca.odell.glazedlists.swing.TableComparatorChooser
import com.relivethefuture.dkb.DKBUtils
import com.relivethefuture.dkb.SampleComparator
import com.relivethefuture.dkb.SampleTableFormat
import com.relivethefuture.dkb.SampleTableTransferHandler
import com.relivethefuture.generic.BasicSample
import com.relivethefuture.generic.Sample
import com.relivethefuture.kitbuilder.model.FolderOfSamples
import griffon.transform.Threading

import javax.swing.JTable
import javax.swing.ListSelectionModel
import javax.swing.event.ListSelectionListener
import java.awt.Cursor
import java.awt.Desktop
import java.util.prefs.Preferences

class DrumKitBuilder2Controller {

    def model
    def view
    def audioPlayerService
    def fileScannerService
    def exportService

    File sourceDirectory

    FolderOfSamples selectedFolder

    static final Preferences PREFERENCES = Preferences.userNodeForPackage(DrumKitBuilder2Controller)

    TableComparatorChooser samplesTableSorter
    SampleTableTransferHandler sampleTableTransferHandler

    def sortInfo

    void mvcGroupInit(Map args) {

        sortInfo = [:]

        edt {
            SortedList<FolderOfSamples> folderList = model.folders
            DefaultEventSelectionModel<FolderOfSamples> folderSelectionModel = model.selectedFolders
            folderSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
            folderSelectionModel.addListSelectionListener({ event ->
                if(!event.valueIsAdjusting) {
                    folderSelected()
                }
            } as ListSelectionListener)

            SortedList<Sample> sampleList = model.samples
            DefaultEventSelectionModel<Sample> sampleSelectionModel = model.selectedSamples
            sampleSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
            sampleSelectionModel.addListSelectionListener({ event ->
                if(!event.valueIsAdjusting) {
                    sampleSelected()
                }
            } as ListSelectionListener)

            String sourcePath = PREFERENCES.get("sampleSourcePath",System.getProperty("user.home"))

            sourceDirectory = new File(sourcePath)
            JTable samplesTable = view.samplesTable

            samplesTableSorter = new TableComparatorChooser<BasicSample>(samplesTable, model.samples, AbstractTableComparatorChooser.SINGLE_COLUMN)
            sampleTableTransferHandler = new SampleTableTransferHandler(samplesTable, samplesTableSorter)
            samplesTable.setTransferHandler(sampleTableTransferHandler)
        }
    }

    void mvcGroupDestroy() {
        log.debug("Updating preferences")
        exportService.exportConfigs.updatePrefs()
    }

    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    def toggleSelectedSample() {
        log.debug("Toggle Selected Sample")
        DefaultEventSelectionModel<Sample> desm = model.selectedSamples
        EventList<Sample> selected = desm.getSelected()

        if(selected.size() > 0) {
            log.debug("Sample Selected " + desm.getSelected().get(0).name)
            Sample sample = desm.getSelected().get(0)
            sample.exclude = !sample.exclude
        }
        edt {
            view.samplesTable.repaint()
        }

    }

    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    def browseAction = {
        log.debug("Browse")
        File selectedDirectory = DKBUtils.selectDirectory(sourceDirectory,"Select Samples Folder")
        if(selectedDirectory != null) {
            sourceDirectory = selectedDirectory
            PREFERENCES.put("sampleSourcePath",sourceDirectory.getAbsolutePath())
            log.debug("Selected Directory " + sourceDirectory.getAbsolutePath())
            model.status = "Using " + sourceDirectory.absolutePath
            scanAction()
        }
    }

    @Threading(Threading.Policy.OUTSIDE_UITHREAD)
    def exportAction = {
        log.debug("Scan")
        storeComparatorForSelectedFolder()

        withMVCGroup('exportProgress') { m, v, c ->
            c.show()
        }
    }

    @Threading(Threading.Policy.OUTSIDE_UITHREAD)
    def scanAction = {
        jxwithWorker(start: true) {
            onInit {
                fileScannerService.setRoot(sourceDirectory)
            }
            work {
                File current
                while(current = fileScannerService.work()) {
                    publish(current.name)
                }
            }
            onUpdate { dirNames ->
                model.status = "Scanning " + dirNames[0]
            }
            onDone {
                edt {
                    view.foldersTable.rowSorter = null
                    model.folders.clear()
                    model.folders.addAll(fileScannerService.getItems())
                    view.exportAction.enabled = fileScannerService.getItems().size() > 0
                    view.scanAction.enabled = true
                    model.status = "Scanned " + model.folders.size() + " directories with " + fileScannerService.getTotalSamples() + " samples."
                }
            }
        }
    }

    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    def configureAction = {
        log.debug("Configure")
        withMVCGroup('formatOptions') { m, v, c ->
            c.show()
        }
        // If output layout isnt using source folders then update the filenames in the source folder list
    }

    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    def helpAction = {
        File f = new File("SampleMapBuilder.pdf")
        log.debug(f.getAbsolutePath())
        Desktop.getDesktop().open(f)
    }

    def aboutAction = {
        withMVCGroup('about') { m, v, c ->
            c.show()
        }
    }

    def quitAction = {
        app.shutdown()
    }

    def onOSXAbout = { app ->
        withMVCGroup('about') { m, v, c ->
            c.show()
        }
    }

    def onOSXQuit = { app ->
        app.shutdown()
    }

    def onOSXPrefs = { app ->
        withMVCGroup('formatOptions') { m, v, c ->
            c.show()
        }
    }

    def folderSelected() {

        storeComparatorForSelectedFolder()

        DefaultEventSelectionModel<FolderOfSamples> desm = model.selectedFolders
        EventList<FolderOfSamples> selected = desm.getSelected()
        if(selected.size() > 0) {
            log.debug("Folder Selected " + desm.getSelected().get(0).name)
            selectedFolder = desm.getSelected().get(0)
            SortedList<BasicSample> sortedSampleList = model.samples

            // Restore sort order
            def folderSortInfo = sortInfo[selectedFolder]
            if(folderSortInfo != null) {
                if(folderSortInfo.comparator) {
                    log.debug("Using root note comparator")
                    sortedSampleList.comparator = folderSortInfo.comparator
                } else {
                    log.debug("Using column sort info : " + folderSortInfo.column + " : " + folderSortInfo.reverse)
                    // configure table sort column and order
                    samplesTableSorter.clearComparator()
                    samplesTableSorter.appendComparator(folderSortInfo.column,0,folderSortInfo.reverse)
                }
            }

            sampleTableTransferHandler.setSamples(sortedSampleList)
            edt {
                view.samplesTable.rowSorter = null
                sortedSampleList.clear()
                sortedSampleList.addAll(selectedFolder.samples)
            }
        }
    }

    void storeComparatorForSelectedFolder() {
        // Store sorting for selected folder
        if(selectedFolder != null && model.samples != null) {

            SortedList<BasicSample> currentSampleList = model.samples
            if(currentSampleList.getComparator() == SampleComparator.NOTE.tableComparator) {
                log.debug("Storing root note comparator for " + selectedFolder.name)
                sortInfo[selectedFolder] = [comparator: SampleComparator.NOTE.tableComparator]

                selectedFolder.setSampleComparator(SampleComparator.NOTE.sampleComparator)
            } else {
                // Get sorting info from table comparator chooser. column number, reverse..
                List<Integer> sortingColumns = samplesTableSorter.getSortingColumns()
                if(sortingColumns.size()) {
                    Integer sortColumn = sortingColumns.get(0)
                    log.debug("Saving column sort info : " + sortColumn + " : " + samplesTableSorter.isColumnReverse(sortColumn) + " for " + selectedFolder.name)
                    sortInfo[selectedFolder] = [column:sortColumn,reverse:samplesTableSorter.isColumnReverse(sortColumn)]
                    Comparator fromTableColumn = SampleComparator.byTableColumn(sortColumn, samplesTableSorter.isColumnReverse(sortColumn))
                    selectedFolder.setSampleComparator(fromTableColumn)
                }
            }
        }
    }

    void sampleSelected() {
        DefaultEventSelectionModel<Sample> desm = model.selectedSamples
        EventList<Sample> selected = desm.getSelected()

        log.debug("Selection " + selected.size())
        if(selected.size() > 0) {
            log.debug("Sample Selected " + selected.get(0).name)
            Sample sample = selected.get(0)
            if(sample != null) {
                audioPlayerService.play(sample.file)
            }

        }

    }
}
