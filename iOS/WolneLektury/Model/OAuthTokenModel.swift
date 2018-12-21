//
//  OAuthTokenModel.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 09/07/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit

class OAuthTokenModel {

    init(token: String, tokenSecret: String) {
        self.token = token
        self.tokenSecret = tokenSecret
    }
    
    var tokenSecret = ""
    var token = ""
    
    func isValid() -> Bool{
        return tokenSecret.count > 0 && token.count > 0
    }
}
