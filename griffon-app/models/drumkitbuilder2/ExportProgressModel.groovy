package drumkitbuilder2

import groovy.beans.Bindable

class ExportProgressModel {
    @Bindable String currentFormat
    @Bindable String currentMap
    @Bindable Integer overallProgress
    @Bindable Integer formatProgress
    @Bindable String buttonLabel = "Cancel"
}