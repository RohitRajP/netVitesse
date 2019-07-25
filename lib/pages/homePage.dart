import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import '../widgets/homePageW.dart';

class HomePage extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    // TODO: implement createState
    return _HomePageState();
  }
}

class _HomePageState extends State<HomePage> {
  static const platform = const MethodChannel('com.a011.netvitesse');
  bool serviceStatus = false;

  // function to open github repository
  void openGithubPage() {
    platform.invokeMethod('openGit');
  }

  // widget for the service activation FAB
  Widget _serviceActivateFAB() {
    return FloatingActionButton.extended(
        onPressed: () async {
          _checkUsagePermission();
        },
        backgroundColor: (serviceStatus == false) ? Colors.green : Colors.red,
        icon: (serviceStatus == false) ? Icon(Icons.check) : Icon(Icons.cancel),
        label:
            (serviceStatus == false) ? Text("Activate") : Text("Deactivate"));
  }

  // settings fab button
  Widget _settingsFAB() {
    return FloatingActionButton.extended(
      onPressed: () {},
      icon: Icon(Icons.settings),
      label: Text("Settings"),
      backgroundColor: Colors.black,
    );
  }

  Widget _aboutFAB() {
    return FloatingActionButton.extended(
      onPressed: () {
        openAboutDialog(context, openGithubPage);
      },
      label: Text("About"),
      icon: Icon(Icons.info),
      backgroundColor: Colors.indigo,
    );
  }

  // Used to display multiple FABs
  Widget _floatingActionButton() {
    return Column(
      mainAxisAlignment: MainAxisAlignment.end,
      children: <Widget>[
        _aboutFAB(),
        SizedBox(
          height: 10.0,
        ),
//        _settingsFAB(),
//        SizedBox(
//          height: 10.0,
//        ),
        _serviceActivateFAB()
      ],
    );
  }

  void _checkStatus() async {
    String result = await platform.invokeMethod("checkStatus");
    if (result == "Service Running") {
      setState(() {
        serviceStatus = true;
      });
    }
  }

  // to check if usage permissions are given before starting service
  void _checkUsagePermission() async {

    // checking if the user has provided usage permissions
    bool usageResult = await platform.invokeMethod("checkUsagePermission");

    bool phoneStatePermission = await platform.invokeMethod("getPhoneStatePermission");

    // if true, start service
    if (usageResult && phoneStatePermission) {
      // calling appropriate java function and getting response
      String result = (serviceStatus == false)
          ? await platform.invokeMethod("startService")
          : await platform.invokeMethod("stopService");
      // updating UI based on response
      if (result == "Service Started") {
        // setting service status as Activated
        setState(() {
          serviceStatus = true;
        });
      } else if (result == "Service Cancelled") {
        // setting service status as Deactivated
        setState(() {
          serviceStatus = false;
        });
      } else if (result == "Service Failed") {
        // showing snackbar for error message
        final snackBar = SnackBar(
          content: Text('Apologies. Error occured'),
          backgroundColor: Colors.red,
          duration: Duration(seconds: 3),
        );

        // Find the Scaffold in the widget tree and use it to show a SnackBar.
        Scaffold.of(context).showSnackBar(snackBar);
      }
    } else {
      _getUsagePermission();
    }
  }

  void _getUsagePermission() {
    showDialog(
        context: context,
        child: AlertDialog(
          title: Text("Permission Required"),
          backgroundColor: Colors.white,
          content: Text(
              "NetVitesse requires usage access and telephone permissions to be able to wield it's magic. \n\nUsage Access permission is to help calculate how much data is being used with WiFi\n\nTelephone permission is used to calculate how much data is consumed with mobile data", style: TextStyle(letterSpacing: 1.5),),
          shape:
              RoundedRectangleBorder(borderRadius: BorderRadius.circular(5.0)),
          actions: <Widget>[
            Column(
              crossAxisAlignment: CrossAxisAlignment.end,
              children: <Widget>[
                FlatButton(
                    onPressed: () {
                      Navigator.pop(context);
                      platform.invokeMethod("getUsageAccessPermission");
                    },
                    shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(3.0)),
                    color: Colors.green,
                    textColor: Colors.white,
                    child: Text("Grant Usage Permissions"))
              ],
            )
          ],
        ));
  }

  void getPhonePermission() async{
    // getting phone permission
    bool phoneStatePermission = await platform.invokeMethod("getPhoneStatePermission");
  }

  @override
  void initState() {
    // TODO: implement initState
    super.initState();

    // get phone permission
    getPhonePermission();

    // check current service status
    _checkStatus();

  }

  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    return SafeArea(
      child: Scaffold(
        floatingActionButtonLocation: FloatingActionButtonLocation.endFloat,
        floatingActionButton: _floatingActionButton(),
        body: Container(
          margin: EdgeInsets.only(bottom: 50.0),
          padding: EdgeInsets.all(20.0),
          child: Center(
            child: ListView(
              shrinkWrap: true,
              children: <Widget>[
                // Contains "Welcome to" text
                Container(
                  alignment: Alignment.center,
                  width: MediaQuery.of(context).size.width * 0.8,
                  child: welcomeText(context),
                ),

                // Contains "NetVitesse" text
                Container(
                  alignment: Alignment.center,
                  width: MediaQuery.of(context).size.width * 0.8,
                  child: netVitesseText(context),
                ),

                // Contains description text
                Container(
                  padding: EdgeInsets.only(left: 20.0, right: 20.0),
                  alignment: Alignment.center,
                  width: MediaQuery.of(context).size.width * 0.8,
                  child: descText(context),
                ),

                // Contains about app button
                Container(
                  margin: EdgeInsets.only(top: 10.0),
                  alignment: Alignment.center,
                  width: MediaQuery.of(context).size.width * 0.8,
                  child: aboutApp(context),
                ),

                // Contains view code button
//                Container(
//                  margin: EdgeInsets.only(top: 20.0),
//                  alignment: Alignment.center,
//                  child: viewCodeBtn(context),
//                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

//// Contains call for action
//Container(
//margin: EdgeInsets.only(top: 20.0),
//alignment: Alignment.center,
//child: callForActionText(context),
//),
