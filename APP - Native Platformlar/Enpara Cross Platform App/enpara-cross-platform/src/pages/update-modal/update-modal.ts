import { Component } from '@angular/core';
import { ViewController, NavParams, ToastController } from 'ionic-angular';

import { ApiProvider } from '../../providers/api/api';

@Component({
  selector: 'page-update-modal',
  templateUrl: 'update-modal.html',
})
export class UpdateModal {
  title: string = '';
  data: any;
  currencyTypes: string[] = ['USD', 'EUR', 'GOLD'];
  alertTypes: string[] = ['BUY', 'SELL'];
  
  currency: string;
  alertType: string;
  value: number;

  currencies: any[] = [];
  initValue: any;

  constructor(
    public viewCtrl: ViewController, 
    public navParams: NavParams, 
    public apiProvider: ApiProvider, 
    public toastCtrl: ToastController
  ) {
    this.data = navParams.get('data');
    this.apiProvider.getCurrencies().subscribe((currencies) => {
      this.currencies = currencies;

      if (this.data) {
        this.title = 'Update Alert';
        this.currency = this.data.currency_type;
        this.alertType = this.data.alert_type;
        this.value = this.data.value;
      } else {
        this.title = 'Add Alert';
        this.currency = this.currencyTypes[0];
        this.alertType = this.alertTypes[0];

        this.initValue = this.currencies.filter(item => item.currency_type == this.currency)[0];
        this.value = (this.alertType == 'BUY') ? this.initValue.buy_value : this.initValue.sell_value;
      }
    });
    
    
  }

  ionViewDidLoad() {}

  saveAlert() {
    let updated = {
      currency_type: this.currency,
      alert_type: this.alertType,
      value: this.value
    };

    if (this.data) {
      this.apiProvider.updateAlert(this.data.$key, updated)
        .then(() => {
          this.showMessage('Updated successfully.');
          this.close();
        });
    } else {
      this.apiProvider.addAlert(updated)
        .then(() => {
          this.showMessage('Added successfully.');
          this.close();
        });
    }
  }

  selectChanged(event: any) {
    this.initValue = this.currencies.filter(item => item.currency_type == this.currency)[0];
    this.value = (this.alertType == 'BUY') ? this.initValue.buy_value : this.initValue.sell_value;
  }

  close() {
    if (this.viewCtrl)
      this.viewCtrl.dismiss();
    this.viewCtrl = null;
  }

  private showMessage(message: string) {
    this.toastCtrl.create({
      message: message,
      duration: 2000
    }).present();
  }

}
