import { NgClass } from '@angular/common';
import { Component } from '@angular/core';
import { NotificationService } from 'src/app/common-services';

@Component({
  selector: 'app-notification-modal',
  standalone: true,
  imports: [NgClass],
  templateUrl: './notification-modal.html',
  styleUrl: './notification-modal.css'
})
export class NotificationModal {
  constructor(private vm: NotificationService) { }
  public get VM() { return this.vm; }
}
