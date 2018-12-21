//
//  BookDetailsHeaderTableViewCell.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 19/06/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit
import Kingfisher

class BookDetailsHeaderTableViewCell: WLTableViewCell {
    
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var bgImageView: UIImageView!
    @IBOutlet weak var bgOverlayView: UIView!
    @IBOutlet weak var miniatureImageView: UIImageView!

    var height: CGFloat!
    
    class func instance(height: CGFloat) -> BookDetailsHeaderTableViewCell{
        let cell = BookDetailsHeaderTableViewCell.instance(type: BookDetailsHeaderTableViewCell.self)
        cell.height = height
        return cell
    }

    override func awakeFromNib() {
        super.awakeFromNib()
        bgOverlayView.alpha = 0.7
    }
    
    func setup(bookModel: BookDetailsModel, topColor: UIColor){
        
        bgOverlayView.backgroundColor = topColor
        let titleAttributedText = bookModel.getAttributedAuthorAndTitle(titleFont: UIFont.systemFont(ofSize: 22, weight: .light), descFont: UIFont.systemFont(ofSize: 28, weight: .light))
        titleLabel.attributedText = titleAttributedText
        
        if let url = bookModel.getCoverThumbUrl(){
            ImageDownloader.default.downloadImage(with: url, options: [], progressBlock: nil) {
                [weak self] (image, error, url, data) in
                if let image = image{
                    self?.bgImageView.image = image
                    self?.miniatureImageView.image = image
                }
            }
        }
    }

    override func getHeight() -> CGFloat {
        return height
    }
}
