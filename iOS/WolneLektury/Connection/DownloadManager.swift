//
//  DownloadManager.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 22/06/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit
import MZDownloadManager

enum FileStatus{
    case notDownloaded
    case downloading
    case downloaded
}

enum FileType{
    case ebook
    case audiobook
    
    private var destinationPath: String{
        switch self {
        case .ebook:
            return Constants.ebookPath
        case .audiobook:
            return Constants.audiobookPath
        }
    }
    
    func destinationPathWithSlug(bookSlug: String) -> String {
        return destinationPath + "/" + bookSlug + "/"
    }
    
    func pathForFileName(filename: String, bookSlug: String) -> String{
        return self.destinationPathWithSlug(bookSlug: bookSlug) + filename
    }
}

protocol DownloadManagerDelegate: class{
    func downloadManagerDownloadProgressChanged(model: MZDownloadModel, allProgress: Float, bookSlug: String)
    func downloadManagerDownloadFinished(model: MZDownloadModel, bookSlug: String)
    func downloadManagerDownloadFailed(model: MZDownloadModel, bookSlug: String)
}

extension MZDownloadModel {
    func isAudiobook() -> Bool {
        return self.destinationPath.starts(with: Constants.audiobookPath)
    }
    func isEbook() -> Bool {
        return self.destinationPath.starts(with: Constants.ebookPath)
    }

}

class DownloadingAudiobook{
    var allUrlsCount: Float = 0
    var waitingToDownloadUrls: [String]
    var downloadedUrls: [String]
    var bookSlug: String
    init(bookDetailsModel: BookDetailsModel) {
        
        waitingToDownloadUrls = [String]()
        downloadedUrls = [String]()
        
        let audiobookUrls = bookDetailsModel.getAudiobookFilesUrls()
        bookSlug = bookDetailsModel.slug
        allUrlsCount = Float(audiobookUrls.count)
        
        for url in audiobookUrls{
            if NSObject.audiobookExists(audioBookUrlString: url, bookSlug: bookSlug){
                downloadedUrls.append(url)
            }
            else{
                waitingToDownloadUrls.append(url)
            }
        }
    }
    
    func getProgress() -> Float{
        let allCount = waitingToDownloadUrls.count + downloadedUrls.count
        if allCount > 0{
            if downloadedUrls.count > 0{
                return Float(downloadedUrls.count) / Float(allCount)
            }
        }
        return 0.0
    }
}

class DownloadManager: NSObject, MZDownloadManagerDelegate{
    
    weak var delegate: DownloadManagerDelegate?
    var downloadingAudiobooks = [DownloadingAudiobook]()
    
    //Shared instance of DownloadManager
    static let sharedInstance : DownloadManager = {
        return DownloadManager()
    }()
    
    
    lazy var downloadManager: MZDownloadManager = {
        [unowned self] in
        let sessionIdentifer: String = "com.moiseum.WolneLektury.BackgroundSession"
        
        let appDelegate = UIApplication.shared.delegate as! AppDelegate
        var completion = appDelegate.backgroundSessionCompletionHandler
        
        let downloadmanager = MZDownloadManager(session: sessionIdentifer, delegate: self, completion: completion)
        return downloadmanager
        }()
    
    func checkEbookStatus(bookSlug: String) -> FileStatus{
        if getEbookProgress(bookSlug: bookSlug) != nil{
            return .downloading
        }
        
        if ebookExists(bookSlug: bookSlug){
            return .downloaded
        }
        
        return .notDownloaded
    }
    
    func getDownloadingAudiobook(bookSlug: String) -> DownloadingAudiobook? {
        for downloadingAudiobook in downloadingAudiobooks {
            if downloadingAudiobook.bookSlug == bookSlug {
                return downloadingAudiobook
            }
        }
        return nil
    }
    
    func clearDownloadingAudiobookFromQueue(bookSlug: String){
        
        var i = 0
        var found = false
        for downloadingAudiobook in downloadingAudiobooks {
            if downloadingAudiobook.bookSlug == bookSlug {
                found = true
                break
            }
            
            i += 1
        }
        if found {
            downloadingAudiobooks.remove(at: i)
        }
        
        if let index = downloadManager.downloadingArray.index(where: {$0.destinationPath == FileType.audiobook.destinationPathWithSlug(bookSlug: bookSlug)}) {
            downloadManager.cancelTaskAtIndex(index)
        }
    }
    
