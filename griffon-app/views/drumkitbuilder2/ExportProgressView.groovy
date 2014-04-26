package drumkitbuilder2

actions {
    action(id: 'cancelAction',
            name: 'Cancel',
            closure: controller.cancel,
            mnemonic: 'C',
            shortDescription: 'Cancel'
    )
}

panel(id: 'content') {
    borderLayout()
    panel(constraints: CENTER) {
        migLayout(layoutConstraints: '',columnConstraints:"",rowConstraints:"")
        label(id: 'currentFormat', text: bind { model.currentFormat },constraints: "pushx,wrap,align center")
        progressBar(id:'overallProgress',minimum:0,maximum:100,value:bind { model.overallProgress },constraints: "span,pushx,growx,wrap")
        button(action:cancelAction,constraints:"align center",text:bind {model.buttonLabel})
    }
}