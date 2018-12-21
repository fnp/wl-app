//
//  CategoryModel.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 30/05/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit
import RealmSwift

class CategoryModel: Object, Decodable, NSCopying {

    @objc dynamic var url: String = ""
    @objc dynamic var href: String = ""
    @objc dynamic var name: String = ""
    @objc dynamic var slug: String = ""
    
    var checked: Bool = false
    
    private enum CodingKeys: String, CodingKey {
        case url
        case href
        case name
        case slug
    }
    
    convenience init(url: String, href: String, name: String, slug: String) {
        self.init()
        
        self.url = url
        self.href = href
        self.name = name
        self.slug = slug
    }
    
    func copy(with zone: NSZone? = nil) -> Any {
        return CategoryModel(url: url, href: href, name: name, slug: slug)
    }
}
