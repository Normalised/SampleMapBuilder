package com.relivethefuture.dkb

import ca.odell.glazedlists.EventList
import ca.odell.glazedlists.SortedList
import ca.odell.glazedlists.swing.TableComparatorChooser
import com.relivethefuture.generic.BasicSample
import com.relivethefuture.generic.Sample
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.activation.ActivationDataFlavor
import javax.activation.DataHandler
import javax.swing.JComponent
import javax.swing.JTable
import javax.swing.TransferHandler
import java.awt.Cursor
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.dnd.DragSource

/**
 * Created by martin on 07/06/13 at 20:28
 *
 */
class SampleTableTransferHandler extends TransferHandler {

    private final Logger logger = LoggerFactory.getLogger(SampleTableTransferHandler.class)

    private final DataFlavor localObjectFlavor = new DataFlavor(Integer.class, "Integer Row Index");
    private JTable           table             = null;
    SortedList<BasicSample> sampleList
    TableComparatorChooser<BasicSample> sampleTableSorter

    Comparator<Sample> sampleComparator

    public SampleTableTransferHandler(JTable table, TableComparatorChooser<BasicSample> tableComparatorChooser) {
        logger.debug("New STTH : " + table)
        this.table = table;
        sampleTableSorter = tableComparatorChooser
        sampleComparator = SampleComparator.NOTE.tableComparator
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        return new DataHandler(new Integer(table.getSelectedRow()), localObjectFlavor.getMimeType());
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport info) {
        //logger.debug("Can Import " + info.component + " : " + info.drop + " : " + info.dataFlavors)
        boolean b = info.getComponent() == table && info.isDrop() && info.isDataFlavorSupported(localObjectFlavor);
        table.setCursor(b ? DragSource.DefaultMoveDrop : DragSource.DefaultMoveNoDrop);
        return b;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport info) {
        logger.debug("Drop into samples table")
        JTable target = (JTable) info.getComponent();
        if(target != table) {
            logger.info("Drop target is not this table")
        }
        JTable.DropLocation dl = (JTable.DropLocation) info.getDropLocation();
        int destIndex = dl.getRow();
        int max = table.getModel().getRowCount();
        if (destIndex < 0) destIndex = 0
        if (destIndex > max) destIndex = max;

        target.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

        try {

            Integer sourceIndex = (Integer) info.getTransferable().getTransferData(localObjectFlavor);
            logger.debug("Drop from " + sourceIndex + " to " + destIndex)
            if (sourceIndex != -1 && sourceIndex != destIndex && sourceIndex != (destIndex - 1)) {
                // set the root notes for list items according to their current display state
                fixListIndex(sourceIndex, destIndex)
                // change the comparator to use root note comparison
                //sampleTableSorter.clearComparator()
                sampleList.comparator = sampleComparator

                //if (destIndex > sourceIndex) destIndex--;
                // Update Selection
                //target.getSelectionModel().addSelectionInterval(destIndex, destIndex);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void fixListIndex(int src, int dest) {
        int index = 0
        sampleList.each { BasicSample s ->
            if(dest > src) {
                if(index < src || index >= dest) {
                    s.root = index
                } else if(index == src) {
                    s.root = dest - 1;
                } else if(index <= dest) {
                    s.root = index - 1
                }
            } else if(src > dest) {
                if(index < dest || index > src) {
                    s.root = index
                } else if(index < src) {
                    s.root = index + 1
                } else if(index == src) {
                    s.root = dest
                }
            }
            index++
        }
    }

    @Override
    protected void exportDone(JComponent c, Transferable t, int act) {
        if (act == TransferHandler.MOVE) {
            table.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    public void setSamples(EventList<BasicSample> s) {
        sampleList = s;
        //samples.
    }
}
