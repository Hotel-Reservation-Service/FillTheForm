#Changelog

## v0.8.1
- Fixed changing items on Pre-Lollipop devices #22.
- AccessibilityNodeInfo replaced with AccessibilityNodeInfoCompat.
- Menu icons dimensions changed.
- Android Plugin for Gradle updated to 2.0.0.

## v0.8.0
- FillTheForm app UI update #20: Added new icon. Changed theme colors. Package item button style changed.
- Added new constant (INTENT_EXTRA_SHOW_CONFIGURATION_SUCCESS_MESSAGE) to FillTheFormCompanion and MyAccessibilityService.
- Added FillTheFormConfigurator-v0.2.0 to tools folder.

## v0.7.0
- FillTheFormCompanion #18: Created an Android library which enables communication with FillTheForm service. Added examples of using FillTheForm with Espresso.

## v0.6.0
- Label attribute for configuration item #15: Now we can describe the value using the label attribute.

## v0.5.0
- Sdcard as configuration file source #12: We are able to load the configuration file from sdcard using just adb commands.

## v0.4.1
- Bugfix #8: Configured app is not restarted any more. Wrong intent flag was removed.

## v0.4.0
- Paste action #6: The user now has the option to paste the text into EditText fields. The text is pasted when user performs long click on the item in FillTheFormDialog.

## v0.3.0
- Fast mode #4: Introduced the new mode which increases the speed of input. Fast mode enables the user to populate EditText fields using focus change and click events.

## v0.2.0
- Configuration file variables support #1: Added support for variables in configuration file. Now we are able to use device information (Device model, Android version, IP Address etc.) as input for EditText fields. Using random values is easier and more flexible than before.

## v0.1.0
- Initial release