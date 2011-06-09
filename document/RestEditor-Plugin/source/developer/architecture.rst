.. ReST Editor architecture description

Architecture
############

Overview
********

.. todo:: Insert schema

Packages description
********************

The root package is ``org.isandlatech.plugins.rst``, which contains the two main
singletons of the plug-in :

* ``BrowserController`` : a utility class that tries to provide internal
  browsing support.
* ``RestPlugin`` : the plug-in activator

The sub-packages are :

editor
   This package contains the classes needed for the initialization of the source
   editor :
   
   * ``RestDocumentSetupParticipant`` : called while the document is loaded, it
     connects the ReST partitioner to it.
   * ``RestEditor`` : the text editor itself.
   * ``RestViewerConfiguration`` : the class used to set up the editor.
   
editor.formatters
   All classes here are used for on-save formatting operations.
   
   * ``AbstractFormattingStrategy`` : basic implementation of a formatting
     strategy, providing some utility methods.
   * ``DefaultTextFormattingStrategy`` : removes trailing spaces on standard
     text lines.
   * ``GridTableFormattingStrategy`` : rewrites grid tables to avoid compilation
     errors.
   * ``SectionFormattingStrategy`` : rewrites section titles to avoid
     compilation errors.
   
editor.linewrap
   Soft and hard line wrapping implementation, still in alpha version.
   See :ref:`line-wrapping` for more information.
   
   * ``AbstractBlockDetector`` : A utility implementation of ``IBlockDetector``
   * ``AbstractBlockWrappingHandler`` : A utility implementation of
     ``IBlockWrappingHandler``
   * ``BlockInformation`` : Stores information about a wrapped text block
   * ``BlockWrappingHandlerStore`` : Stores and provides instances of
     ``IBlockWrappingHandler``.
   * ``DefaultBlockDetector`` : Standard implementation of the block detector.
   * ``DefaultBlockWrappingHandler`` : Standard implementation of the block
     wrapper.
   * ``HardLineWrap`` : Core methods for block/document wrapping
   * ``HardLineWrapAutoEdit`` : Auto-edit strategy for the text editor, calling
     ``HardLineWrap``.
   * ``IBlockDetector`` : Description of a block detector
   * ``IBlockWrappingHandler`` : Description of a block wrapper
   * ``LinePositionUpdater`` : A *Position* updater that, in fact, updates the
     offset of watched lines.
   * ``LineUtil`` : Some lines utility methods
   * ``LineWrapUtil`` : Some line wrapping utility methods
   * ``ListBlockDetector`` : Specific ReST list block detector
   * ``ListBlockWrappingHandler`` : Specific ReST list block wrapper

editor.outline
   Everything useful to show the document hierarchy in the outline view.
   
   * ``HierarchyAction`` : The *move ...* buttons on the outline view for
     hierarchy modification.
   * ``NormalizeSectionsAction`` : The *normalize...* button on the outline 
     view, for section titles markers normalization.
   * ``OutlineUtil`` : Some utility methods.
   * ``RefreshOutlineAction`` : The *refresh* button on the outline view.
   * ``RestContentOutlinePage`` : The outline page itself, managing the tree
     view content.
   * ``SectionContentProvider`` : The tree view content provider.
     It reads the document to extract its hierarchy. 
   * ``SectionLabelProvider`` : Indicates how to display the tree view content.
   * ``TreeData`` : Data extracted by ``SectionContentProvider``.

editor.providers
   Here are some classes that aims to reduce memory consumption by storing
   equivalent instances of rules and tokens description only once.
   
   * ``IThemeConstants`` : Defines preferences keys used for color
     customization.
   * ``RuleProvider`` : Provides rules for the rule-based scanner
   * ``TokenProvider`` : Provides tokens for the rule-based scanner
   
editor.rules
   Defines all rules used for tokens detection by the rule based scanner.

   * ``AbstractRule`` : Utility implementation of an IPredicateRule
   * ``ContiguousRules`` : Meta-rule, combining two contiguous rules
   * ``DecoratedLinesRule`` : Detects decorated lines (section titles)
   * ``ExactStringRule`` : Detects the exact given string
   * ``LinePrefixRule`` : Detects lines and blocks with the given prefix
   * ``MarkedCharacterScanner`` : An ICharacterScanner wrapper, able to reset
     its position.
   * ``MarkupRule`` : Detects *markups* (in ReST terms : bold marker, ...)
   * ``RestGridTableRule`` : Detects ReST grid tables (ASCII art)
   * ``RestSimpleTableRule`` : Detects ReST simple tables (not tested)