    func checkAudiobookStatus(bookDetailsModel: BookDetailsModel) -> FileStatus{
        
        guard bookDetailsModel.slug.count > 0 else { return .notDownloaded}
        
        if getDownloadingAudiobook(bookSlug: bookDetailsModel.slug) != nil {
            return .downloading
        }
        
        if bookDetailsModel.checkIfAllAudiobookFilesAreDownloaded() {
            return .downloaded
        }
        
        return .notDownloaded
    }

    func deleteEbook(bookSlug: String){
        
        let fileType = FileType.ebook
        
        if let index = downloadManager.downloadingArray.index(where: {$0.fileName == bookSlug && $0.destinationPath == fileType.destinationPathWithSlug(bookSlug: bookSlug)}) {
            downloadManager.cancelTaskAtIndex(index)
        }
        else {
            let path = fileType.destinationPathWithSlug(bookSlug: bookSlug)// pathForFileName(filename: bookSlug,bookSlug: bookSlug)
            if FileManager.default.fileExists(atPath: path) {
                try! FileManager.default.removeItem(atPath: path)
            }
        }
        UserDefaults.standard.removeObject(forKey: bookSlug)
    }
    
    func deleteAudiobook(bookSlug: String){
        
        let fileType = FileType.audiobook
        
        try? FileManager.default.removeItem(atPath: fileType.destinationPathWithSlug(bookSlug: bookSlug))
    }
    
    func getEbookProgress(bookSlug: String) -> Float? {
        if let model = downloadManager.downloadingArray.first(where: {$0.fileName == bookSlug && $0.destinationPath == FileType.ebook.destinationPathWithSlug(bookSlug: bookSlug)}){
            return model.progress
        }
        return nil
    }
    
    func getAudiobookProgress(bookDetailsModel: BookDetailsModel) -> Float? {
        
        guard let downloadingAudiobook = getDownloadingAudiobook(bookSlug: bookDetailsModel.slug) else {return nil}
        
        return downloadingAudiobook.getProgress()
    }

    func downloadEbook(bookDetailsModel: BookDetailsModel) {
        
        guard bookDetailsModel.epub.count > 0, bookDetailsModel.slug.count > 0 else {
            return
        }
        
        downloadManager.addDownloadTask(bookDetailsModel.slug, fileURL: bookDetailsModel.epub, destinationPath: FileType.ebook.destinationPathWithSlug(bookSlug: bookDetailsModel.slug))
    }
    
    func downloadAudiobooks(bookDetailsModel: BookDetailsModel) {
        
        let bookSlug = bookDetailsModel.slug
        guard bookDetailsModel.getAudiobookFilesUrls().count > 0, bookSlug.count > 0 else {
            return
        }
        
        if let downloadingAudiobook = getDownloadingAudiobook(bookSlug:bookSlug){
            
            if let firstUrl = downloadingAudiobook.waitingToDownloadUrls.first{
                addDownloadAudiobookTask(bookSlug: bookSlug, fileUrl: firstUrl)
                return
            }
            else{
                clearDownloadingAudiobookFromQueue(bookSlug: bookSlug)
            }
        }
        
        let downloadingAudiobook = DownloadingAudiobook(bookDetailsModel: bookDetailsModel)
        if let firstUrl = downloadingAudiobook.waitingToDownloadUrls.first {
            downloadingAudiobooks.append(downloadingAudiobook)
            addDownloadAudiobookTask(bookSlug: bookSlug, fileUrl: firstUrl)
        }
    }
    
    func addDownloadAudiobookTask(bookSlug: String, fileUrl: String){
        downloadManager.addDownloadTask((fileUrl as NSString).lastPathComponent, fileURL: fileUrl, destinationPath: FileType.audiobook.destinationPathWithSlug(bookSlug: bookSlug))
    }

    func notifyDelegateThatProgressChanged(downloadModel: MZDownloadModel){
        guard let delegate = delegate else { return }
        
        if downloadModel.isAudiobook(){
            for downloadAudiobook in downloadingAudiobooks {
                if downloadAudiobook.waitingToDownloadUrls.index(of: downloadModel.fileURL) != nil {
                    delegate.downloadManagerDownloadProgressChanged(model: downloadModel, allProgress: downloadAudiobook.getProgress() + downloadModel.progress/downloadAudiobook.allUrlsCount, bookSlug: downloadAudiobook.bookSlug)
                    return
                }
            }
        }
        delegate.downloadManagerDownloadProgressChanged(model: downloadModel, allProgress: downloadModel.progress, bookSlug: downloadModel.fileName)
    }
    
