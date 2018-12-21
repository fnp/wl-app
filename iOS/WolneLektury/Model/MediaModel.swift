//
//  MediaModel.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 30/05/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit
import RealmSwift

class MediaModel: Object, Decodable, NSCopying {

    @objc dynamic var url: String = ""
    @objc dynamic var director: String = ""
    @objc dynamic var type: String = ""
    @objc dynamic var name: String = ""
    @objc dynamic var artist: String = ""
    
    convenience init(url: String, director: String, type: String, name: String, artist: String) {
        self.init()
        
        self.url = url
        self.director = director
        self.type = type
        self.name = name
        self.artist = artist
    }

    func copy(with zone: NSZone? = nil) -> Any {
        return MediaModel(url: url, director: director, type: type, name: name, artist: artist)
    }
    
    func titleForPlayer() -> String {
        return "\(artist), \(name)"
    }
}
