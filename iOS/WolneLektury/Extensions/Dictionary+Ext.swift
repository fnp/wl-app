//
//  Dictionary+Ext.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 10/07/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import Foundation

extension Dictionary{
    var urlEncoded: String {
        var parts = [String]()
        
        for (key, value) in self {
            let keyString = "\(key)".urlEncoded()
            let valueString = "\(value)".urlEncoded()
            let query = "\(keyString)=\(valueString)"
            parts.append(query)
        }
        
        return parts.joined(separator: "&")
    }
}
