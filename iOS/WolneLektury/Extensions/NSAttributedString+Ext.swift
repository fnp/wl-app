//
//  NSAttributedString+Ext.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 30/05/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import Foundation

import UIKit

extension NSAttributedString{
    
    
    static func AttributedString(font: UIFont, text: String, lineSpacing: CGFloat, textAlignment: NSTextAlignment) -> NSAttributedString{
        
        let paragraphStyle = NSMutableParagraphStyle()
        paragraphStyle.lineSpacing = 1.0
        paragraphStyle.lineHeightMultiple = lineSpacing
        paragraphStyle.alignment = textAlignment
        
        let attrString = NSMutableAttributedString(string: text)
        attrString.addAttribute(NSAttributedStringKey.font, value: font, range: NSMakeRange(0, attrString.length))
        attrString.addAttribute(NSAttributedStringKey.paragraphStyle, value:paragraphStyle, range:NSMakeRange(0, attrString.length))
        return attrString;
    }
    
    static func AttributedString(font: UIFont, text: String, color: UIColor) -> NSAttributedString{
        
        let attrString = NSMutableAttributedString(string: text)
        attrString.addAttribute(NSAttributedStringKey.font, value: font, range: NSMakeRange(0, attrString.length))
        attrString.addAttribute(NSAttributedStringKey.foregroundColor, value: color, range: NSMakeRange(0, attrString.length))
        return attrString;
    }
    
    
    public convenience init(html:String) throws {
        
        let htmString = "<html><head><style> " +
            "\nbody \n{\n" +
            //"text-align: justify;\n" +
                        "font-size: 11.00pt;\n" +
                        "font-family: '-apple-system';\n" +
                        "line-height: 6px;\n" +
            "} \n" +
            "</style></head><body>\(html)</body>" +
        "</html>"
        
        
        guard let data =  htmString.data(using: .unicode, allowLossyConversion: true) else {
            throw NSError(domain: "Invalid HTML", code: -500, userInfo: nil)
        }
        
        try self.init(data: data, options: [.documentType: NSAttributedString.DocumentType.html], documentAttributes: nil)
    }
    
    
    public convenience init(string: String?, font: UIFont?) {
        self.init(string: string, font: font, color: nil, textAlignment: nil, lineSpacing: nil)
    }
    
    public convenience init(string: String?, font: UIFont?, color: UIColor?) {
        self.init(string: string, font: font, color: color, textAlignment: nil, lineSpacing: nil)
    }
    
    public convenience init(string: String?, font: UIFont?, color: UIColor?, textAlignment: NSTextAlignment?) {
        self.init(string: string, font: font, color: color, textAlignment: textAlignment, lineSpacing: nil)
    }
    
    public convenience init(string: String?, font: UIFont?, textAlignment: NSTextAlignment?) {
        self.init(string: string, font: font, color: nil, textAlignment: textAlignment, lineSpacing: nil)
    }
    
    public convenience init(string: String?, font: UIFont?, color: UIColor?, textAlignment: NSTextAlignment?, lineSpacing: CGFloat?) {
        
        let style = NSMutableParagraphStyle()
        var addStyle = false
        
        
        style.lineBreakMode = .byWordWrapping;
        
        var str = ""
        if let value = string{
            str = value
        }
        
        var dict = [NSAttributedStringKey : AnyObject]()
        if let fnt = font{
            dict[NSAttributedStringKey.font] = fnt
        }
        
        if let clr = color{
            dict[NSAttributedStringKey.foregroundColor] = clr
        }
        
        if let tal = textAlignment{
            style.alignment = tal
            if(tal == .justified){
                style.firstLineHeadIndent = 0.05;
            }
            addStyle = true
        }
        
        if let ls = lineSpacing{
            style.lineSpacing = ls
            addStyle = true
        }
        
        
        if(addStyle){
            dict[NSAttributedStringKey.paragraphStyle] = style
        }
        
        self.init(string: str, attributes: dict)
        return
    }
}
