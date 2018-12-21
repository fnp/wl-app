//
//  BookDetailsModel.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 30/05/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit
import RealmSwift

protocol BookBase {
    var simple_thumb: String { get set }
    var title: String { get set }
    
    func getAuthor() -> String
}

extension BookBase{
    
    // returns image url
    func getCoverThumbUrl() -> URL?{
        
        if simple_thumb.count > 0{
            var str = simple_thumb
            
            if (!str.contains(Config.MEDIA_URL) && !str.contains(Config.MEDIA_URL_HTTPS)) {
                str = Config.MEDIA_URL + str
            }
            
            return str.getUrl()
        }
        return nil
    }
    
    func getAttributedAuthorAndTitle(titleFont: UIFont, descFont: UIFont) -> NSAttributedString{
        let titleAttributedText = NSMutableAttributedString(attributedString: NSAttributedString(string: getAuthor() + "\n", font: titleFont))
        titleAttributedText.append(NSAttributedString(string: title, font: descFont))
        return titleAttributedText
    }
}

class BookDetailsModel: Object, BookBase, Decodable, NSCopying {
    @objc dynamic var title: String = ""
    @objc dynamic var simple_thumb: String = ""
    
    var genres = List<CategoryModel>()
    var kinds = List<CategoryModel>()
    @objc dynamic var url: String = ""
    var media = List<MediaModel>()
    @objc dynamic var simple_cover: String = ""
    var epochs = List<CategoryModel>()
    var authors = List<CategoryModel>()
    @objc dynamic var pdf: String = ""
    @objc dynamic var epub: String = ""
    @objc dynamic var fragmentTitle = ""
    @objc dynamic var fragmentHtml = ""
    
    @objc dynamic var audio_length: String = ""
    @objc dynamic var cover_color: String = ""

    //additional
    @objc dynamic var currentChapter = 0
    @objc dynamic var totalChapters = 0
    @objc dynamic var currentAudioChapter = 0
    @objc dynamic var totalAudioChapters = 0
    @objc dynamic var slug = ""
    
    private var privateBgColor: UIColor?
    var bgColor: UIColor{
        get {
            if let privateBgColor = privateBgColor {
                return privateBgColor
            }
            privateBgColor = UIColor(hex: cover_color)
            return privateBgColor!
        }
        set {
            privateBgColor = newValue
        }
    }
    
    override class func primaryKey() -> String? {
        return "slug"
    }

    private enum BookDetailsModelCodingKeys: String, CodingKey {
        case title
        case simple_thumb
        case genres
        case kinds
        case url
        case media
        case simple_cover
        case epochs
        case authors
        case pdf
        case epub
        case fragment_data
        case audio_length
        case cover_color
    }

    convenience init(title: String, simple_thumb: String, url: String, simple_cover: String, pdf: String, epub: String, audio_length: String, cover_color: String, genres: [CategoryModel], kinds: [CategoryModel], media: [MediaModel], epochs: [CategoryModel], authors: [CategoryModel], fragmentTitle: String, fragmentHtml: String, slug: String) {
        
        self.init()
        self.title = title
        self.simple_thumb = simple_thumb
        self.url = url
        self.simple_cover = simple_cover
        self.pdf = pdf
        self.epub = epub
        self.audio_length = audio_length
        self.cover_color = cover_color
        self.fragmentHtml = fragmentHtml
        self.fragmentTitle = fragmentTitle
        self.slug = slug
        
        self.genres = List<CategoryModel>()
        self.kinds = List<CategoryModel>()
        self.epochs = List<CategoryModel>()
        self.authors = List<CategoryModel>()
        self.media = List<MediaModel>()
        
        self.genres.append(objectsIn: genres)
        self.kinds.append(objectsIn: kinds)
        self.epochs.append(objectsIn: epochs)
        self.authors.append(objectsIn: authors)
        self.media.append(objectsIn: media)
    }

