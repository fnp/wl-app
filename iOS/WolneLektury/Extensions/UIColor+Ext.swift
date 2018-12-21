//
//  UIColor+Ext.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 30/08/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import Foundation
import UIKit

extension UIColor{
    
    convenience init(hex:String) {
        
        var cString:String = hex.trimmingCharacters(in: .whitespacesAndNewlines).uppercased()
        var rgbValue:UInt32 = 0
        
        if cString.hasPrefix("#") {
            cString.remove(at: cString.startIndex)
        }
        
        if cString.count == 6 {
            Scanner(string: cString).scanHexInt32(&rgbValue)
        }
        
        self.init(red: CGFloat((rgbValue & 0xFF0000) >> 16) / 255.0, green: CGFloat((rgbValue & 0x00FF00) >> 8) / 255.0, blue: CGFloat(rgbValue & 0x0000FF) / 255.0, alpha: 1.0 )
    }
}
