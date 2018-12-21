//
//  String+Ext.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 30/05/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import Foundation

import UIKit

extension String{
    
    var trimmed: String {
        return trimmingCharacters(in: CharacterSet.whitespacesAndNewlines)
    }
    
    var trimmedLength: Int{
        get {
            return trimmed.count
        }
    }
    
    var isBlank: Bool {
        get {
            return trimmedLength == 0
        }
    }
    
    //Validate Email
    var isEmail: Bool {
        do {
            let regex = try NSRegularExpression(pattern: "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", options: .caseInsensitive)
            let selfTrimmed = self.trimmed
            return regex.firstMatch(in: selfTrimmed, options: NSRegularExpression.MatchingOptions(rawValue: 0), range: NSMakeRange(0, selfTrimmed.characters.count)) != nil
        } catch {
            return false
        }
    }
    
    var isPostCode: Bool{
        
        let pinRegex =  "^\\d{2}-\\d{3}$" //"^[0-9]{6}$"
        let predicate = NSPredicate(format: "SELF MATCHES %@", pinRegex)
        
        let result =  predicate.evaluate(with:self)
        return result
    }
    
    var localized: String {
        return NSLocalizedString(self, comment: "")
    }
    
    func base64Encoded() -> String? {
        if let data = self.data(using: .utf8) {
            return data.base64EncodedString()
        }
        return nil
    }
    
    func base64Decoded() -> String? {
        if let data = Data(base64Encoded: self) {
            return String(data: data, encoding: .utf8)
        }
        return nil
    }
    
    var numericOnly: Bool{
        get{
            return CharacterSet.decimalDigits.isSuperset(of: CharacterSet(charactersIn: self))
        }
    }
    
    func height(withConstrainedWidth width: CGFloat, font: UIFont) -> CGFloat {
        let constraintRect = CGSize(width: width, height: .greatestFiniteMagnitude)
        let boundingBox = self.boundingRect(with: constraintRect, options: .usesLineFragmentOrigin, attributes: [NSAttributedStringKey.font: font], context: nil)
        
        return ceil(boundingBox.height)
    }
    
    func width(withConstrainedHeight height: CGFloat, font: UIFont) -> CGFloat {
        let constraintRect = CGSize(width: .greatestFiniteMagnitude, height: height)
        let boundingBox = self.boundingRect(with: constraintRect, options: .usesLineFragmentOrigin, attributes: [NSAttributedStringKey.font: font], context: nil)
        
        return ceil(boundingBox.width)
    }
    
    subscript(_ range: CountableRange<Int>) -> String {
        let idx1 = index(startIndex, offsetBy: range.lowerBound)
        let idx2 = index(startIndex, offsetBy: range.upperBound)
        return String(self[idx1..<idx2])
    }
    
    func getUrl() -> URL? {
        if let escapedUrl = self.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed), let url = URL(string: escapedUrl){
            return url
        }
        return nil
    }
    
    func utf8DecodedString()-> String {
        let data = self.data(using: .utf8)
        if let message = String(data: data!, encoding: .nonLossyASCII){
            return message
        }
        return ""
    }
    
    func utf8EncodedString()-> String {
        let messageData = self.data(using: .nonLossyASCII)
        let text = String(data: messageData!, encoding: .utf8)
        return text!
    }
    
    //
    //  from oauhSwift
    //
    
    func urlEncoded() -> String {
        let customAllowedSet = CharacterSet(charactersIn: "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-._~")
        return self.addingPercentEncoding(withAllowedCharacters: customAllowedSet)!
    }
    
    var parametersFromQueryString: [String: String] {
        return dictionaryBySplitting("&", keyValueSeparator: "=")
    }
    
    var urlQueryEncoded: String? {
        return self.addingPercentEncoding(withAllowedCharacters: CharacterSet.urlQueryAllowed)
    }
    
    /// Returns new url query string by appending query parameter encoding it first, if specified.
    func urlQueryByAppending(parameter name: String, value: String, encode: Bool = true, _ encodeError: ((String, String) -> Void)? = nil) -> String? {
        if value.isEmpty {
            return self
        } else if let value = encode ? value.urlQueryEncoded : value {
            return "\(self)\(self.isEmpty ? "" : "&")\(name)=\(value)"
        } else {
            encodeError?(name, value)
            return nil
        }
    }
    
    /// Returns new url string by appending query string at the end.
    func urlByAppending(query: String) -> String {
        return "\(self)\(self.contains("?") ? "&" : "?")\(query)"
    }
    
    fileprivate func dictionaryBySplitting(_ elementSeparator: String, keyValueSeparator: String) -> [String: String] {
        var string = self
        
        if hasPrefix(elementSeparator) {
            string = String(dropFirst(1))
        }
        
        var parameters = [String: String]()
        
        let scanner = Scanner(string: string)
        
        var key: NSString?
        var value: NSString?
        
        while !scanner.isAtEnd {
            key = nil
            scanner.scanUpTo(keyValueSeparator, into: &key)
            scanner.scanString(keyValueSeparator, into: nil)
            
            value = nil
            scanner.scanUpTo(elementSeparator, into: &value)
            scanner.scanString(elementSeparator, into: nil)
            
            if let key = key as String? {
                if let value = value as String? {
                    if key.contains(elementSeparator) {
                        var keys = key.components(separatedBy: elementSeparator)
                        if let key = keys.popLast() {
                            parameters.updateValue(value, forKey: String(key))
                        }
                        for flag in keys {
                            parameters.updateValue("", forKey: flag)
                        }
                    } else {
                        parameters.updateValue(value, forKey: key)
                    }
                } else {
                    parameters.updateValue("", forKey: key)
                }
            }
        }
        
        return parameters
    }

    func getPhotoUrl() -> URL?{
        
        if self.count > 0{
            var str = self
            
            if (!self.contains(Config.MEDIA_URL) && !self.contains(Config.MEDIA_URL_HTTPS)) {
                str = Config.MEDIA_URL + str
            }
            return str.getUrl()
        }
        return nil
    }
    
    func sizeOf(_ font: UIFont) -> CGSize {
        return self.size(withAttributes: [NSAttributedStringKey.font: font])
    }
}