    func downloadRequestStarted(_ downloadModel: MZDownloadModel, index: Int) {
        
        notifyDelegateThatProgressChanged(downloadModel: downloadModel)
    }
    
    func downloadRequestDidPopulatedInterruptedTasks(_ downloadModels: [MZDownloadModel]) {
    }
    
    func downloadRequestDidUpdateProgress(_ downloadModel: MZDownloadModel, index: Int) {
        notifyDelegateThatProgressChanged(downloadModel: downloadModel)
    }
    
    func downloadRequestDidPaused(_ downloadModel: MZDownloadModel, index: Int) {
    }
    
    func downloadRequestDidResumed(_ downloadModel: MZDownloadModel, index: Int) {
    }
    
    func downloadRequestCanceled(_ downloadModel: MZDownloadModel, index: Int) {
        if downloadModel.isAudiobook(){
            for downloadAudiobook in downloadingAudiobooks {
                if downloadAudiobook.waitingToDownloadUrls.index(of: downloadModel.fileURL) != nil {
                    clearDownloadingAudiobookFromQueue(bookSlug: downloadAudiobook.bookSlug)

                    delegate?.downloadManagerDownloadFailed(model: downloadModel, bookSlug:downloadAudiobook.bookSlug )
                    return
                }
            }
        }

        delegate?.downloadManagerDownloadFailed(model: downloadModel, bookSlug: downloadModel.fileName)
    }
    
    func downloadRequestFinished(_ downloadModel: MZDownloadModel, index: Int) {
        
        // audiobook
        if downloadModel.isAudiobook() {
            for downloadAudiobook in downloadingAudiobooks {
                if let index = downloadAudiobook.waitingToDownloadUrls.index(of: downloadModel.fileURL) {
                    downloadAudiobook.waitingToDownloadUrls.remove(at: index)
                    downloadAudiobook.downloadedUrls.append(downloadModel.fileURL)
                    let slug = downloadAudiobook.bookSlug
                    // check if there is any waiting to download url, and start downloading
                    if let firstUrl = downloadAudiobook.waitingToDownloadUrls.first(where: {$0.count > 0}) {
                        addDownloadAudiobookTask(bookSlug: downloadAudiobook.bookSlug, fileUrl: firstUrl)
                    }
                    else { // otherwise, downloading is finished, notify delegates
                        clearDownloadingAudiobookFromQueue(bookSlug: slug)
                        delegate?.downloadManagerDownloadFinished(model: downloadModel, bookSlug: slug)
                    }
                }
            }
        }
        else {//ebook
            delegate?.downloadManagerDownloadFinished(model: downloadModel, bookSlug: downloadModel.fileName)
        }
    }
    
    func downloadRequestDidFailedWithError(_ error: NSError, downloadModel: MZDownloadModel, index: Int) {
        var bookSlug = downloadModel.fileName
        if downloadModel.isAudiobook() {
            for downloadAudiobook in downloadingAudiobooks {
                if downloadAudiobook.waitingToDownloadUrls.index(of: downloadModel.fileURL) != nil {
                    bookSlug = downloadAudiobook.bookSlug
                    clearDownloadingAudiobookFromQueue(bookSlug: bookSlug ?? "")
                    continue
                }
            }
        }
        delegate?.downloadManagerDownloadFailed(model: downloadModel, bookSlug: downloadModel.fileName)
    }
    
    //Oppotunity to handle destination does not exists error
    //This delegate will be called on the session queue so handle it appropriately
    func downloadRequestDestinationDoestNotExists(_ downloadModel: MZDownloadModel, index: Int, location: URL) {
        let myDownloadPath = downloadModel.destinationPath
        if !FileManager.default.fileExists(atPath: myDownloadPath) {
            try! FileManager.default.createDirectory(atPath: myDownloadPath, withIntermediateDirectories: true, attributes: nil)
        }        
        let filePath = myDownloadPath + "/" + downloadModel.fileName
        if FileManager.default.fileExists(atPath: filePath){
           try! FileManager.default.removeItem(atPath: filePath)
        }
        try! FileManager.default.moveItem(at: location, to: URL(fileURLWithPath: filePath))
    }
}