editor.scanners
   
   * ``AbstractRuleBasedScanner`` : Rule based scanner utility implementation
   * ``ITokenConstants`` : Tokens constants definition
   * ``RestLiteralBlockScanner`` : Scanner for literal blocks
   * ``RestPartitionScanner`` : ReST document partitioner (defines partitions)
   * ``RestScanner`` : Default text scanner (defines tokens in the default
     partition)
   * ``RestSectionBlockScanner`` : Scanner for sections
   * ``RestSourceBlockScanner`` :  Scanner for source blocks
   * ``RestTableBlockScanner`` : Scanner for ReST tables

editor.userassist
   Defines common classes for text hovering and content assistance.

   * ``BasicInternalLinkHandler`` : internal browser links handler, for hover
     actions proposal.
   * ``HelpMessagesUtil`` : Utility class to format i18n help messages.
   * ``IAssistanceConstants`` : Constants for internal browser links definition
     and treatments.
   * ``IInternalBrowserListener`` : Description of an internal browser links
     handler.
   * ``InternalBrowserData`` : Structure used to transmit internal links data
   * ``InternalBrowserInformationControl`` : Utility class to let Eclipse use
     an internal browser for hover dialogs and content assistance.

editor.userassist.contentassist
   Defines the content assistance handler.

   * ``AbstractProposalProcessor`` : A utility implementation of
     IContentAssistProcessor
   * ``DeclarativeProposalProcessor`` : Content assistant processor for literal
     blocks.
   * ``HoverCompletionProposal`` : A completion proposal implementation
     providing internal browser link support.
   * ``ProposalDispatcher`` : A content assistant dispatcher according to the
     nearest non-default partition, avoiding a bug on ends of line.

editor.userassist.hover
   Defines the text hover handler : provides a ReST help in literal blocks and
   a spell check replacement proposal.
   
   * ``RestTextHover`` : Sets up the text hover dialog, with help and spell
     proposals
   * ``SpellingProblemCollector`` : The collector used to store spelling
     problems detected by the selected spelling engine in the hovered region.

i18n
   The internalization package, containing the properties files that define the
   locale labels.
   
   * ``Messages`` : Utility class to access the localized text.
   * *messages.properties* : The en-US version of the localization.

launch
   The Sphinx *Run configuration* package, defining a builder and a run
   configuration type.
   
   * ``IMakefileConstants`` : Run configuration preferences constants
   * ``MakefileLauncher`` : The Makefile launcher, using ProcessBuilder.
   * ``MakefileTabGroup`` : The run configuration preference page definition
   * ``MakefileTabMain`` : The main tab in the preference page
     (output selection, ...)

parser
   ReST language description package.
   
   * ``RestLanguage`` : Defines all constants used by rules and internal text
     treatments that describes ReST specific concepts (list markers, markups,
     ...)
   * ``SingleWordDetector`` : A word detector for the Eclipse *built-in*
     WordRule. It is used for directives detection.

prefs
   Preferences handling package
   
   * ``EditorPreferenceInitializer`` : The preferences initializer, setting up
     the default values to be used.
   * ``EditorPreferencePage`` : Definition of the preferences page content.
   * ``IEditorPreferenceConstants`` : Definition of the preferences keys and
     default values.

wizards
   New project wizard package

   * ``AbstractWizardPage`` : An extension of WizardPage, providing utility
     methods.
   * ``ConfigGenerator`` : A Sphinx *conf.py* file generator, based on a
     template file.
   * ``IConfigConstants`` : Project and Sphinx configuration constants.
   * ``NewSphinxProject`` : Core of the *New Sphinx project* wizard.
   * ``ProjectAdvancedPropertiesPage`` : *Advanced properties* page definition.
   * ``ProjectPropertiesPage`` : *Project properties* page definition.
