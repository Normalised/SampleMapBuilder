Sample Map Builder
==================

Sample Map Builder v2

New version of Sample Map Builder which was Reaktor only.

This version also includes Ableton Live, SFZ and Renoise output among others.

Development Setup
=================

The app is built around Griffon 1.5.0 available here [http://griffon.codehaus.org/](http://griffon.codehaus.org/)

To run the app just use `griffon run-app` from the command line.

To package the app you can choose one of the standard [griffon packaging](http://griffon.codehaus.org/guide/1.5.0/guide/packaging.html) options or use the [Installer plugin](http://griffon.codehaus.org/Installer+Plugin).

All of the required jars are now available in lib because the griffon v1 artifact host is due to close down.

To build on OSX run

```
ant bundleOSX
```

