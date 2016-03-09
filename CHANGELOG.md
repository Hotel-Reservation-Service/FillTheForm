#Changelog

## Next version
- Label attribute for configuration item #15: Now we can describe the value using the label attribute.
- Sdcard as configuration file source #12: We are able to load the configuration file from sdcard using just adb commands.
- Bugfix #8: Configured app is not restarted any more. Wrong intent flag was removed.
- Paste action #6: The user now has the option to paste the text into EditText fields. The text is pasted when user performs long click on the item in FillTheFormDialog.
- Fast mode #4: Introduced the new mode which increases the speed of input. Fast mode enables the user to populate EditText fields using focus change and click events.
- Configuration file variables support #1: Added support for variables in configuration file. Now we are able to use device information (Device model, Android version, IP Address etc.) as input for EditText fields. Using random values is easier and more flexible than before.

## v0.1.0
- Initial release