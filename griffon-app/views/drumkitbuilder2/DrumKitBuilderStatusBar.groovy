package drumkitbuilder2

vbox {
    separator()
    panel {
        gridBagLayout()
        label(id: 'status', text: bind { model.status },
                constraints: gbc(weightx: 1.0,
                        anchor: GridBagConstraints.WEST,
                        fill: GridBagConstraints.HORIZONTAL,
                        insets: [1, 3, 1, 3])
        )
    }
}