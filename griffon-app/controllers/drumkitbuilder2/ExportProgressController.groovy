package drumkitbuilder2

import griffon.transform.Threading

import java.awt.Window

class ExportProgressController {
    def model
    def view
    def builder
    protected dialog

    def fileScannerService
    def exportService

    Boolean cancelExport = false

    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
    void show(Window window) {
        window = window ?: Window.windows.find{it.focused}
        if(!dialog || dialog.owner != window) {
            if(dialog) app.windowManager.hide(dialog)
            log.debug("Creating export progress dialog")
            dialog = builder.dialog(
                    owner: window,
                    title: "Exporting...",
                    resizable: false,
                    modal: true) {
                container(view.content)
            }
            dialog.preferredSize = [600, 130]
            dialog.pack()
        }
        int x = window.x + (window.width - dialog.width) / 2
        int y = window.y + (window.height - dialog.height) / 2
        dialog.setLocation(x, y)
        log.debug("Trigger export")
        cancelExport = false
        doExport()

        app.windowManager.show(dialog)

    }

    void mvcGroupInit(Map args) {
        log.debug("Export MVC Init")
    }

    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    def cancel = { evt = null ->
        log.debug("Cancel")
        cancelExport = true
        app.windowManager.hide(dialog)
    }

    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    def doExport = {
        log.debug("Do Export")
        jxwithWorker(start: true) {
            onInit {
                exportService.start(fileScannerService.scanner)
                model.buttonLabel = "Cancel"
            }
            work {
                Float progress = 0f
                def status
                while((progress < 100) || cancelExport) {
                    status = exportService.work()
                    publish(status)
                    progress = status[1]
                }
            }
            onUpdate { status ->
                model.currentFormat = status[0][0]
                model.overallProgress = Math.floor(status[0][1])
            }
            onDone {
                edt {
                    model.currentFormat = "Complete"
                    model.overallProgress = 100
                    model.buttonLabel = "Close"
                    log.debug("Export Done")
                }
            }
        }
    }
}
