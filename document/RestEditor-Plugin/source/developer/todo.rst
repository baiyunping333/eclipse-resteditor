.. TODO list for ReST Editor

TODO list
#########

TODO table
**********

+------------------------+----------+--------------------+
| Task                   | Priority | Duration (approx.) |
+========================+==========+====================+
| Line wrapping          | Hight    | 2 weeks            |
+------------------------+----------+--------------------+
| Shortcuts              | Low      | 2 days             |
+------------------------+----------+--------------------+
| Formatters             | Low      | 3 days             |
+------------------------+----------+--------------------+
| Favorite configuration | Medium   | 2 days             |
+------------------------+----------+--------------------+
| Sphinx output          | Low      | 3 weeks            |
+------------------------+----------+--------------------+
| Handle other tools     | Low      | 2 weeks            |
+------------------------+----------+--------------------+
| Architecture           | Low      | 4 weeks            |
+------------------------+----------+--------------------+

Editor
******

Line wrapping
=============

The current line wrapping implementation must be fixed and completed.

This task will be considered as done when :

* the caret offset will be correctly updated
* in-line markers, links, ... won't be cut anymore


Layout shortcuts
================

A useful feature would be to handle *standard* shortcuts (Ctrl+i, Ctrl+b, ...)
to insert the corresponding in-line markers around the selected text or the
current word.


Formatters
==========

It would be great to correct the caret offset after modifying section titles
and tables, as done in the word wrapping process.


Run configuration
*****************

Update the favorite configurations
==================================

The Sphinx run configuration is never added to the favorite run configurations.
Therefore, the user always have to select its run configuration instead of
directly clicking the green arrow to use the last one.

Allow custom Makefile target
============================

By adding a check-box *custom* and a text field, the user could write his own
Makefile rules or use the Sphinx makefile builder with other Makefile projects.


Handle Sphinx output
====================

By handling Sphinx error output, we could annotate documents when an error is
encountered while generating the documentation.


Handle other tools
==================

Sphinx is not the only tool to compile ReST documents, therefore we could try
to provide support for some of them, like :

* rst2pdf : a direct PDF generator, that doesn't need LaTex to work.
* rst2beamer : generates a LaTex beamer source file, ready to be compiled with
  LaTex.

Architecture
************

A review of the complete architecture is important : the project grown with
new informations and lost its initial design.
Moreover, many improvements could be done with the knowledge acquired during the
development process.
