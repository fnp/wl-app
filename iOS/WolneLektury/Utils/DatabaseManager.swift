//
//  DatabaseManager.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 11/09/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit
import RealmSwift

class DatabaseManager: NSObject {
    static let shared = DatabaseManager()

    override init() {
        super.init()
        realmConfiguration()
    }
    
    var rlmApplication:RLMApplication?

    func realmConfiguration()
    {
        Realm.Configuration.defaultConfiguration = Realm.Configuration(
            schemaVersion: 1,
            migrationBlock: { migration, oldSchemaVersion in
                /*
                 if (oldSchemaVersion < 1) {
                 
                 } */
        })
        
        let realm = try! Realm()
        self.rlmApplication = realm.objects(RLMApplication.self).last
        
        print(realm.configuration.fileURL)
        
        if  self.rlmApplication  == nil
        {
            self.rlmApplication  = RLMApplication()
            try! realm.write {
                realm.add(self.rlmApplication!)
            }
        }
    }

    /// removing database
    func purgeData(){
        let realm = try! Realm()
        self.rlmApplication = nil
        try! realm.write {
            realm.deleteAll()
        }
        
        self.rlmApplication  = RLMApplication()
        try! realm.write {
            realm.add(self.rlmApplication!)
        }
    }

    func isUserPremium()-> Bool{
        if let premium = rlmApplication?.user?.premium, premium == true{
            return true
        }
        return false
    }

    func updateUser(usernameModel: UsernameModel?){
        self.rlmApplication?.updateUser(usernameModel: usernameModel)
    }

    func addBookToDownloaded(bookDetailsModel: BookDetailsModel) {
        rlmApplication?.addBookToDownloaded(bookDetailsModel: bookDetailsModel)
    }
    
    func removeAllBooksFromDownloaded() {
        guard let application = rlmApplication else { return }
        for book in application.downloadedBooks {
            DownloadManager.sharedInstance.deleteEbook(bookSlug: book.slug)
            DownloadManager.sharedInstance.deleteAudiobook(bookSlug: book.slug)
        }
        
        application.removeAllBooksFromDownloaded()
    }
    
    func removeBookFromDownloaded(bookSlug: String) -> Bool{
        guard let application = rlmApplication else {
            return false
        }

        DownloadManager.sharedInstance.deleteEbook(bookSlug: bookSlug)
        DownloadManager.sharedInstance.deleteAudiobook(bookSlug: bookSlug)
        return application.removeBookFromDownloaded(bookSlug: bookSlug)
    }
    
    func getBookFromDownloaded(bookSlug: String) -> BookDetailsModel? {
        return rlmApplication?.getBookFromDownloaded(bookSlug: bookSlug)
    }

    func updateCurrentChapters(bookDetailsModel: BookDetailsModel, currentChapter: Int?, totalChapters: Int?, currentAudioChapter: Int?, totalAudioChapters: Int?)
    {
        let realm = try! Realm()
        realm.beginWrite()
     
        if let currentChapter = currentChapter{
            bookDetailsModel.currentChapter = currentChapter
        }
        
        if let totalChapters = totalChapters{
            bookDetailsModel.totalChapters = totalChapters
        }

        if let currentAudioChapter = currentAudioChapter{
            bookDetailsModel.currentAudioChapter = currentAudioChapter
        }

        if let totalAudioChapters = totalAudioChapters{
            bookDetailsModel.totalAudioChapters = totalAudioChapters
        }

        try! realm.commitWrite()
    }
    
    func removeBookFromDownloadedIfNoFiles(bookSlug: String) {
        
        if FileManager.default.fileExists(atPath: FileType.ebook.destinationPathWithSlug(bookSlug: bookSlug)) {
            return
        }
        
        let fileNames = try! FileManager.default.contentsOfDirectory(atPath: FileType.audiobook.destinationPathWithSlug(bookSlug: bookSlug))

        if fileNames.count > 0 {
            return
        }
        
        let _ = rlmApplication?.removeBookFromDownloaded(bookSlug: bookSlug)
    }
    
    func refresh(){
        rlmApplication?.realm?.refresh()
    }
    
    func setUserPremium() {
        rlmApplication?.setUserPermium()
    }
}
