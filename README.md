# OpenPplSoft

OpenPplSoft is an open source runtime for PeopleSoft applications. A typical PeopleSoft application is usually composed of several different types of assets, but the most common and traditional of these are PeopleSoft components. Components are run by the Component Processor, a proprietary runtime embedded within PeopleTools. OpenPplSoft aims to be a faster, more flexible, clean room implementation of the Component Processor.

## Project Status

Although far from being production ready, OpenPplSoft (OPS) is far from nascent. OPS includes a functioning PeopleCode interpreter (`org.openpplsoft.antlr4` package) and a verification facility to compare OPS behavior against the behavior taken by PeopleTools when opening a component; the latter is provided in the form of a standard PeopleSoft tracefile. As OPS opens a particular component, this facility compares OPS emissions with the tracefile, line by line; if any mismatch occurs, OPS halts. At this point, OpenPplSoft can start with nothing but a database connection and build the correct component buffers, run the correct SQL, and run the correct PeopleCode for SSS\_STUDENT\_CENTER (23,605 tracefile lines), SSR\_SSENRL\_LIST (33,746 tracefile lines), and CLASS\_SEARCH (42,804 tracefile lines).

## Goals

My primary goal with OpenPplSoft is to have fun, learn, and challenge myself to see how much of the Component Processor I can reverse engineer. PeopleSoft has always intrigued me. It has enormous staying power, and has managed to evolve with the rapidly changing times; in the 1980s it was a rich-client application, and today it is a client-server application used by corporations, universities, and governments worldwide. Amazon, the National Security Agency, the state of Texas, Stanford University, University of California at Berkeley, and many more rely on PeopleSoft. End users always seem to have something negative to say about the interface, and PeopleSoft's age does show in some respects, but PeopleSoft is unrivaled in market adoption and isn't going anywhere anytime soon.

It's possible that OPS could be useful to an organization one day. The most likely use case for a production-ready OPS would involve serving web services to vanilla and custom PeopleSoft components, with lower latency and more flexibility than is possible with PeopleSoft's Integration Broker. But the complexity of PeopleSoft, and the necessarily conservative stance businesses have towards changes to critical production systems, makes this a huge challenge that would require more developers and many more development hours. If you would like to contribute to OpenPplSoft, do not hesitate to contact me or submit a pull request.

## How to Run OpenPplSoft

OPS only requires access to a PeopleSoft database at runtime; it bypasses the PeopleSoft application server (PSAPPSRV) and thus is not necessary when running OPS. OPS uses JDBC to connect to a PeopleSoft database and build in-memory representations of components; as of this writing, no writes are made to the database, only reads.

However, to generate tracefiles, which are read by OPS at runtime, you will need a PSAPPSRV process and WebLogic instance in order to login to PeopleSoft, select the necessary trace settings, and open the appropriate component.

Speaking from experience, it is very difficult and time-consuming to acquire or create a PeopleSoft environment. If you are up to the challenge, I have written up my notes on the process and made them available in three separate posts on my website; the first of the three posts is available [here][ps-post1], and links to the subsequent posts are available at the end of each.

Once you have access to a PeopleSoft database, you will need to check out the OpenPplSoft codebase (OPS binaries do not exist at this time, as the project is in a highly developmental state). I recommend that this be done on a machine separate from the database server, but this is not strictly necessary. You can clone the OPS codebase via `git@github.com:OpenPplSoft/OPS.git`.

The following dependencies are required on the machine you intend to run OPS on:

* JDK 8
* Ant 1.9.4 or higher
* Apache Ivy

Next, take a look at the `resources` directory in the OPS root directory. Two of the files within, `MQUINN_AWS.xml` and `MQUINN_XEN.xml`, are environment-specific files; one corresponds to my remote PeopleSoft cluster on AWS, the other to a PeopleSoft cluster running on my local Xen machine. You will need a file corresponding to your own environment; the name of this environment is passed to OPS at runtime. Make a copy of either of the two XML files, give it a unique name, and do the following:

