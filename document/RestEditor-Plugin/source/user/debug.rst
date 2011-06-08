.. Debug mode documentation

.. _debug-mode:

Debug Mode
##########

Activation
==========

The plug-in debug mode is activated by adding the VM parameter
``-Dresteditor.debug`` in the Eclipse configuration file (for example, 
*/opt/eclipse/eclipse.ini*).

Thanks to
`report bugs <https://sourceforge.net/tracker/?group_id=554338&atid=2249477>`_
on the SourceForge project page when a bug is encountered.

Features enabled in debug mode
==============================

The next feature(s) are only available in debug mode :

* Word wrapping in soft and hard modes. It can be activated using the
  preferences page.
  
  This feature is nearly working and is not destructive, but has some document
  state bugs (saved document is marked as modified) and may not work in all
  situations (caret position errors, ...).
