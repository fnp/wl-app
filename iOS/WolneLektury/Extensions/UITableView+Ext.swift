//
//  UITableView+Ext.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 30/05/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import Foundation
import UIKit

extension UITableView{
    
    func registerNib(name: String){
        registerNib(name: name, forCellReuseIdentifier: name)
    }
    
    func registerNib(name: String, forCellReuseIdentifier: String){
        self.register(UINib(nibName: name, bundle: nil), forCellReuseIdentifier: forCellReuseIdentifier)
    }
}
