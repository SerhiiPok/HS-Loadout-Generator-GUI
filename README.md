# HS-Loadout-Generator-GUI

## Random Hunt:Showdown loadouts for desktop
This software is a random generator of loadouts for the Hunt:Showdown game by Crytek for Windows desktop.
It looks something as below (at the moment being):

<img src="https://github.com/SerhiiPok/hs-loadout-gen-files/blob/main/guiCapture.PNG?raw=true" align="center" width="1100px" hspace="0px" vspace="20px">

As you can see, the generator lets you apply filters to the random generator, filtering for price, required rank, and the different loadout properties, like
its healing capacity, melee loadout etc. The filter system is fast when it comes to performance and flexible, so additional filters can be added by the developer 
easily.

## GUI
GUI is done using Java FX.

## Packaging
Packaging is done by jpackage, using jdk 17. The result is an .msi installer that installs an application along with a shortcut and a Windows menu entry.

## Game items
There is a copy of the full collection of the game items distributed along with the GUI application, but this is never guaranteed to be up-to-date. Instead,
the application will try to connect to a REST API at start-up and download the collection of game items from there. The author does his best to manage the 
collection and keep it updated. This REST API Spring application is also accessible somewhere in author's Github. To access an up-to-date version of game items
collection, the REST API may be called directly from browser.

## Game items library
The GUI application uses the game items and types library provided in HuntShowdownTypes, also in author's github. This module has to be maven-installed before 
compiling the GUI.

## Distribution
A Windows .msi installer is available. This has not been extensively tested, (tested on a 64-bit Windows 10 machine),
so it may fail for various reasons. The application installed includes a Java custom
runtime environment, so no Java has to installed to launch the application. 
The .msi installer is publicly available [here](https://www.dropbox.com/s/pa6c6upyl3o6fh3/Hunt%20Showdown%20Random%20Loadouts-1.0.msi?dl=0)

## Caveats
To launch the project from Intellij, both platform-specific binaries for JavaFX *and* VM arguments to locate required modules will be needed.

## Issues and requests
Feel free to raise issues and requests in case project or installer are not working properly.
