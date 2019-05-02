import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { NgbDateAdapter } from '@ng-bootstrap/ng-bootstrap';

import { NgbDateMomentAdapter } from './util/datepicker-adapter';
import {
    AppSharedLibsModule,
    AppSharedCommonModule,
    JhiLoginModalComponent,
    HasAnyAuthorityDirective,
    PasswordStrengthBarComponent
} from './';

@NgModule({
    imports: [AppSharedLibsModule, AppSharedCommonModule],
    declarations: [JhiLoginModalComponent, HasAnyAuthorityDirective, PasswordStrengthBarComponent],
    providers: [{ provide: NgbDateAdapter, useClass: NgbDateMomentAdapter }],
    entryComponents: [JhiLoginModalComponent],
    exports: [AppSharedCommonModule, JhiLoginModalComponent, HasAnyAuthorityDirective, PasswordStrengthBarComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AppSharedModule {
    static forRoot() {
        return {
            ngModule: AppSharedModule
        };
    }
}
