//
//  MenuItem.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 30/05/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit

enum MenuItem {
    
    case wolne_lektury
    case premium
    case audiobooks
    case downloaded
    case catalog
    case news
    case settings
    case about

    case reading
    case favourites
    case completed

    var title: String{
        return "nav_\(self)".localized
    }
    
    var image: UIImage?{
        return UIImage(named: "menu_\(self)")
    }
    
    var tintColor: UIColor{
        switch self {
        case .premium:
            return Constants.Colors.orangeColor()
        default:
            return .white
        }
    }
}
