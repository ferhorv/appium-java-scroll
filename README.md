# appium-java-scroll

This is a sample project with a scrolling helper for Appium using the java client.

#Setup
* start an appium server on localhost and the default 4723 port
* Android: attach device or use emulator
* iOS: only simulator supported as test is using a dev app currently

For Android enable indicators for touch and pointer locations to verify/debug gestures.
Achieve this by one of the following approach:
* manually on the device in developer settings
* executing the followings in command line:
  * adb shell settings put system show_touches 1
  * adb shell settings put system pointer_location 1