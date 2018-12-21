//
//  Credentials.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 14/07/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit

class Credentials: NSObject, NSCoding{
    
    let credentialOAuthTokenSecretKey = "credentialOAuthTokenSecretKey"
    let credentialOAuthTokenKey = "credentialOAuthTokenKey"
    
    var oauthTokenModel: OAuthTokenModel?
    
    override init(){
        
    }

    required init?(coder decoder: NSCoder) {
        
        if let token = decoder.decodeObject(forKey: credentialOAuthTokenKey) as? String, let secret = decoder.decodeObject(forKey: credentialOAuthTokenSecretKey) as? String  {
            self.oauthTokenModel = OAuthTokenModel(token: token, tokenSecret: secret)
        }
    }
    
    func encode(with encoder: NSCoder) {
        
        if let tokenModel = oauthTokenModel{
            encoder.encode(tokenModel.token, forKey: credentialOAuthTokenKey)
            encoder.encode(tokenModel.tokenSecret, forKey: credentialOAuthTokenSecretKey)
        }
    }
    
    func isLoggedIn() -> Bool {
        guard let tokenModel = oauthTokenModel, tokenModel.isValid() else {
            return false
        }
        return true
    }
}
