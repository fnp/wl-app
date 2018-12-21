//
//  RestAction.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 30/05/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit
import Alamofire

enum RestAction{

    //books
    case filterBooks
    case books
    case newest
    case recommended
    case audiobooks
    case setReadingState
    case getReadingState
    case getReadingBooks
    case getReadenBooks
    case setFavouriteState
    case getFavouriteState
    case preview
    case getFavourites

    case epochs
    case genres
    case kinds
    case blog

    //user
    case requestToken
    case accessToken
    case username

    
    
    var endpoint: String{
        switch self {
        //books
        case .filterBooks:
            return "filter-books/"
        case .books:
            return "books/"
        case .newest:
            return "newest/"
        case .recommended:
            return "recommended/"
        case .audiobooks:
            return "audiobooks/"
        case .setReadingState, .getReadingState:
            return "reading/"
        case .getReadingBooks:
            return "shelf/reading/"
        case .getReadenBooks:
            return "shelf/complete/"
        case .setFavouriteState, .getFavouriteState:
            return "like/"
        case .getFavourites:
            return "shelf/likes/"

        case .epochs:
            return "epochs"
        case .genres:
            return "genres"
        case .kinds:
            return "kinds"
        case .preview:
            return "preview"
        case .blog:
            return "blog"

        //user
        case .requestToken:
            return "oauth/request_token/"
        case .accessToken:
            return "oauth/access_token/"
        case .username:
            return "username/"
        }
    }
    
    var httpMethod: Alamofire.HTTPMethod{
        switch self {
        case .setReadingState, .setFavouriteState:
            return .post
        default:
            return .get
        }
    }
    
    var tokenRequestedHeader: Bool{
        return self == .requestToken || self == .accessToken
    }
}
