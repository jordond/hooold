#Hooold
A SMS scheduling app for android.  Ever needed to send a message at a later time and always relied on yourself remembering?
Well that is a thing of the past with hooold.  Schedule future text messages so that you never miss an anniversary or birthday ever again.

##Changelog
```
Version 0.1
 - Active development
 - Opening the source
```
##Features
- Schedule future sms messages
- Send to multiple recipients
- Send long messages that will be split
- History tracking with success and failures

##TODO
- Add repeat messages (weekly, daily, monthly, etc.)
- Add the ability to send MMS messages
- Integrate Facebook capabilities

##Get It
It is not yet published so just download the source and compile yourself.

##Permissions Breakdown
- Internet - Used for the crash reporting analytics
- Read Contacts - Used to choose recipients for the message
- Send SMS - Fairly obvious, send the scheduled messages

##Libraries used
```groovy
compile 'de.psdev.licensesdialog:licensesdialog:1.7.0'
compile 'com.jakewharton:butterknife:7.0.1'
compile 'com.afollestad:material-dialogs:0.7.6.0'
compile 'com.crashlytics.sdk.android:crashlytics:2.5.1@aar'
compile 'com.github.satyan:sugar:1.4'
compile 'com.wdullaer:materialdatetimepicker:1.4.2'

// Personal repo
compile 'ca.hoogit:chips:1.0.6@aar'
```

##License

Hooold
Copyright (C) 2015  Jordon de Hoog

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
