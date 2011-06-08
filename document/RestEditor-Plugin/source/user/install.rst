.. ReST Plugin installation

Installation
############

Set up Sphinx Documentation Generator
*************************************

Prerequisites
=============

Sphinx is the official Python documentation generator, written in Python.
The following packages are required to install and run Sphinx :

* `Python 2.7 <http://www.python.org/download/>`_
* `Python Setup Tools <http://pypi.python.org/pypi/setuptools>`_ (for the
  easy_install command)
* Under Unix-like systems : GNU make

Installation
============

Sphinx is easily installed using Python setup tools :

.. code-block:: bash

   easy_install -U sphinx

Under Unix-like systems, this command may be ran with root privileges (or with
fakeroot).

Install the ReST Editor plugin
******************************

Prerequisites
=============

The only thing you need is the Eclipse IDE, in a version >= 3.6 (Eclipse Helios).
The plug-in has been developped under Eclipse Helios (3.6), and will be tested
on Indigo (3.7).

It seems we have a problem under Fedora 15 with Eclipse Helios, due to the lack
of support of XulRunner 2 in Eclipse Helios.

We recommand to use Eclipse for JavaEE Developpers distribution, which provides
the internal browser support, `here <http://www.eclipse.org/downloads/>`_.

Installation
============

Like other plugins :

* Go to Help> Install new software
* Add the isandlaTech forge update site : ``http://forge.isandlatech.com:3080/eclipse``
* Select the ReST Editor plug-in.
