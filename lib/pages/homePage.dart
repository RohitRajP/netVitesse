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

  Widget _floatingActionButton() {
    return FloatingActionButton.extended(
        onPressed: () async {
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
          } else if(result == "Service Cancelled"){

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
        },
        backgroundColor: (serviceStatus == false) ? Colors.green : Colors.red,
        icon: (serviceStatus == false) ? Icon(Icons.check) : Icon(Icons.cancel),
        label: (serviceStatus == false) ? Text("Activate") : Text("Deactivate"));
  }

  void _checkStatus() async{
    String result = await platform.invokeMethod("checkStatus");
    if(result == "Service Running"){
      setState(() {
        serviceStatus = true;
      });
    }
  }

  @override
  void initState(){
    // TODO: implement initState
    super.initState();

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
          color: Colors.black,
          padding: EdgeInsets.all(20.0),
          child: Center(
            child: ListView(
              shrinkWrap: true,
              children: <Widget>[
                // Contains "Welcome to" text
                Container(
                  alignment: Alignment.center,
                  child: welcomeText(context),
                ),

                // Contains "NetVitesse" text
                Container(
                  alignment: Alignment.center,
                  child: netVitesseText(context),
                ),

                // Contains description text
                Container(
                  padding: EdgeInsets.only(left: 20.0, right: 20.0),
                  alignment: Alignment.center,
                  child: descText(context),
                ),

                // Contains about app button
//                Container(
//                  margin: EdgeInsets.only(top: 30.0),
//                  alignment: Alignment.center,
//                  child: aboutAppBtn(context),
//                ),

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
