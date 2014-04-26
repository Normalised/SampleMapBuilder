package drumkitbuilder2

import com.relivethefuture.FileSystemScanner
import com.relivethefuture.ResourceUtils
import com.relivethefuture.dkb.ExportConfigs
import com.relivethefuture.dkb.Exporter
import com.relivethefuture.dkb.LayoutStyle
import com.relivethefuture.dkb.PathUtils
import com.relivethefuture.generic.Instrument
import com.relivethefuture.generic.Sample
import com.relivethefuture.kitbuilder.KitFileNameGenerator
import com.relivethefuture.kitbuilder.builder.MapBuilder
import com.relivethefuture.kitbuilder.model.FolderOfSamples
import com.relivethefuture.kitbuilder.output.OutputConfig
import com.relivethefuture.kitbuilder.writers.InstrumentWriter
import org.apache.commons.io.FileUtils

import java.nio.channels.FileChannel

class ExportService {

    ExportConfigs exportConfigs

    Exporter exporter

    void serviceInit() {
        exporter = new Exporter()
        exportConfigs = new ExportConfigs()
    }

    void serviceDestroy() {
    }

    def start(FileSystemScanner scanner) {
        exporter.start(scanner, exportConfigs)
    }

    def work() {
        return exporter.doNext()
    }

}
