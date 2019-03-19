//
//  NetworkService.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 30/05/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit
import Alamofire
import AlamofireActivityLogger
import OAuthSwift
import SwiftKeychainWrapper

enum Result<Model> {
    case success(Model)
    case failure(Error)
}

typealias ConnectionCompletionHandler = (_ result: Result<Any>) -> ()

let keychainWrapperCredentialString = "credential"

final class NetworkService {
    
    private let REQUEST_TOKEN_HEADER = "Token-Requested"
    private let AUTH_REQUIRED_HEADER = "Authentication-Required"
    private let AUTHORIZATION_HEADER = "Authorization"
    
    private let OAUTH_REALM = "realm=\"API\", "
    private let OAUTH_CONSUMER_KEY = "oauth_consumer_key"
    private let OAUTH_NONCE = "oauth_nonce"
    private let OAUTH_SIGNATURE = "oauth_signature"
    private let OAUTH_SIGNATURE_METHOD = "oauth_signature_method"
    private let OAUTH_SIGNATURE_METHOD_VALUE = "HMAC-SHA1"
    private let OAUTH_TIMESTAMP = "oauth_timestamp"
    private let OAUTH_ACCESS_TOKEN = "oauth_token"
    private let OAUTH_VERSION = "oauth_version"
    private let OAUTH_VERSION_VALUE = "1.0"
    private let ONE_SECOND = 1000
    
    // Temporarly token used only for request access token
    private var oAuthRequestToken: OAuthTokenModel?
    
    // Place with sensitive data such as authentication tokens
    private var credentials: Credentials

    // Values used for OAuth authentication
    private var signCredentials = OAuthSwiftCredential(consumerKey: Config.CONSUMER_KEY, consumerSecret: Config.CONSUMER_SECRET)
    
    let baseURL: String
    
    private var _privateManager: SessionManager?
    var manager:SessionManager{
        get{
            if let manager = _privateManager{
                return manager
            }
            
            let configuration = URLSessionConfiguration.default
            configuration.timeoutIntervalForRequest = 60
            configuration.timeoutIntervalForResource = 60
            
            _privateManager = Alamofire.SessionManager(configuration: configuration)
            //            _privateManager!.retrier = self
            //            _privateManager!.adapter = self
            return _privateManager!
        }
        set(newVal){
            _privateManager = manager
        }
    }
    
    init() {
        credentials = (KeychainWrapper.standard.object(forKey: keychainWrapperCredentialString) as? Credentials) ?? Credentials()
        self.baseURL = Config.BASE_URL
        
        if SharedGlobals.shared.isFirstUse(){
            logout()
        }
        setSignCredentials(oauthToken: credentials.oauthTokenModel?.token ?? "", oauthTokenSecret: credentials.oauthTokenModel?.tokenSecret ?? "")
    }

    func logout(){
        updateUserCredentials(oAuthTokenModel: nil)
    }
    
    func isLoggedIn() -> Bool {
        return credentials.isLoggedIn()
    }
    
    func setSignCredentials(oauthToken: String, oauthTokenSecret: String){
        signCredentials.oauthToken = oauthToken
        signCredentials.oauthTokenSecret = oauthTokenSecret
    }
    
    func updateUserCredentials(oAuthTokenModel: OAuthTokenModel?){
        
        credentials.oauthTokenModel = oAuthTokenModel
        if let oAuthTokenModel = oAuthTokenModel, oAuthTokenModel.isValid(){
            setSignCredentials(oauthToken: oAuthTokenModel.token, oauthTokenSecret: oAuthTokenModel.tokenSecret)
        }
        else{
            setSignCredentials(oauthToken: "", oauthTokenSecret: "")
        }
        
        KeychainWrapper.standard.set(credentials, forKey:keychainWrapperCredentialString)
    }

    
    private func getNonce() -> String {
        let uuid: String = UUID().uuidString
        return uuid[0..<8]
    }
    
    private func URLString(restAction: RestAction) ->String
    {
        let url = baseURL + restAction.endpoint
        return url
    }
    
    
    func performRequest<responseModel: Decodable>(with action:RestAction, responseModelType: responseModel.Type, params: Parameters? = nil, completionHandler: ConnectionCompletionHandler?)
    {
        
        performRequest(with: action, responseModelType: responseModelType, urlSuffix: "", params: params, completionHandler: completionHandler)
    }
    
