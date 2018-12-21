//
//  BookModel.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 30/05/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit

struct BookModel: Codable{

    var localId: Int?

    var kind: String!
    var author: String!
    var url: String!
    var title: String!
    var cover: String!
    var epoch: String!
    var href: String!
    var genre: String!
    var slug: String!
    var key: String!
    var simple_thumb: String!
    var fileName: String?
    var epub: String?
    var has_audio: Bool!
    var cover_color: String!
    var full_sort_key: String!
    
    var bgColor: UIColor {
        return UIColor(hex: cover_color)
    }

    private enum CodingKeys: String, CodingKey {
        case localId
        case kind
        case author
        case url
        case title
        case cover
        case epoch
        case href
        case genre
        case slug
        case key
        case simple_thumb
        case fileName
        case epub
        case has_audio
        case cover_color
        case full_sort_key
    }

    
    // returns image url
    func getCoverThumbUrl() -> URL?{
        return simple_thumb.getPhotoUrl()
    }
    
    func getAttributedAuthorAndTitle(titleFont: UIFont, descFont: UIFont) -> NSAttributedString{
        let titleAttributedText = NSMutableAttributedString(attributedString: NSAttributedString(string: author, font: titleFont))
        titleAttributedText.append(NSAttributedString(string:  "\n\n", font: UIFont.systemFont(ofSize: 2)))

        titleAttributedText.append(NSAttributedString(string:  title, font: descFont))
        return titleAttributedText
    }
}
