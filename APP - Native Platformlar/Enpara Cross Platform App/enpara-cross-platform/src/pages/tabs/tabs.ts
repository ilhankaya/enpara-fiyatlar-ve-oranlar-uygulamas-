import { Component } from '@angular/core';

import { AlertPage } from '../alert/alert';
import { HomePage } from '../home/home';

@Component({
  templateUrl: 'tabs.html'
})
export class TabsPage {

  tab1Root = HomePage;
  tab2Root = AlertPage;

  constructor() {

  }
}
