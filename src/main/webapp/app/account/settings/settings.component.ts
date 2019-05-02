import { Component, OnInit, ElementRef } from '@angular/core';
import { JhiLanguageService } from 'ng-jhipster';

import { AccountService, JhiLanguageHelper } from 'app/core';

import { JhiDataUtils } from 'ng-jhipster';

@Component({
    selector: 'jhi-settings',
    templateUrl: './settings.component.html'
})
export class SettingsComponent implements OnInit {
    error: string;
    success: string;
    settingsAccount: any;
    languages: any[];

    constructor(
        private dataUtils: JhiDataUtils,
        protected elementRef: ElementRef,
        private accountService: AccountService,
        private languageService: JhiLanguageService,
        private languageHelper: JhiLanguageHelper
    ) {}

    ngOnInit() {
        this.accountService.identity().then(account => {
            this.settingsAccount = this.copyAccount(account);
        });
        this.languageHelper.getAll().then(languages => {
            this.languages = languages;
        });
    }

    save() {
        if (this.settingsAccount && this.settingsAccount.imageContentType) {
            this.settingsAccount.imageUrl = 'data:' + this.settingsAccount.imageContentType + ';base64,' + this.settingsAccount.image;
        }
        this.accountService.save(this.settingsAccount).subscribe(
            () => {
                this.error = null;
                this.success = 'OK';
                this.accountService.identity(true).then(account => {
                    this.settingsAccount = this.copyAccount(account);
                });
                this.languageService.getCurrent().then(current => {
                    if (this.settingsAccount.langKey !== current) {
                        this.languageService.changeLanguage(this.settingsAccount.langKey);
                    }
                });
            },
            () => {
                this.success = null;
                this.error = 'ERROR';
            }
        );
    }

    copyAccount(account) {
        const data = {
            image: null,
            imageContentType: null
        };
        if (account.imageUrl && account.imageUrl.indexOf(';base64,')) {
            const fileData = account.imageUrl.split(';base64,');
            data.image = fileData[1];
            data.imageContentType = fileData[0].substr(5);
        }
        return Object.assign(
            {
                activated: account.activated,
                email: account.email,
                firstName: account.firstName,
                langKey: account.langKey,
                lastName: account.lastName,
                login: account.login,
                userProfiles: account.userProfiles,
                imageUrl: account.imageUrl
            },
            data
        );
    }

    // Avatar support
    byteSize(field) {
        return this.dataUtils.byteSize(field);
    }

    openFile(contentType, field) {
        return this.dataUtils.openFile(contentType, field);
    }

    setFileData(event, entity, field, isImage) {
        this.dataUtils.setFileData(event, entity, field, isImage);
    }

    clearInputImage(field: string, fieldContentType: string, idInput: string) {
        this.dataUtils.clearInputImage(this.settingsAccount, this.elementRef, field, fieldContentType, idInput);
    }
}