* Modify the `dataSource` bean to use the driver, URL, username, and password appropriate for your PeopleSoft database.
* Add the SID of your database to `resources/ops.properties`, then modify the `psEnvironmentName` bean to reference the newly added property.
* Remove all component runtime profiles except one, and modify it to reference a component available in your PeopleSoft database, along with a tracefile generated by you while opening that component. Example trace files can be found in the `trace` directory, along with a file called `INDEX`, which explains the steps I took to generate each trace file.

At this point you should be able to run the following build steps:

* `$ ant clean`
* `$ ant antlr`
* `$ ant compile`

The `antlr` build target is separate from the main compilation target; this is because the PeopleCode grammar rarely changes (however, if you change the PeopleCode.g4 file, you must remember to run this target in order to rebuild the Antlr generated lexers and parsers). Note that if you make incremental changes to the OPS source, you can simply run `ant compile`, without needing to do a clean rebuild each time.

To have OPS build a component in your database, run the following:

`$ ant exec -Denvi=<name_of_config_xml> -Dcomp=<name_of_component> -emacs`

For example, to have OPS build the CLASS\_SEARCH component in my local PeopleSoft database, I run:

`$ ant exec -Denvi=MQUINN_XEN -Dcomp=CLASS_SEARCH -emacs`

This will output all of the PeopleCode instructions, SQL statements, and component processing steps required to build the component. An execution summary will be emitted at the very end, describing various aspects of the build. Understanding the various aspects requires a detailed knowledge of PeopleSoft and the Component Processor; if you have any questions, don't hesitate to contact me.

## OPS Architecture

The `org.openpplsoft.Main` class contains the main entry point for OPS. It sets up the trace file verifier, initializes several PeopleTools environment variables, and performs Component Processor actions for the requested component. From a high level, you should be able to see how these actions map to the top half of the following PeopleSoft Component Processor flowchart:

![PeopleSoft Component Processor flowchart](https://raw.github.com/OpenPplSoft/OPS/master/images/ps_comp_processor_flow.jpg)

From a package-level perspective, here are summaries of each OPS source package:

* `org.openpplsoft.antlr4` contains the PeopleCode loader and interpreter classes, in addition to supporting files. Note that when you build OPS, the `antlr` build target will create a subpackage named `frontend`; this will contain the lexer, parser, and supporting files for the PeopleCode language, as generated by ANTLR using the `grammars/PeopleCode.g4` grammar file.
* `org.openpplsoft.buffers` contains definitions for the various in-memory buffers that contain component data.
* `org.openpplsoft.bytecode` contains the logic needed to convert PeopleCode bytecode programs into their equivalent text representation. The classes in this package are derived from the excellent "Decode PeopleCode" open source project by Erik H.
* `org.openpplsoft.pt` contains definitions for various PeopleSoft artifacts, with subpackages for pages and programs.
* `org.openpplsoft.runtime` contains definitions for various OPS runtime classes, including the trace file verifier, environment variable repository, the interpreter supervisor, and the global function library.
* `org.openpplsoft.sql` contains a consolidated SQL statement library used during execution, along with useful abstractions of SQL statements in order to allow clear and easy comparison of SQL statements regardless of whether they originated from a PeopleSoft tracefile or were emitted by OPS directly.
* `org.openpplsoft.types` contains definitions for all of the various data types used in PeopleCode programs and within the component buffers. Some of these are self-explanatory, while others are quite complex due to various nuances between standalone and buffer rows and rowsets.

The JavaDocs for OpenPplSoft are located [here][ops-javadocs].

## How to Contribute

I welcome any and all efforts to contribute to OpenPplSoft. Contributing to OPS involves the same steps as running it due to the need to build from source, so if you can run OpenPplSoft as described above, you are ready to start making changes. Due to the proprietary nature of PeopleSoft and the complexities of this project, I realize that the barrier to entry is quite steep, but I encourage you to contact me if you come across this and harbor even a fleeting interest or curiousity.

[ps-post1]: http://mattjquinn.com/2013/09/09/preparing-pscs-client-workstation.html
[ops-javadocs]: http://openpplsoft.org/javadocs/
