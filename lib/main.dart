import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import './pages/homePage.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    // TODO: implement createState
    return _MyAppState();
  }

}

class _MyAppState extends State<MyApp>{

  @override
  void initState() {
    // TODO: implement initState
    super.initState();

    // setting navigation bar and status bar colors
    SystemChrome.setSystemUIOverlayStyle(SystemUiOverlayStyle(
      systemNavigationBarColor: Colors.black,
      systemNavigationBarIconBrightness: Brightness.dark,
      statusBarColor: Colors.black,
      statusBarBrightness: Brightness.dark
    ));
  }

  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    return MaterialApp(
      title: "Net Vitesse",
      home: HomePage(),
    );
  }
}