//
//  NSObject+Ext.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 30/05/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import Foundation
import UIKit

extension NSObject
{
    var appDelegate:AppDelegate {
        return UIApplication.shared.delegate as! AppDelegate
    }
    
    func ebookExists(bookSlug: String) -> Bool {
        let path = FileType.ebook.pathForFileName(filename: bookSlug, bookSlug: bookSlug)
        print(path)
        return FileManager.default.fileExists(atPath: path)
    }
    
    static func audiobookExists(audioBookUrlString: String, bookSlug: String) -> Bool {
        let fileName = (audioBookUrlString as NSString).lastPathComponent
        return FileManager.default.fileExists(atPath: FileType.audiobook.pathForFileName(filename: fileName, bookSlug: bookSlug))
    }
    
    static func audiobookPathIfExists(audioBookUrlString: String, bookSlug: String) -> URL? {
        let fileName = (audioBookUrlString as NSString).lastPathComponent
        let path = FileType.audiobook.pathForFileName(filename: fileName, bookSlug: bookSlug)
        
        if FileManager.default.fileExists(atPath: path) {
            return URL(fileURLWithPath: path)
        }
        return nil
    }

}
