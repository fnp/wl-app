//
//  SimpleGalleryCollectionViewCell.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 17/09/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit
import Kingfisher
class SimpleGalleryCollectionViewCell: UICollectionViewCell {

    @IBOutlet weak var imageView: UIImageView!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        imageView.clipsToBounds = true
    }
    
    func setup(imageUrlString: String?) {
        imageView.image = nil
        
        if let url = imageUrlString?.getPhotoUrl(){
            
            imageView.kf.setImage(with: ImageResource(downloadURL: url),
                                       placeholder: #imageLiteral(resourceName: "list_nocover"),
                                       options: [.transition(.fade(1))],
                                       progressBlock: nil,
                                       completionHandler: { (image, error, cacheType, url) in
            })
        }
    }
}
