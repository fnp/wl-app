//
//  NewsTableViewCell.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 15/09/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit
import Kingfisher

class NewsTableViewCell: UITableViewCell {

    @IBOutlet weak var miniatureImageBgView: UIView!
    @IBOutlet weak var miniatureImageView: UIImageView!
    @IBOutlet weak var dateLabel: UILabel!
    @IBOutlet weak var titleLabel: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        
        miniatureImageBgView.layer.masksToBounds = false
        miniatureImageBgView.layer.shadowColor = UIColor.black.cgColor
        miniatureImageBgView.layer.shadowOffset = CGSize(width: 0, height: 2)
        miniatureImageBgView.layer.shadowOpacity = 0.2
        miniatureImageBgView.layer.shadowRadius = 2
        miniatureImageBgView.layer.cornerRadius = 4
        miniatureImageView.layer.cornerRadius = 4
        miniatureImageView.clipsToBounds = true
        selectionStyle = .none
        miniatureImageView.isUserInteractionEnabled = true
        selectionStyle = .none
    }
    
    func setup(newsModel: NewsModel){
        dateLabel.text = newsModel.time
        titleLabel.text = newsModel.title
        miniatureImageView.image = nil
        if let url = newsModel.getCoverThumbUrl(){
            
            miniatureImageView.kf.setImage(with: ImageResource(downloadURL: url),
                                       placeholder: #imageLiteral(resourceName: "list_nocover"),
                                       options: [.transition(.fade(1))],
                                       progressBlock: nil,
                                       completionHandler: { (image, error, cacheType, url) in
            })
        }
    }
}
