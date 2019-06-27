//
//  BookImageOverlayView.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 18/06/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit

class BookImageOverlayView: DesignableXibView {
    
        @IBOutlet weak var iconsView: UIView!
        @IBOutlet weak var readImageView: UIImageView!
        @IBOutlet weak var audiobookImageView: UIImageView!
        @IBOutlet weak var iconsWidthConstraint: NSLayoutConstraint!
        @IBOutlet weak var titleLabel: UILabel!
        @IBOutlet weak var textBgColorView: UIView!


        override func awakeFromNib() {
            super.awakeFromNib()
            readImageView.layer.cornerRadius = 9
            audiobookImageView.layer.cornerRadius = 9
            readImageView.tintColor = UIColor.white
            audiobookImageView.tintColor = UIColor.white
            
            let color = UIColor(red:0.84, green:0.29, blue:0.19, alpha:0.9)
            readImageView.backgroundColor = color
            audiobookImageView.backgroundColor = color
            textBgColorView.alpha = 0.9

        }
        
        func setup(bookModel: BookModel){
            setup(attributedTitle: bookModel.getAttributedAuthorAndTitle(titleFont: UIFont.systemFont(ofSize: 9, weight: .medium), descFont: UIFont.systemFont(ofSize: 11, weight: .bold)), bgColor: bookModel.bgColor, hasAudio: bookModel.has_audio)

            let titleAttributedText = bookModel.getAttributedAuthorAndTitle(titleFont: UIFont.systemFont(ofSize: 9, weight: .medium), descFont: UIFont.systemFont(ofSize: 11, weight: .bold))
            
            readImageView.backgroundColor = bookModel.bgColor
            audiobookImageView.backgroundColor = bookModel.bgColor
            textBgColorView.backgroundColor = bookModel.bgColor
            titleLabel.attributedText = titleAttributedText

            if bookModel.has_audio{
                audiobookImageView.isHidden = false
                iconsWidthConstraint.constant = 39
            }
            else{
                audiobookImageView.isHidden = true
                iconsWidthConstraint.constant = 18
            }
        }
    
    func setup(bookDetailsModel: BookDetailsModel){
        setup(attributedTitle: bookDetailsModel.getAttributedAuthorAndTitle(titleFont: UIFont.systemFont(ofSize: 9, weight: .medium), descFont: UIFont.systemFont(ofSize: 11, weight: .bold)), bgColor: bookDetailsModel.bgColor, hasAudio: bookDetailsModel.audio_length.count > 0)
        
    }

    func hideTextAndIcons() {
        textBgColorView.isHidden = true
        iconsView.isHidden = true
        titleLabel.isHidden = true
    }
    
    private func setup(attributedTitle: NSAttributedString, bgColor: UIColor, hasAudio: Bool) {
        
        readImageView.backgroundColor = bgColor
        audiobookImageView.backgroundColor = bgColor
        textBgColorView.backgroundColor = bgColor
        titleLabel.attributedText = attributedTitle
        
        if hasAudio{
            audiobookImageView.isHidden = false
            iconsWidthConstraint.constant = 39
        }
        else{
            audiobookImageView.isHidden = true
            iconsWidthConstraint.constant = 18
        }
    }
}

