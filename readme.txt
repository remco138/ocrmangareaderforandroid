A continuation/fork of "Ocr manga reader for android".

Roadmap:
✓ Don't add a space when exporting to ankidroid, this breaks anki(droid)'s nice furigana feature
✗ Improve gradle support (doesn't seem to want to build a release apk without disabling lintVitalRelease)
✗ Decouple edits made to library "tess-two" (with decorator pattern). Would make updating tess-two a breeze
✗ Use newer AnkiDroid API if available. Necessary for adding more than 2 fields
✗ Pull vocab audio from jpod101 
   ✗ Also include vocab audio when exporting vocab to an anki card
✗ Somehow integrate with J-J dictionary Sanseido
   ✗ Include senseido J definitions when exporting an Ankidroid card (as per anku plugin https://ankiweb.net/shared/info/1967553085)
✗ Actually put some effort into this readme

Setting up a dev environment and running the mangareader:
1: In Android Studio, open the file build.gradle, it will actually import the project
2: Android Studio will complain about a missing SDK folder and correct it automatically
3: get the appropriate SDK for your phone (ctrl-shift-a "android sdk")
4: Probably need so set up the NDK too. I Will document this if Necessary Soon(tm)

4:
run ./gradlew assembleRelease -x lintVitalRelease, or hook your phone onto your PC, enable debugging mode, and start instant run in intellij


Original readme.txt:
Website:
http://ocrmangareaderforandroid.sourceforge.net/

--------------------------
[Version 6.1 (26-August-2016)]
● Add border on trigger captures
● Added "Force Border" option
● Removed "Strip Furigana" option (always on now)
● Removed "Enhance" button (always on now)
● Removed "Vibrate" permission (was never used)

[Version 6.0 (21-August-2016)]
● Upon entering OCR mode, user may now tap text instead of drawing a box.
● Removed legacy ACV cruft.

[Version 5.2 (29-May-2016)]
● Added an "Auto" text orientation option.

[Version 5.1 (20-January-2016)]
● Updated to Tesseract v3.05 dev (160105 snapshot)

[Version 5.0 (30-December-2015)]
● Added two-page layout.
● Added ability to take a photo.

[Version 4.1 (31-July-2015)]
● Added support for optional kanji dictionary.

[Version 4.0 (2-July-2015)]
● Added ability to save to a word list.
● Added support for optional name dictionary.

[Version 3.2 (12-June-2015)]
● Added bluetooth keyboard shortcuts.
● Holding the primary (left) mouse button while scrolling the mouse wheel up/down will zoom in/out.
● The Vert/Horiz button is slighter wider to prevent text wrapping on some devices.

[Version 3.1 (13-May-2015)]
● Updated the EDICT and frequency databases.

[Version 3.0 (30-December-2014)]
● Added frequency information.
● Added ability to lookup the next/previous word.
● Added customizable buttons to the left, right, top, and bottom edges.
● Added option to set the action to take when the OCR Send button is pressed.
● Added the known/todo word marker (looks at known_words.txt and todo_words.txt)
● Added scroll amount option.
● EPWING feature now works with Android 5.0.
● Fixed one potential source of the "Unable to load image" error.
● Fixed one potential source of random crashes.
● Fixed Yahoo Jisho search paths.
● Removed the Show/Hide button.
● Minor GUI tweaks related to button sizes.
● Minor tweaks to menu text.
● Modified layout defaults.
● Optimized substitutions.
● Added link to homepage.
● Removed the "OCR: " prefix from the OCR'd text.

[Version 2.4 (11-July-2014)]
● Will now attempt to strip furigana before OCR.
● Added mouse wheel support.
● Changed some default settings (landscape, show line number, remapped some controls).

[Version 2.3 (23-March-2014)]
● Fixed bug that prevented light text on a dark background from OCRing correctly.
● Updated EDICT.
● Increased default max width and height to 1600x1600 for low memory devices.

[Version 2.2 (8-March-2014)]
● Improved OCR accuracy (Tesseract v3.02.02 + custom config + better pre-processing)
● Renamed "Binarize" to "Enhance"
● For devices with < 32MB available heap, force Low Memory Mode.
● By default, max width and max height are now set automatically.
● Added default subject line when contacting the developer.

[Version 2.1 (06-December-2013)]
● For EPWING searches, don't kanjify the word before lookup.
● For EPWING searches, if the definition is blank and parsing is enabled, display the first example sentence instead.
● When "Show screen number" is enabled, also show the total page count.
● Fixed bug in 『研究社　新和英大辞典　第５版』 search where the definition wasn't shown if it did not contain any alpha characters.

[Version 2.0 (28-November-2013)]
● Added EPWING dictionary support.
● Added more color options.

[Version 1.3 (23-November-2013)]
● Enabled "Immersive Mode" which removes the navigation bar on devices that have one. Only affects Android 4.4+ devices. To briefly show the navigation bar, swipe down from the top of the screen. Also added an Exit button to the top-right corner that can be used in addition to the back button to exit OCR mode.
● Dictionary panel should now display proper Japanese glyphs instead of Chinese glyphs when device's language is not set to Japanese. Only affects Android 4.4+ devices.

[Version 1.2 (14-September-2013)]
● Corner button taps now register even if user slides their finger a little. (Thanks Zarxrax!).
● Added the "OCR -> Layout -> Large Nudge Buttons" setting. (Thanks Zarxrax!).

[Version 1.1 (07-September-2013)]
● Changed the "OCR -> Hide/Show" setting text to "OCR -> Layout". Merely a cosmetic change.
● Added the "OCR -> Layout -> Simplified layout (portrait)" setting. The "Simplified" layout is now the default. It places the text orientation, binarize, send, and hide/show features in a menu that appears when the new Menu button is tapped. With this mode enabled, devices with smaller phone-sized screens should no longer display overlapping buttons. Tablet users might want to uncheck this option to go back to original layout and avoid the extra tap required to access these features. (Thanks all who helped with this!).
● Added the "OCR -> Layout -> Simplified layout (landscape)" setting. Same deal as above, but for landscape mode.
● Added "Advanced -> Max zoom factor" setting. (Thanks RawToast!). Changed the default from 2.0 to 5.0.
● Added "Advanced -> Zoom increment" setting. Amount to zoom by when the screen is double-tapped. Default is 2.5.
● Added auto-repeat action to the nudge buttons. (Thanks RawToast!). So you may now hold the nudge buttons down to make slight adjustments to the capture box.
● Fixed de-inflections not working when switching screen orientation.
● On single tap don't display menu if the action is set to None.

[Version 1.0 (02-September-2013)]
● Initial version.
