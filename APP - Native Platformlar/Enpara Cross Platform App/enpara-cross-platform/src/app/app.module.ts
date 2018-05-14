import { NgModule, ErrorHandler } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { IonicApp, IonicModule, IonicErrorHandler } from 'ionic-angular';
import { MyApp } from './app.component';

import { AlertPage } from '../pages/alert/alert';
import { HomePage } from '../pages/home/home';
import { TabsPage } from '../pages/tabs/tabs';
import { UpdateModal } from '../pages/update-modal/update-modal';

import { StatusBar } from '@ionic-native/status-bar';
import { SplashScreen } from '@ionic-native/splash-screen';
import { ApiProvider } from '../providers/api/api';

import { AngularFireModule } from 'angularfire2';
import { AngularFireDatabaseModule } from 'angularfire2/database';

const firebaseConfig = {
  apiKey: "AIzaSyCtBCMdgTkLq53ogmHQ1DdY23L1lX_t0tE",
  authDomain: "enpara-mobile.firebaseapp.com",
  databaseURL: "https://enpara-mobile.firebaseio.com",
  projectId: "enpara-mobile",
  storageBucket: "enpara-mobile.appspot.com",
  messagingSenderId: "163927835703"
}

@NgModule({
  declarations: [
    MyApp,
    AlertPage,
    HomePage,
    TabsPage,
    UpdateModal
  ],
  imports: [
    BrowserModule,
    IonicModule.forRoot(MyApp),
    AngularFireModule.initializeApp(firebaseConfig),
    AngularFireDatabaseModule
  ],
  bootstrap: [IonicApp],
  entryComponents: [
    MyApp,
    AlertPage,
    HomePage,
    TabsPage,
    UpdateModal
  ],
  providers: [
    StatusBar,
    SplashScreen,
    {provide: ErrorHandler, useClass: IonicErrorHandler},
    ApiProvider
  ]
})
export class AppModule {}