    convenience required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: BookDetailsModelCodingKeys.self)
        
        let title = try container.decode(String.self, forKey: .title)
        let simple_thumb = try container.decode(String.self, forKey: .simple_thumb)
        let url = try container.decode(String.self, forKey: .url)
        let simple_cover = try container.decode(String.self, forKey: .simple_cover)
        let pdf = try container.decode(String.self, forKey: .pdf)
        let epub = try container.decode(String.self, forKey: .epub)
        let audio_length = try container.decode(String.self, forKey: .audio_length)
        let cover_color = try container.decode(String.self, forKey: .cover_color)

        let genres = try container.decodeIfPresent([CategoryModel].self, forKey: .genres)
        let kinds = try container.decodeIfPresent([CategoryModel].self, forKey: .kinds)
        let epochs = try container.decodeIfPresent([CategoryModel].self, forKey: .epochs)
        let authors = try container.decodeIfPresent([CategoryModel].self, forKey: .authors)
        let media = try container.decodeIfPresent([MediaModel].self, forKey: .media)
        
        let fragment_data = try container.decodeIfPresent(FragmentModel.self, forKey: .fragment_data)
        
        let genresArray = genres ?? []
        let kindsArray = kinds ?? []
        let mediaArray = media ?? []
        let epochsArray = epochs ?? []
        let authorsArray = authors ?? []
        
        self.init(title: title, simple_thumb: simple_thumb, url: url, simple_cover: simple_cover, pdf: pdf, epub: epub, audio_length: audio_length, cover_color: cover_color, genres: genresArray, kinds: kindsArray, media: mediaArray, epochs: epochsArray, authors: authorsArray, fragmentTitle: fragment_data?.title ?? "", fragmentHtml: fragment_data?.html ?? "", slug: "")
    }
    
    
    func getAuthor() -> String {
        return authors.map({$0.name}).joined(separator: ", ")
    }
    
    func getGenres() -> String {
        return genres.map({$0.name}).joined(separator: ", ")
    }
    
    func getKinds() -> String {
        return kinds.map({$0.name}).joined(separator: ", ")
    }
    
    func getEpochs() -> String {
        return epochs.map({$0.name}).joined(separator: ", ")
    }
    
    func getAudiobookMediaModels() -> [MediaModel] {
        var mediaModels = [MediaModel]()
        
        for mediaFile in media {
            if mediaFile.type == "mp3" {
                mediaModels.append(mediaFile)
            }
        }
        return mediaModels;
    }
    
    func getAudiobookFilesUrls() -> [String] {
        
        return getAudiobookMediaModels().map({$0.url})
    }
    
    func checkIfAllAudiobookFilesAreDownloaded() -> Bool{
        
        guard slug.count > 0 else {return false}
        let audiobookUrls = getAudiobookFilesUrls()
        
        for url in audiobookUrls{
            if !NSObject.audiobookExists(audioBookUrlString: url, bookSlug: slug) {
                return false
            }
        }
        return true
    }
    
    func copy(with zone: NSZone? = nil) -> Any {
        
        var genresArray = [CategoryModel]()
        var kindsArray = [CategoryModel]()
        var epochsArray = [CategoryModel]()
        var authorsArray = [CategoryModel]()
        var mediaArray = [MediaModel]()
        
        for object in genres{
            genresArray.append(object.copy() as! CategoryModel)
        }
        
        for object in kinds{
            kindsArray.append(object.copy() as! CategoryModel)
        }

        for object in epochs{
            epochsArray.append(object.copy() as! CategoryModel)
        }
        
        for object in authors{
            authorsArray.append(object.copy() as! CategoryModel)
        }
        
        for object in media{
            mediaArray.append(object.copy() as! MediaModel)
        }
        
        let model = BookDetailsModel(title: title, simple_thumb: simple_thumb, url: url, simple_cover: simple_cover, pdf: pdf, epub: epub, audio_length: audio_length, cover_color: cover_color, genres: genresArray, kinds: kindsArray, media: mediaArray, epochs: epochsArray, authors: authorsArray, fragmentTitle: fragmentTitle, fragmentHtml: fragmentHtml, slug: slug)
        if currentChapter > 0 {
            model.currentChapter = currentChapter
        }
        if currentAudioChapter > 0 {
            model.currentAudioChapter = currentAudioChapter
        }
        if totalAudioChapters > 0 {
            model.totalAudioChapters = totalAudioChapters
        }
        if totalChapters > 0 {
            model.totalAudioChapters = totalAudioChapters
        }
        return model
    }
    
    func setInitialAudiobookChaptersValuesIfNeeded() {
        if totalAudioChapters == 0 && currentAudioChapter == 0 {
            DatabaseManager.shared.updateCurrentChapters(bookDetailsModel: self, currentChapter: nil, totalChapters: nil, currentAudioChapter: 0, totalAudioChapters: getAudiobookFilesUrls().count)
        }
    }
}
