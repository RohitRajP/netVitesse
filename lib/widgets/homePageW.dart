import 'package:flutter/material.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';

final Shader linearGradient = LinearGradient(
  colors: <Color>[Color(0xffE100FF), Color(0xff7F00FF)],
).createShader(Rect.fromLTWH(0.0, 0.0, 200.0, 70.0)),
    callActionActivate = LinearGradient(
  colors: <Color>[Color(0xff38ef7d), Color(0xff11998e)],
).createShader(Rect.fromLTWH(0.0, 0.0, 200.0, 70.0));

Widget welcomeText(context) {
  return Text(
    "Welcome to",
    style: TextStyle(
        fontWeight: FontWeight.bold,
        fontSize: MediaQuery.of(context).size.width * 0.1,
        foreground: Paint()..shader = linearGradient),
  );
}

Widget netVitesseText(context) {
  return Text(
    "NetVitesse",
    style: TextStyle(
        fontWeight: FontWeight.bold,
        fontSize: MediaQuery.of(context).size.width * 0.15,
        foreground: Paint()..shader = linearGradient),
  );
}

Widget descText(context) {
  return Text(
    "Have you ever been frustrated wondering about your internet connection or it's speed?\n\nFear no more!",
    textAlign: TextAlign.center,
    style: TextStyle(
        fontStyle: FontStyle.italic,
        fontSize: MediaQuery.of(context).size.width * 0.05,
        foreground: Paint()..shader = linearGradient),
  );
}

Widget callForActionText(context) {
  return Text(
    "Press Activate to begin",
    style: TextStyle(
        fontWeight: FontWeight.normal,
        fontSize: MediaQuery.of(context).size.width * 0.047,
        color: Colors.green),
  );
}

Widget aboutAppBtn(context) {
  return RaisedButton.icon(
    icon: Icon(FontAwesomeIcons.infoCircle),
    label: Text(
      "About App",
      style: TextStyle(fontSize: 18.0),
    ),
    color: Color(0xff7F00FF),
    onPressed: () {},
    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20.0)),
    textColor: Colors.white,
  );
}

Widget viewCodeBtn(context) {
  return RaisedButton.icon(
      color: Colors.black,
      onPressed: () {},
      icon: Icon(
        FontAwesomeIcons.github,
        color: Colors.white,
      ),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20.0)),
      label: Text(
        "View Code",
        style: TextStyle(color: Colors.white,fontSize: 16.0),
      ));
}
