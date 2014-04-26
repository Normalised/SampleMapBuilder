package drumkitbuilder2

import ca.odell.glazedlists.EventList
import com.relivethefuture.dkb.ConfigModel
import com.relivethefuture.dkb.DKBUtils
import com.relivethefuture.dkb.LayoutStyle
import com.relivethefuture.dkb.OutputLayout
import com.relivethefuture.kitbuilder.output.GenericOutput
import com.relivethefuture.kitbuilder.output.OutputConfig

import java.awt.Window
import griffon.transform.Threading

class FormatOptionsController {
    def model
    def view
    def builder

    def exportService

    protected dialog

    def previousLayout

    void mvcGroupInit(Map args) {
        log.debug("MVC init")
        if(model.configModelMap == null) {
            model.configModelMap = [:]
            exportService.exportConfigs.configMap.each { name, GenericOutput config ->
                model.configModelMap[name] = new ConfigModel(absolutePath: config.outputDirectory.absolutePath,relativePath: config.relativePath, active:config.active)
            }
        }
        EventList layoutOptions = model.layoutSelectOptions
        layoutOptions.addAll(LayoutStyle.listNames())
        EventList filenameDepthOptions = model.filenameDepthOptions
        filenameDepthOptions.add("1")
        filenameDepthOptions.add("2")
        filenameDepthOptions.add("3")
        //filenameDepthOptions.add("All")
        EventList outputDepthOptions = model.outputDepthOptions
        outputDepthOptions.add("0")
        outputDepthOptions.add("1")
        outputDepthOptions.add("2")
        outputDepthOptions.add("3")
        outputDepthOptions.add("All")

        copyFromConfigsToModel()

        previousLayout = model.selectedLayout
    }

    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
    def layoutSelected = { event ->
        log.debug("Layout Selected : " + model.selectedLayout + " : " + previousLayout)

        if(previousLayout != null && model.selectedLayout != previousLayout) {
            copyFromViewToModel(previousLayout)
        }
        LayoutStyle layoutStyle = LayoutStyle.fromName(model.selectedLayout)
        previousLayout = model.selectedLayout
        copyFromModelToView(model.selectedLayout)
        updateEnabledState(layoutStyle)
    }

    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
    void updateEnabledState(layoutStyle) {
        log.debug("Set layout style to " + layoutStyle.name)
        def browseButton
        def pathText
        view.activeBoxsamples.enabled = layoutStyle != LayoutStyle.SOURCE;
        exportService.exportConfigs.configMap.each { name, GenericOutput config ->
            browseButton = view."browse${name}"
            pathText = view."pathText${name}"
            if(!name.equals('base')) {
                browseButton.enabled = layoutStyle == LayoutStyle.ABSOLUTE
                pathText.enabled = layoutStyle != LayoutStyle.SOURCE
            } else {
                browseButton.enabled = layoutStyle == LayoutStyle.RELATIVE
                pathText.enabled = layoutStyle == LayoutStyle.RELATIVE
            }

        }


    }

    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
    void show(Window window) {
        window = window ?: Window.windows.find{it.focused}
        if(!dialog || dialog.owner != window) {
            if(dialog) app.windowManager.hide(dialog)
            log.debug("Creating new dialog")
            dialog = builder.dialog(
                owner: window,
                title: model.title,
                resizable: model.resizable,
                modal: model.modal) {
                container(view.content)
            }
            if(model.width > 0 && model.height > 0) {
                dialog.preferredSize = [model.width, model.height]
            }
            dialog.pack()
        }
        int x = window.x + (window.width - dialog.width) / 2
        int y = window.y + (window.height - dialog.height) / 2
        dialog.setLocation(x, y)
        app.windowManager.show(dialog)
    }

    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
    def hide = { evt = null ->
        previousLayout = null
        app.windowManager.hide(dialog)
        //dialog = null
    }

    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    def choosePath = { String name ->
        ConfigModel config = model.configModelMap[name]
        log.debug("Choose path for config " + name)
        File selectedDirectory = DKBUtils.selectDirectory(new File(config.absolutePath))
        if(selectedDirectory != null) {
            log.debug("Selected Directory " + selectedDirectory.absolutePath)
            config.absolutePath = selectedDirectory.absolutePath
            view."pathText${name}".text = selectedDirectory.absolutePath
        }
    }

    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    def okClicked = {
        applyChanges(model.selectedLayout)
        hide()
    }

