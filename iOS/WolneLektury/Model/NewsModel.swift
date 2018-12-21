//
//  NewsModel.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 15/09/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit

class NewsModel: Codable {
    var body: String!
    var lead: String!
    var title: String!
    var url: String!
    var image_url: String!
    var key: String!
    var time: String!
    var place: String!
    var image_thumb: String!
    var gallery_urls: [String]!
    var type: String!
    
    // returns image url
    func getCoverThumbUrl() -> URL?{
      
        return image_thumb.getPhotoUrl()
    }
}
