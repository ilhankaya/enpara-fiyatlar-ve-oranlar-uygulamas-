import { Component } from '@angular/core';
import { NavController, AlertController, ToastController } from 'ionic-angular';

import { ApiProvider } from '../../providers/api/api';

@Component({
  selector: 'page-home',
  templateUrl: 'home.html'
})
export class HomePage {
  currencyList: any;

  constructor(
    public navCtrl: NavController, 
    public apiProvider: ApiProvider, 
    public alertCtrl: AlertController, 
    public toastCtrl: ToastController
  ) {
    this.currencyList = this.apiProvider.getCurrencies();
  }

  addCurrency() {
    this.showDialog();
  }

  updateCurrency(data: any) {
    this.showDialog(data);
  }

  deleteCurrency(key: string) {
    this.apiProvider.deleteCurrency(key)
      .then(() => this.showMessage('Removed successfully.'));
  }

  private showDialog(item?: any) {
    this.alertCtrl.create({
      title: !item ? 'Add Currency' : 'Update Currency',
      inputs: [
        {
          name: 'currency_type',
          placeholder: 'CURRENCY TYPE',
          value: (item && item.currency_type) || ''
        },
        {
          name: 'buy_value',
          placeholder: 'BUY',
          type: 'number',
          value: (item && item.buy_value) || 0.0
        },
        {
          name: 'sell_value',
          placeholder: 'SELL',
          type: 'number',
          value: (item && item.sell_value) || 0.0
        }
      ],
      buttons: [
        {
          text: 'Cancel',
          role: 'cancel',
          handler: data => {
            console.log('Cancel clicked');
          }
        },
        {
          text: 'Save',
          handler: data => {
            if (item) {
              this.apiProvider.updateCurrency(
                item.$key, 
                {
                  currency_type: data.currency_type,
                  sell_value: data.sell_value,
                  buy_value: data.buy_value
                }).then(() => this.showMessage('Updated successfully.'));
            } else {
              this.apiProvider.addCurrency({
                currency_type: data.currency_type,
                sell_value: data.sell_value,
                buy_value: data.buy_value
              }).then(() => this.showMessage('Added successfully.'));
            }
          }
        }
      ]
    }).present();
  }

  private showMessage(message: string) {
    this.toastCtrl.create({
      message: message,
      duration: 2000
    }).present();
  }

}