    func performRequest<responseModel: Decodable>(with action:RestAction, responseModelType: responseModel.Type, urlSuffix: String, params: Parameters? = nil, completionHandler: ConnectionCompletionHandler?)
    {
        var components: [(String, String)] = []
        
        let baseURLString = URLString(restAction: action) + urlSuffix

        if let params = params{
            for key in params.keys.sorted(by: <) {
                let value = params[key]!
                components += URLEncoding.default.queryComponents(fromKey: key, value: value)
            }
        }
        
        let componentsString = components.map { "\($0)=\($1)" }.joined(separator: "&")
        var urlParameters = ""
        if componentsString.count > 0{
            urlParameters = urlParameters + "?" + componentsString
        }

        let url = baseURLString + urlParameters
        
        let method = action.httpMethod
        
        var headers: HTTPHeaders? = nil
        let acceptableContentType = "application/json"
        
        if /*action.authenticationRequiredHeader || */isLoggedIn(){
            
            let baseUrl = URL(string: baseURLString)!

            var oAuthParameters = getOAuthParams()
            
            if let params = params{
                for key in params.keys.sorted(by: <) {
                    
                    oAuthParameters[key] = params[key]!
                }
            }
           
            let signature = signCredentials.signature(method: action.httpMethod == .post ? .POST : .GET, url: baseUrl, parameters: oAuthParameters)
            
            var authorizationString = "OAuth " + OAUTH_REALM
            authorizationString +=  OAUTH_CONSUMER_KEY + "=\"" + ((oAuthParameters[OAUTH_CONSUMER_KEY] as? String) ?? "") + "\", "
            authorizationString +=  OAUTH_NONCE + "=\"" + ((oAuthParameters[OAUTH_NONCE] as? String) ?? "") + "\", "
            authorizationString +=  OAUTH_SIGNATURE + "=\"" + signature + "\", "
            authorizationString +=  OAUTH_SIGNATURE_METHOD + "=\"" + OAUTH_SIGNATURE_METHOD_VALUE + "\", "
            authorizationString +=  OAUTH_TIMESTAMP + "=\"" + ((oAuthParameters[OAUTH_TIMESTAMP] as? String) ?? "") + "\", "
            authorizationString +=  OAUTH_ACCESS_TOKEN + "=\"" + signCredentials.oauthToken + "\", "
            authorizationString +=  OAUTH_VERSION + "=\"" + OAUTH_VERSION_VALUE + "\"";
            
            headers = [
                "Accept": "application/json",
            ]
            
            headers![AUTHORIZATION_HEADER] = authorizationString

            print(authorizationString)
        }
        else{
            headers = ["Accept": "application/json"]
        }
        
        
        let encoding : ParameterEncoding = URLEncoding.default //(method == .get || method == .delete) ? URLEncoding.default : JSONEncoding.default
        self.manager.request(url, method: method, parameters: nil, encoding: encoding, headers: headers)
            .validate(contentType: [acceptableContentType])
            .validate()
            .log(level: .all, options: [.onlyDebug, .jsonPrettyPrint, .includeSeparator], printer: NativePrinter())
            .responseJSON { response in
                
                guard response.error == nil else {
                    completionHandler?(.failure(response.error!))
                    return
                }
                
                if  let data = response.data,
                    let model = try? JSONDecoder().decode(responseModel.self, from: data) {
                    completionHandler?(.success(model))
                } else {
                    completionHandler?(.failure(NSError(  domain: "wolnelektury",
                                                          code: 1000,
                                                          userInfo: ["error":"wrong model"])))
                }
        }
    }
    
    func getOAuthParams() -> [String: Any]{
        let timestamp = String(Int64(Date().timeIntervalSince1970))
        let nonce = OAuthSwiftCredential.generateNonce()
        let oAuthParameters = signCredentials.authorizationParameters(nil, timestamp: timestamp, nonce: nonce)
        return oAuthParameters
    }
    
