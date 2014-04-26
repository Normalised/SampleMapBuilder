package drumkitbuilder2

build(DrumKitBuilderActions)

application(title: 'Sample Map Builder',
  preferredSize: [1024, 768],
  pack: true,
  locationByPlatform: true,
  iconImage:   imageIcon('/icon-48x48.png').image,
  iconImages: [imageIcon('/icon-48x48.png').image,
               imageIcon('/icon-32x32.png').image,
               imageIcon('/icon-16x16.png').image]) {

    widget(build(DrumKitBuilderMenuBar))
    migLayout(layoutConstraints: 'fill')
    toolBar(build(DrumKitBuilderToolBar), constraints: 'north')
    widget(build(DrumKitBuilderContent), constraints: 'center, grow')
    widget(build(DrumKitBuilderStatusBar), constraints: 'south, grow')

}
