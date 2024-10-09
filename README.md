Bluetooth Scanner Android App
Overview
This Android application allows users to scan for nearby Bluetooth devices, display them in a list, and view details of selected devices. It supports both classic Bluetooth and Bluetooth Low Energy (BLE) scanning on Android devices running Android 14 and above.
Features

Scan for nearby Bluetooth devices
Display a list of discovered devices
View detailed information about a selected device
Support for both classic Bluetooth and BLE scanning
Compatibility with Android 14+

Main Components
MainActivity

Handles Bluetooth initialization and permission requests
Displays the list of discovered Bluetooth devices
Provides a refresh button to restart scanning

DeviceDetailsActivity

Shows detailed information about a selected Bluetooth device

Setup

Clone the repository to your local machine.
Open the project in Android Studio.
Ensure you have the Android 14 SDK installed.
Build and run the application on a device or emulator running Android 14 or higher.

Permissions
The app requires the following permissions:

BLUETOOTH_SCAN
BLUETOOTH_CONNECT

These permissions are requested at runtime and must be granted by the user for the app to function properly.
Usage

Launch the app.
If prompted, grant the necessary Bluetooth permissions.
The app will automatically start scanning for nearby Bluetooth devices.
Discovered devices will appear in the list.
Tap the refresh button to restart the scanning process.
Tap on a device in the list to view more details about it.

Technical Notes

The app uses BluetoothAdapter for classic Bluetooth scanning and BluetoothLeScanner for BLE scanning.
Scanning automatically switches to BLE if classic Bluetooth scanning fails.
The app checks for Bluetooth support and requests the user to enable Bluetooth if it's turned off.

Future Improvements

Add functionality to connect to selected Bluetooth devices
Implement pairing with new devices
Add filtering options for discovered devices
Improve UI/UX with material design components

Contributing
Contributions to improve the app are welcome. Please feel free to submit pull requests or open issues for bugs and feature requests.