    func urlStringWithRequestTokenParameters(action: RestAction) -> String{
        var url = URLString(restAction: action)
        
        let urlUrl = URL(string: url)!
        
        if action == .accessToken && oAuthRequestToken != nil{
            signCredentials.oauthToken = oAuthRequestToken!.token
            signCredentials.oauthTokenSecret = oAuthRequestToken!.tokenSecret
        }
        else{
            signCredentials.oauthToken = ""
            signCredentials.oauthTokenSecret = ""
            oAuthRequestToken = nil
        }
        
        var oAuthParameters =  getOAuthParams()
        let signature = signCredentials.signature(method: .GET, url: urlUrl, parameters: oAuthParameters)
        
        url = url + "?" +
            OAUTH_CONSUMER_KEY.urlEncoded() + "=" + Config.CONSUMER_KEY.urlEncoded() + "&" +
            OAUTH_NONCE.urlEncoded() + "=" + (oAuthParameters[OAUTH_NONCE] as! String).urlEncoded() + "&" +
            OAUTH_SIGNATURE_METHOD.urlEncoded() + "=" + OAUTH_SIGNATURE_METHOD_VALUE.urlEncoded() + "&" +
            OAUTH_TIMESTAMP.urlEncoded() + "=" + (oAuthParameters[OAUTH_TIMESTAMP] as! String) + "&" +
            OAUTH_VERSION.urlEncoded() + "=" + OAUTH_VERSION_VALUE.urlEncoded() + "&" +
            OAUTH_SIGNATURE.urlEncoded() + "=" + signature.urlEncoded()
        
        if action == .accessToken && oAuthRequestToken != nil{
            url = url + "&" + OAUTH_ACCESS_TOKEN.urlEncoded() + "=" + oAuthRequestToken!.token.urlEncoded()
        }
        
        return url
    }
    
    func requestToken( completionHandler: ConnectionCompletionHandler?)
    {
        let action = RestAction.requestToken
        let method = action.httpMethod

//        let acceptableContentType = "text/html"
        let url = urlStringWithRequestTokenParameters(action: action)
        print(url)
        
        let parameters:Parameters = [:]
        let headers: HTTPHeaders? = nil
        
        let encoding : ParameterEncoding = (method == .get || method == .delete) ? URLEncoding.default : JSONEncoding.default
        self.manager.request(url, method: method, parameters: parameters, encoding: encoding, headers: headers)
//            .validate(contentType: [acceptableContentType])
            .validate()
            .log(level: .all, options: [.onlyDebug, .jsonPrettyPrint, .includeSeparator], printer: NativePrinter())
            .response { response in
                
                guard response.error == nil else {
                    completionHandler?(.failure(response.error!))
                    return
                }
                
                if let data = response.data, let string = String(data: data, encoding: .utf8){
                    print(string)
                    
                    var tokenModel: OAuthTokenModel?
                    let parameters = string.parametersFromQueryString
                    if let oauthToken = parameters["oauth_token"], let oauthTokenSecret = parameters["oauth_token_secret"]  {
                        tokenModel = OAuthTokenModel(token: oauthToken, tokenSecret: oauthTokenSecret)
                    }
                    
                    if let tokenModel = tokenModel, tokenModel.isValid(){
                        self.oAuthRequestToken = tokenModel
                        completionHandler?(.success(tokenModel))
                    }
                    else{
                        self.oAuthRequestToken = nil
                        completionHandler?(.failure(NSError(  domain: "wolnelektury",
                                                              code: 1000,
                                                              userInfo: ["error":"wrong model"])))
                    }
                }
        }
    }
    
    func requestAccessToken( completionHandler: ConnectionCompletionHandler?)
    {
        let action = RestAction.accessToken
        let method = action.httpMethod
        
//        let acceptableContentType = "text/html"
        let url = urlStringWithRequestTokenParameters(action: action)
        print(url)
        
        let parameters:Parameters = [:]
        let headers: HTTPHeaders? = nil
        
        let encoding : ParameterEncoding = (method == .get || method == .delete) ? URLEncoding.default : JSONEncoding.default
        self.manager.request(url, method: method, parameters: parameters, encoding: encoding, headers: headers)
//            .validate(contentType: [acceptableContentType])
            .validate()
            .log(level: .all, options: [.onlyDebug, .jsonPrettyPrint, .includeSeparator], printer: NativePrinter())
            .response { response in
                
                guard response.error == nil else {
                    completionHandler?(.failure(response.error!))
                    return
                }
                
                if let data = response.data, let string = String(data: data, encoding: .utf8){
                    print(string)
                    
                    let parameters = string.parametersFromQueryString

                    var tokenModel: OAuthTokenModel?
                    if let oauthToken = parameters["oauth_token"], let oauthTokenSecret = parameters["oauth_token_secret"]{
                        tokenModel = OAuthTokenModel(token: oauthToken, tokenSecret: oauthTokenSecret)
                    }
                    
                    if let tokenModel = tokenModel, tokenModel.isValid(){
                        
                        completionHandler?(.success(tokenModel))
                    }
                    else{
                        completionHandler?(.failure(NSError(  domain: "wolnelektury",
                                                              code: 1000,
                                                              userInfo: ["error":"wrong model"])))
                    }
                }
        }
    }
}
