import 'package:flutter/material.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';

final Shader linearGradient = LinearGradient(
  colors: <Color>[Color(0xffec008c), Color(0xfffc6767)],
).createShader(Rect.fromLTWH(0.0, 0.0, 200.0, 70.0)),
    callActionActivate = LinearGradient(
  colors: <Color>[Color(0xffec008c), Color(0xfffc6767)],
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

Widget _appLogo(){
  return Image.asset('assets/icon/try22.png',width: 120,height: 120);
}

Widget _title(){
  return Text(
    "NetVitesse",
    style: TextStyle(
      fontWeight: FontWeight.bold,
      fontSize: 30.0,

    ),
  );
}

Widget _version(){
  return Text(
    "Version 1.0",
    style: TextStyle(
      color: Colors.blueGrey,
      fontSize: 16.0,
    ),
  );
}

Widget _message(){
  return Text(
    "A flutter and native android cocktail, NetVitesse literally spells 'Net-Speed' due to the lack of better alternatives and, my faint memories of learning french. \n\nI hope you find as much fun using this app as I had developing it.\n\nTill the next project,\nNamaskar 😁",
    textAlign: TextAlign.center,
    style: TextStyle(
        foreground: Paint()..shader = linearGradient,
      letterSpacing: 1.5
    ),
  );
}

Widget _viewSourceBtn(openGithubPage){
  return FlatButton.icon(
      icon: Container(
        margin: EdgeInsets.only(bottom: 3.0),
        child: Icon(FontAwesomeIcons.github,size: 30,),
      ),
      label: Text("View Source", style: TextStyle(fontSize: 16.0),),
      onPressed: (){
        openGithubPage();
      },
      color: Colors.black,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20.0)),
      textColor: Colors.white,
      colorBrightness: Brightness.dark
  );
}

void openAboutDialog(context, openGithubPage){
  showDialog(
      context: context,
      child: AlertDialog(
        backgroundColor: Color(0xFFf8f9fa),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10.0)),
        content: Container(
          height: 400,
          width: 300,
          child: ListView(
            children: <Widget>[
              _appLogo(),
              SizedBox(
                height: 3.0,
              ),
              Container(
                alignment: Alignment.center,
                child: _title(),
              ),
              SizedBox(
                height: 3.0,
              ),
              Container(
                alignment: Alignment.center,
                child: _version(),
              ),
              SizedBox(
                height: 5.0,
              ),
              Container(
                alignment: Alignment.center,
                child: _message(),
              ),
            ],
          ),
        ),
        actions: <Widget>[
          Container(
            margin: EdgeInsets.only(bottom: 5.0),
            alignment: Alignment.center,
            child: _viewSourceBtn(openGithubPage),
            width: 300,
          )
        ],
      )
  );
}