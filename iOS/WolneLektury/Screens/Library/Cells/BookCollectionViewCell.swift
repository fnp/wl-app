//
//  BookCollectionViewCell.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 18/06/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit
import Kingfisher

class BookCollectionViewCell: UICollectionViewCell {

    @IBOutlet weak var bgView: UIView!
    @IBOutlet weak var coverImageView: UIImageView!
    @IBOutlet weak var overlayView: BookImageOverlayView!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        bgView.layer.cornerRadius = 5
    }
    
    func setup(bookModel: BookModel){

        coverImageView.kf.cancelDownloadTask()
        coverImageView.image = #imageLiteral(resourceName: "list_nocover")
        
        if let url = bookModel.getCoverThumbUrl(){
            
            coverImageView.kf.setImage(with: ImageResource(downloadURL: url),
                                       placeholder: #imageLiteral(resourceName: "list_nocover"),
                                       options: [.transition(.fade(1))],
                                       progressBlock: nil,
                                       completionHandler: { (image, error, cacheType, url) in
            })
        }
        overlayView.setup(bookModel: bookModel)
    }
    
    
}
