# HS-Loadout-Generator-GUI

## Random Hunt:Showdown loadouts for desktop

### Overview

This software is a random generator of loadouts for the Hunt:Showdown game by Crytek for Windows desktop.
It looks something as below (at the moment being):

<img src="https://github.com/SerhiiPok/hs-loadout-gen-files/blob/main/guiCapture.PNG?raw=true" align="center" width="1100px" hspace="0px" vspace="20px">

### Features

#### Smart random generation with filters

Generates a random loadout under restrictions. You can filter the random outcomes by price, required bloodline rank, available melee items, and other parameters. This also allows to prevent generation of loadouts with only melee weapons or with no healing items. As well, this allows to generate budget loadouts or the expensive ones by your choice.

#### Equip custom loadouts

The generator allows to equip your own loadout by choosing items from drop-down menus.

#### Specs

There is an info-section which shows your loadout's specs. You can see the price, best melee damage that you can deal, best headshot range, and others.

## Versioning and download

Versioning follows the x.y pattern where x is the major release and y is the bug fix. A latest release .msi installer for Windows 10 64 bit is available below. At the moment, if you want to upgrade the software, delete the old instance via software center, download the latest .msi installer and run it.

Current release: [1.1](https://www.dropbox.com/s/43si839d9ua81r6/Hunt%20Showdown%20Random%20Loadouts-1.1.msi?dl=0).

## Implementation info

### GUI
GUI is done using Java FX.

### Packaging
Packaging is done by jpackage, using jdk 17. The result is an .msi installer that installs an application along with a shortcut and a Windows menu entry.

### Caveats
To launch the project from Intellij, both platform-specific binaries for JavaFX *and* VM arguments to locate required modules will be needed.

### Game items library
The GUI application uses the game items and types library provided in HuntShowdownTypes, also in author's github. This module has to be maven-installed before 
compiling the GUI.

## Database of in-game items
There is a copy of the full collection of the game items distributed along with the GUI application, but this is never guaranteed to be up-to-date. Instead,
the application will try to connect to a REST API at start-up and download the collection of game items from there. The author does his best to manage the 
collection and keep it updated. 

## Issues and requests
Feel free to raise issues and requests in case project or installer are not working properly.
