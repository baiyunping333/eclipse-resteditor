.. Help for the preferences page

Configuration
#############

Editor preferences
******************

The editor must be re-opened to apply preferences.

Save actions
============

ReST Editor is currently able to perform some actions when the document is saved.
You can enable or disable them using the preferences page :

* Format on save : formats complex elements (Section markers, grid tables) in
  order to obtain a valid ReST file
* Trim lines on save : delete blank spaces at the end of standard text lines.
  Other lines (source blocks, ...) are not modified
* Modify section markers on save : change all sections markers in the document
  to correspond to the preferred ones

   * Preferred section markers order : preferred section markers.
     The first one corresponds to the top level in the document.
     It is highly recommended to have at least 6 preferred markers.



Tabulations treatment
=====================

* Tab length : the tab length to be used for display and tab to spaces operations
* Insert spaces instead of tabs : automatically transform tabulation into spaces


Line wrapping
=============

.. warning:: Under development. Needs to activate the debug mode (see
   :ref:`debug-mode`)

Allows 3 wrap modes :

* None (doing nothing)
* Hard (inserting end of line sequences) and allows to set the maximum line
  length when using the Hard mode.
* Soft : works like hard mode, but un-wrap lines before saving the document.


Spell checking
==============

ReST Editor has been developed to be used with the
`Hunspell4Eclipse <http://code.google.com/p/hunspell4eclipse/>`_ plug-in.
The editor doesn't work with the Java spelling service (provided by the JDT).

Run configuration
*****************

This plug-in provides a run configuration to generate easily your document using
the Makefile (under Unix-like systems) or the make.bat file.
Sphinx must be properly installed on the system to use this run configuration.

If it is not in the PATH, use the environment tab to set up the PATH (path to
the sphinx-build directory) variable or the SPHINXBUILD (complete sphinx-build
path) variable

Main tab
========

Here you can choose the Makefile directory to be used while running the
configuration, the Sphinx output formats and where to find the make command (on
Unix-like systems).

Common tab
==========

Common run configuration options. You may not need to use it.

Environment tab
===============

This tab allows you to configure the process environment to use when the
configuration is executed.

You can set up the PATH variable to define where to find the sphinx-build
script, or any other variable your Sphinx configuration script may use.
