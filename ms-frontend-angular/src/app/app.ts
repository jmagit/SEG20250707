import { Component, ViewContainerRef } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavigationService, WindowService } from './common-services';
import { AjaxWait, Footer, Header, NotificationModal } from './main-components';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Header, Footer, AjaxWait, NotificationModal],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  constructor(_cache: NavigationService, viewContainerRef: ViewContainerRef, window: WindowService) {
    window.RootViewContainerRef = viewContainerRef
  }
}
