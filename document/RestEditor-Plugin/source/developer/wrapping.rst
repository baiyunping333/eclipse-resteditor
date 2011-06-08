.. Line wrapping document
.. highlight:: java

.. _line-wrapping:

Line wrapping
#############

Definition
**********

Line wrapping is the action of modifying a text in order to control is maximal
line width.

Their are two kinds of line wrapping :

* Hard wrapping : the simplest one to implement. The principle is to wrap text
  lines and to store the file as-is.

* Soft wrapping : the text is shown as a hard wrapped one, but all lines are
  un-wrapped before saving the file. The wrapping is only visual.


Line wrapping in Eclipse
************************

Methods
=======

There are two main methods to provide soft line wrapping in Eclipse :

* using SWT text widget features (soft-wrapping only)
* by providing an auto-edit strategy (hard and soft wrapping)

In the first method, the developer must grab a reference to the underlying SWT
text widget used by the Eclipse SourceViewer, then activate its text wrapping
option.

It is very simple to do, but it can't be configured, the text width depends on
the widget width, and it implies many bugs.
For example, the line numbers, which are in a separate widget, are not linked
with the shown text, therefore document links are not working.

This method has been applied in the first versions of the plug-in
`Eclipse word-wrap <http://ahtik.com/blog/projects/eclipse-word-wrap/>`_.
By now, it seems to use a different approach, by implementing a specific line
tracker.

The second method has been implemented by
`TeXlipse <http://texlipse.sourceforge.net/>`_ : each time the user writes a
character, the auto-edit strategy wraps the modified line.
This way, the wrapping process is totally handled by the developer, who has
more control.


ReST Editor implementation
**************************

ReST Editor uses the TeXclipse approach : an auto-edit strategy has been
defined in order to wrap modified lines while they are written.

The algorithm is the same for hard and soft wrapping, the only difference is
that the document is totally un-wrapped before it is saved on disk.


Eclipse Auto-edit strategies
============================

An auto-edit strategy is an instance of a class implementing
``IAutoEditStrategy``.
On each text modification, the source viewer calls all strategies
``customizeDocumentCommand`` method indicating the modified document and a
replace command description corresponding to the content modification.

A replace command description is a DocumentCommand object, containing the
following fields :

* **offset** : the offset of the beginning of the modification
* **length** : the length of the modified region. This region is replaced by the
  content of the **text** field. If length is 0, then the text is simply
  inserted.
* **text** : The added text
* **caretOffset** : the offset of the caret after the modification
* **doit** : indicated if the modification must be applied or not.
 
The replacement is easy to handle, the most difficult point is to provide a
valid caret offset after line wrapping.


Algorithm
=========

This algorithm is inspired from the TeXlipse one.

#. Store information about the modified line.

#. Find the modified block, using block detectors.
   
   * If no one is found, return without modifying the document.

#. Get the block wrapping handler associated to the detector.

   * If no one is found, return without modifying the document.

#. Use the handler, in a synchronized block :

   #. Configure it, indicating the modified document and the block to be
      wrapped.
   
   #. Set the reference offset (see :ref:`wrapping-internals`).

   #. Apply the replace command on the block.
   
   #. Retrieves the wrapped version of the block.
   
#. If the result is valid, set up the replace command fields to apply the
   modification.

#. Retrieve a description of the applied modification.

On document loading, a utility method is called, using an empty DocumentReplace
object on each detected block to wrap them.
This way, the document is initially wrapped before any user action.

On save, when soft-wrapping is enabled, a utility method detects all wrapped
blocks, using the watched lines information, and asks the corresponding handler
to convert blocks in a line.
When the document is saved, it is re-wrapped as while loading.

.. _wrapping-internals:

Internals
=========

Detectors
---------

The wrapping algorithm is base on block detectors and wrapping handlers.

Detectors are objects that find the real bounds of the modified block.
They are called with the first and the last modified lines in the document and
must return a block information containing similar lines.
The detector can return null if it can't recognize a valid block there.

In ReST Editor, there are two detectors :

* the default detector, which searches for similar lines, according to their
  indentation.
  It considers empty lines as end of block. 

* the list detector works the same way than the default one, but it has a
  specific constraint : the first line of the block must be de-indented and
  start with a list marker.

Each detector has a priority : the less its value is, the more chance it get
to be selected if more than one detector found a block.
The default detector has the maximum priority value possible
(``Integer.MAX_VALUE - 1``).

When the best detector has been found, we use its associated handler to do the
job.
Detectors and handlers are linked by a string corresponding to the handler type.


Wrapping handler
----------------

A wrapping handler is the real implementation of the block wrapping, depending
on the kind of the detected block.

The base wrapping algorithm is the following :

#. Get the modified block content : the replace command must have already been
   applied.

#. Convert the block in a single line :

   * Except for the first line, remove lines indentation.
   * Except the last one, replace all line delimiters by a space character.

#. Wrap the in-line block :

   * While we find a break position in the in-line block.
   * Append indentation, left-trimmed sub-line and a line delimiter to the
     result.
   * Update the current position offsets, relative to the in-line block and to
     the result line.

#. Prepare the wrapping information and return.


The reference offset
--------------------

The reference offset is an offset in the block, relative to the document, that
is updated every step of the wrapping.

This way, we can compute the caret offset as it must be before wrapping, and
let the handler update it as needed after all modifications of the block.

In the end, when the handler returns a valid value, the caret offset in the
replace command must be the updated value of the reference one.


Problems
========

Add a line or a space
---------------------

Adding a line in a wrapped document is not possible : once the command has been
applied, the new line delimiter is destroyed during the conversion of the block
into a single line.

The current solution is to replace the line delimiter by a specific one during
the wrapping and replace it by two line delimiters at the end of the handler
treatment.
We currently use the pilcrow character (U+00B6 : ¶) as an internal line marker.

Adding a space is more difficult :

* a leading space will be deleted by the left-trimming step.
* a trailing space may be inserted, if not wrapped.


Update the caret offset
-----------------------

The main problem in the auto-edit strategy wrapping method is that we have to
provide the offset of the caret at the end of the modification.

As said before, we use the reference offset mechanism to update its position
during the wrapping process.
Unfortunately, this update is bogus and may not work in every encountered
condition.

For example, a known bug : if we insert a character at the position just before
the last one, the caret will jump over this one after to be at the end of the
line.
The same treatments work very well if the character is inserted in the middle
or at the beginning of a line.
