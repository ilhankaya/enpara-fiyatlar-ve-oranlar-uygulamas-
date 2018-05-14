import { Component } from '@angular/core';
import { ModalController, AlertController, ToastController } from 'ionic-angular';
import { UpdateModal } from '../update-modal/update-modal';

import { ApiProvider } from '../../providers/api/api';

@Component({
  selector: 'page-alert',
  templateUrl: 'alert.html'
})
export class AlertPage {
  alertList: any;

  constructor(
    public modalCtrl: ModalController, 
    public apiProvider: ApiProvider, 
    public alertCtrl: AlertController, 
    public toastCtrl: ToastController
  ) {
    this.alertList = this.apiProvider.getAlerts();
  }

  addAlert() {
    this.modalCtrl.create(UpdateModal).present();
  }

  updateAlert(data: any) {
    this.modalCtrl.create(UpdateModal, {data: data}).present();
  }

  deleteAlert(key: string) {
    this.apiProvider.deleteAlert(key)
      .then(() => this.showMessage('Removed successfully.'));
  }

  private showMessage(message: string) {
    this.toastCtrl.create({
      message: message,
      duration: 2000
    }).present();
  }

}

