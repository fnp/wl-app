//
//  Routes.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 30/05/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit

struct Route<Model> {
    let endpoint: String
    var id: Int?
    var params: [String: AnyObject]?
    
    init (endpoint: String, id: Int? = nil, parameters: [String: AnyObject]? = nil) {
        self.endpoint = endpoint
        self.id = id
        self.params = parameters
    }
}

struct Routes {
    static let filterBooks = Route<[BookModel]>(endpoint: "filter-books")
    
}
