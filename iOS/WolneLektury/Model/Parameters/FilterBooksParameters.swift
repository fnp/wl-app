//
//  FilterBooksParameters.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 30/05/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit

class FilterBooksParameters{
    static let SEARCH_ITEMS_COUNT = 30
    
    static func ==(lhs: FilterBooksParameters, rhs: FilterBooksParameters) -> Bool {
        return lhs.search == rhs.search && lhs.epochs == rhs.epochs && lhs.genres == rhs.genres && lhs.kinds == rhs.kinds && lhs.after == rhs.after
    }

    var onlyLectures: Bool?
    var hasAudiobook: Bool?

    var search: String?
    var epochs: String?
    var genres: String?
    var kinds: String?
    var after: String?

    func parameters() -> [String: Any]{
        var dict: [String: Any] =  ["count": FilterBooksParameters.SEARCH_ITEMS_COUNT]
        
        if let v = search{
            dict["search"] = v
        }

        if let v = after{
            dict["after"] = v
        }
        if let v = epochs{
            dict["epochs"] = v
        }
        if let v = genres{
            dict["genres"] = v
        }
        if let v = kinds{
            dict["kinds"] = v
        }
        
        if let value = onlyLectures, value == true {
            dict["lektura"] = "true"
        }

        if let value = hasAudiobook, value == true {
            dict["audiobook"] = "true"
        }

        dict["new_api"] = "true"
        return dict
    }
}
