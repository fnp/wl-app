//
//  RLMApplication.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 24/08/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit
import RealmSwift

class RLMApplication: Object {
    
    @objc dynamic var user: RLMUser?
    var downloadedBooks = List<BookDetailsModel>()
    @objc dynamic var premium: Bool = false

    
    func updateUser(usernameModel: UsernameModel?){
        let realm = try! Realm()
        
        if let usernameModel = usernameModel{
            try! realm.write {
                let usr = RLMUser()
                usr.username = usernameModel.username
                usr.premium = usernameModel.premium
                self.user = usr
            }
        }
        else {
            if user != nil {
                try! realm.write {
                    realm.delete( user!)
                    self.user = nil
                }
            }
        }
    }
    
    func addBookToDownloaded(bookDetailsModel: BookDetailsModel){
        let realm = try! Realm()
        realm.beginWrite()

        var bookExists = false
        if let b = getBookFromDownloaded(bookSlug: bookDetailsModel.slug) {
            bookDetailsModel.currentChapter = b.currentChapter
            bookDetailsModel.totalChapters = b.totalChapters
            bookDetailsModel.currentAudioChapter = b.currentAudioChapter
            bookDetailsModel.totalAudioChapters = b.totalAudioChapters
            
            bookDetailsModel.authors = b.authors
            bookDetailsModel.epochs = b.epochs
            bookDetailsModel.genres = b.genres
            bookDetailsModel.kinds = b.kinds
            bookDetailsModel.media = b.media

            bookExists = true
        }
        
        realm.add(bookDetailsModel, update: true)
        if !bookExists{
            self.downloadedBooks.append(bookDetailsModel)
        }
        try! realm.commitWrite()
    }
    
    func removeAllBooksFromDownloaded() {
        
        guard downloadedBooks.count > 0 else { return }
        
        let realm = try! Realm()
        realm.beginWrite()
        realm.delete(downloadedBooks)
        try! realm.commitWrite()
    }
    
    func removeBookFromDownloaded(bookSlug: String) -> Bool {
        
        let books = downloadedBooks.filter({$0.slug == bookSlug})
        
        if books.count > 0 {
            let realm = try! Realm()
            realm.beginWrite()
            realm.delete(books)
            try! realm.commitWrite()
            return true
        }
        return false
    }
    
    func getBookFromDownloaded(bookSlug: String) -> BookDetailsModel? {
        return downloadedBooks.first(where: {$0.slug == bookSlug})
    }
    
    func setUserPermium() {
        guard let user = user else { return }
        
        let realm = try! Realm()
        realm.beginWrite()
        user.premium = true
        try! realm.commitWrite()
    }
}
