//
//  SyncManager.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 30/05/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit


class SyncManager: NSObject {
    private var networkService = NetworkService()

    override init() {
        
    }
    
    func isLoggedIn() -> Bool {
        return networkService.isLoggedIn()
    }
    
    func logout() {
        networkService.logout()
        DatabaseManager.shared.updateUser(usernameModel: nil)
    }
    
    func updateUserCredentials(oAuthTokenModel: OAuthTokenModel?){
        networkService.updateUserCredentials(oAuthTokenModel: oAuthTokenModel)
    }
    
    func requestToken(completionHandler: ConnectionCompletionHandler?){
        networkService.requestToken(completionHandler: completionHandler)
    }
    
    func accessToken(completionHandler: ConnectionCompletionHandler?){
        networkService.requestAccessToken(completionHandler: completionHandler)
    }

    func getUsername(completionHandler: ConnectionCompletionHandler?){
        networkService.performRequest(with: .username, responseModelType: UsernameModel.self, params: nil, completionHandler: completionHandler)
    }
    
    func filterBooks(params: FilterBooksParameters, completionHandler: ConnectionCompletionHandler?){
        networkService.performRequest(with: .filterBooks, responseModelType: [BookModel].self, params: params.parameters(), completionHandler: completionHandler)
    }

    func getNews(after: String?, completionHandler: ConnectionCompletionHandler?){
        
        var params = [String: Any]()
        if let after = after {
            params["after"] = after
        }
        params["count"] = 20
        
        networkService.performRequest(with: .blog, responseModelType: [NewsModel].self, params: params, completionHandler: completionHandler)
    }

    func getCategories(filterSection: FilterSection, bookOnly: Bool, completionHandler: ConnectionCompletionHandler?){
        
        var method = RestAction.epochs
        if filterSection == .genres{
            method = .genres
        }
        else if filterSection == .kinds{
            method = .kinds
        }
        networkService.performRequest(with: method, responseModelType: [CategoryModel].self, params: ["book_only": bookOnly ? "true" : "false"], completionHandler: completionHandler)
    }
    
    func getDataForLibrary(libraryCollectionType: LibraryCollectionType, completionHandler: ConnectionCompletionHandler?){
        var method: RestAction!
        
        switch libraryCollectionType {
        case .newest:
            method = .newest
        case .recommended:
            method = .recommended
        case .reading_now:
            method = .getReadingBooks
        }
        
        networkService.performRequest(with: method, responseModelType: [BookModel].self, params: nil, completionHandler: completionHandler)
    }
    
    func getPreview(completionHandler: ConnectionCompletionHandler?){
        
        networkService.performRequest(with: .preview, responseModelType: [BookModel].self, params: nil, completionHandler: completionHandler)
    }


    func getDataForListType(listViewControllerType: ListViewControllerType, params: FilterBooksParameters? = nil, completionHandler: ConnectionCompletionHandler?){
        var method: RestAction!
        
        switch listViewControllerType {
        case .newest:
            method = .newest
        case .recommended:
            method = .recommended
        case .reading_now:
            method = .getReadingBooks
        case .audiobooks:
            method = .audiobooks
        case .news:
            method = .blog
        case .favourites:
            method = .getFavourites
        case .completed:
            method = .getReadenBooks
        }
        
        if method == .blog {
            networkService.performRequest(with: method, responseModelType: [NewsModel].self, params: params?.parameters(), completionHandler: completionHandler)
        }
        else {
            networkService.performRequest(with: method, responseModelType: [BookModel].self, params: params?.parameters(), completionHandler: completionHandler)
        }
    }

    func getBookDetails(bookSlug: String, completionHandler: ConnectionCompletionHandler?){
        
        networkService.performRequest(with: .books, responseModelType: BookDetailsModel.self, urlSuffix: bookSlug + "/", completionHandler: completionHandler)
    }

    func getFavouriteState(slug: String, completionHandler: ConnectionCompletionHandler?){
        
        networkService.performRequest(with: .getFavouriteState, responseModelType: LikeModel.self, urlSuffix: slug + "/", params: nil, completionHandler: completionHandler)
    }
    
    func setFavouriteState(slug: String, favourite: Bool, completionHandler: ConnectionCompletionHandler?){
        
        networkService.performRequest(with: .setFavouriteState, responseModelType: LikeModel.self, urlSuffix: slug + "/", params: ["action" : favourite ? "like" : "unlike"], completionHandler: completionHandler)
    }
    
    func setReadingState(slug: String, readingState: ReadingStateModel.ReadingState, completionHandler: ConnectionCompletionHandler?){
        
        networkService.performRequest(with: .setReadingState, responseModelType: ReadingStateModel.self, urlSuffix: slug + "/" + readingState.rawValue + "/", params: nil, completionHandler: completionHandler)
    }
    
    func getReadingState(slug: String, completionHandler: ConnectionCompletionHandler?){
        
        networkService.performRequest(with: .getReadingState, responseModelType: ReadingStateModel.self, urlSuffix: slug + "/", params: nil, completionHandler: completionHandler)
    }
}
