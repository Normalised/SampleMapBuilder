package drumkitbuilder2

import com.relivethefuture.FileSystemScanner

class FileScannerService {

    FileSystemScanner scanner

     void serviceInit() {
        scanner = new FileSystemScanner()
     }

    void serviceDestroy() {
        scanner.reset()
    }

    def setRoot(File sourceDirectory) {
        scanner.reset()
        scanner.setRoot(sourceDirectory)
    }

    def work() {
        return scanner.work();
    }

    def getItems() {
        return scanner.items
    }

    def getTotalSamples() {
        return scanner.totalNumberOfSamples;
    }
}
