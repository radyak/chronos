import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {WebAppConfig} from "../../common/model/general/webconfig/web-app.config";

@Injectable({
    providedIn: 'root'
})
export class WebAppConfigService {

    private _config!: WebAppConfig;

    constructor(private http: HttpClient) { }

    loadAppConfig() {
        return this.http.get<WebAppConfig>('/api/config/web/app-config.json')
            .toPromise()
            .then(data => {
                this._config = data!;
            });
    }

    get config() {
        return this._config;
    }
}
