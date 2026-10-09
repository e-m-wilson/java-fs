import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/shared/angular/app.config';
import { App } from './app/shared/angular/app';

bootstrapApplication(App, appConfig)
  .catch((err) => console.error(err));
