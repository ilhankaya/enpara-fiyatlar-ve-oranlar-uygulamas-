import { Injectable } from '@angular/core';
import { AngularFireDatabase, FirebaseListObservable } from 'angularfire2/database';
import * as firebase from 'firebase/app';

@Injectable()
export class ApiProvider {

  currencyList: FirebaseListObservable<any>;
  alertList: FirebaseListObservable<any>;

  constructor(public db: AngularFireDatabase) {
    this.currencyList = this.db.list('/enpara_currency');
    this.alertList = this.db.list('/enpara_alert');
  }

  getCurrencies() {
    return this.currencyList;
  }

  addCurrency(data: any) {
    return this.currencyList.push(data);
  }

  updateCurrency(key: string, data: any) {
    data.timestamp = firebase.database['ServerValue']['TIMESTAMP'];
    return this.db.object(`/enpara_currency/${key}`).update(data);
  }

  deleteCurrency(key: string) {
    return this.currencyList.remove(key);
  }

  getAlerts() {
    return this.alertList;
  }

  addAlert(data: any) {
    data.timestamp = firebase.database['ServerValue']['TIMESTAMP'];
    return this.alertList.push(data);
  }

  updateAlert(key: string, data: any) {
    data.timestamp = firebase.database['ServerValue']['TIMESTAMP'];
    return this.db.object(`/enpara_alert/${key}`).update(data);
  }

  deleteAlert(key: string) {
    return this.alertList.remove(key);
  }

}