    void applyChanges(layoutName) {
        log.debug("Apply Changes")
        def textfield
        def activeBox
        def configModels = model.configModelMap

        LayoutStyle layoutStyle = LayoutStyle.fromName(layoutName)
        exportService.exportConfigs.configMap.each { name, GenericOutput config ->
            textfield = view."pathText${name}"
            log.debug("V->M " + name + " : " + textfield.text)
            if(!name.equals('base')) {
                activeBox = view."activeBox${name}"
                model.configModelMap[name].active = activeBox.selected
                if(layoutStyle == LayoutStyle.RELATIVE) {
                    model.configModelMap[name].relativePath = textfield.text
                } else if(layoutStyle == LayoutStyle.ABSOLUTE) {
                    model.configModelMap[name].absolutePath = textfield.text
                }
            } else {
                model.configModelMap[name].absolutePath = textfield.text
            }
            log.debug("M->C " + name + " : " + configModels[name].absolutePath)
            config.setOutputDirectory(new File(configModels[name].absolutePath))
            if(!name.equals("base")) {
                config.relativePath = configModels[name].relativePath
                config.active = configModels[name].active
            }
        }
        OutputLayout outputLayout = exportService.exportConfigs.outputLayout
        outputLayout.setLayoutStyle(LayoutStyle.fromName(model.selectedLayout))
        outputLayout.filenameDepth = model.selectedFilenameDepth.equals("All") ? OutputLayout.ALL_DIRS : Integer.parseInt(model.selectedFilenameDepth)
        outputLayout.outputStructureDepth = model.selectedOutputDepth.equals("All") ? OutputLayout.ALL_DIRS : Integer.parseInt(model.selectedOutputDepth)
        log.debug("Filename Depth : " + outputLayout.filenameDepth + ". Structure : " + outputLayout.outputStructureDepth)

        if(model.reaktorFixedRoot) {
            try {
                Integer reaktorRoot = Integer.parseInt(model.reaktorRootNote)
                if(reaktorRoot >= 0 && reaktorRoot < 128) {
                    log.debug("Setting reaktor root to " + reaktorRoot)
                    exportService.exportConfigs.reaktor.setRoot(reaktorRoot)
                }

            } catch(e) {
                log.debug("Couldnt get reaktor root from model " + model.reaktorRootNote)
            }
        } else {
            exportService.exportConfigs.reaktor.setRoot(null)
        }

    }

    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
    void copyFromModelToView(layoutName) {
        def textfield
        def activeBox

        def configModelMap = model.configModelMap
        log.debug("Copy from model to view : " + layoutName)
        LayoutStyle layoutStyle = LayoutStyle.fromName(layoutName)
        exportService.exportConfigs.configMap.each { name, GenericOutput config ->
            log.debug("M->V " + name + " : " + configModelMap[name].absolutePath)
            textfield = view."pathText${name}"
            if(!name.equals('base')) {
                activeBox = view."activeBox${name}"
                activeBox.selected = configModelMap[name].active
                if(layoutStyle == LayoutStyle.RELATIVE) {
                    textfield.text = configModelMap[name].relativePath
                } else if(layoutStyle == LayoutStyle.ABSOLUTE) {
                    textfield.text = configModelMap[name].absolutePath
                }

            } else {
                textfield.text = configModelMap[name].absolutePath
            }

        }

    }

    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
    void copyFromViewToModel(layoutName) {
        log.debug("Copy from view to model")
        def textfield
        def activeBox

        LayoutStyle layoutStyle = LayoutStyle.fromName(layoutName)
        exportService.exportConfigs.configMap.each { name, GenericOutput config ->
            textfield = view."pathText${name}"
            log.debug("V->M " + name + " : " + textfield.text)
            if(!name.equals('base')) {
                activeBox = view."activeBox${name}"
                model.configModelMap[name].active = activeBox.selected
                if(layoutStyle == LayoutStyle.RELATIVE) {
                    model.configModelMap[name].relativePath = textfield.text
                } else if(layoutStyle == LayoutStyle.ABSOLUTE) {
                    model.configModelMap[name].absolutePath = textfield.text
                }
            } else {
                model.configModelMap[name].absolutePath = textfield.text
            }

        }
    }

    /**
     * Copy from configs into model
     */
    void copyFromConfigsToModel() {

        OutputLayout outputLayout = exportService.exportConfigs.outputLayout
        LayoutStyle layoutStyle = outputLayout.getLayoutStyle()
        model.selectedFilenameDepth = outputLayout.filenameDepth > 0 ? outputLayout.filenameDepth.toString() : "All"
        model.selectedOutputDepth = outputLayout.outputStructureDepth >= 0 ? outputLayout.outputStructureDepth.toString() : "All"
        model.selectedLayout = layoutStyle.name
        log.debug("Copy Configs to Model : " + outputLayout.filenameDepth + " : " + outputLayout.outputStructureDepth)

        def configModels = model.configModelMap
        exportService.exportConfigs.configMap.each { name, GenericOutput config ->
            log.debug("C->M " + name + " : " + config.getOutputDirectory()?.getAbsolutePath())
            configModels[name].absolutePath = config.outputDirectory.absolutePath
            configModels[name].active = config.active
            if(!name.equals('base')) {
                configModels[name].relativePath = config.relativePath
            }
        }
        model.reaktorFixedRoot = exportService.exportConfigs.reaktor.useFixedRoot()
        model.reaktorRootNote = exportService.exportConfigs.reaktor.root?.toString() ?: ""
        log.debug("Reaktor Fixed Root " + exportService.exportConfigs.reaktor.useFixedRoot() + " : " +  model.reaktorRootNote)
    }

}
