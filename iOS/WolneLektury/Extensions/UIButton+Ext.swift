//
//  UIButton+Ext.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 30/05/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import Foundation

import UIKit

extension UIButton {
    
    var text: String{
        get{
            return title(for: .normal) ?? ""
        }
        set(newVal){
            setTitle(newVal, for: .normal)
        }
    }
}
