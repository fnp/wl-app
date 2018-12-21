//
//  BookDescriptionView.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 18/06/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit

class BookDescriptionView: DesignableXibView {

    @IBOutlet weak var authorLabel: UILabel!
    @IBOutlet weak var titleLabel: UILabel!
    
    @IBOutlet weak var epochTitleLabel: UILabel!
    @IBOutlet weak var kindTitleLabel: UILabel!
    @IBOutlet weak var genreTitleLabel: UILabel!
    
    @IBOutlet weak var epochLabel: UILabel!
    @IBOutlet weak var kindLabel: UILabel!
    @IBOutlet weak var genreLabel: UILabel!
    
    @IBOutlet weak var lineView: UIView!
    
    @IBOutlet weak var descFirstImageView: UIImageView!
    @IBOutlet weak var descFirstLabel: UILabel!
    @IBOutlet weak var descSecondImageView: UIImageView!
    @IBOutlet weak var descSecondLabel: UILabel!
    
    @IBOutlet weak var epochTopConstraint: NSLayoutConstraint!
    @IBOutlet weak var titleLabelTrailingConstraint: NSLayoutConstraint!
    
    @IBOutlet weak var trashButton: UIButton!
    
    let orangeTextColor = UIColor(red: 255.0/255.0, green: 165/255.0, blue: 0, alpha: 1.0)
    let whiteTextColor = UIColor.white
    
    override func awakeFromNib() {
        super.awakeFromNib()
        epochTitleLabel.text = "book_epoch".localized.uppercased()
        kindTitleLabel.text = "book_kind".localized.uppercased()
        genreTitleLabel.text = "book_genre".localized.uppercased()
        descFirstImageView.tintColor = lineView.backgroundColor
        descSecondImageView.tintColor = lineView.backgroundColor
        descFirstImageView.image = #imageLiteral(resourceName: "icon_glasses-mid")
        descSecondImageView.image = #imageLiteral(resourceName: "icon_speaker-mid")
        setTrashButtonHidden(isHidden: true)
    }
    
    func setDescViewsVisibility(hidden: Bool){
        descFirstImageView.isHidden = hidden
        descFirstLabel.isHidden = hidden
        descSecondImageView.isHidden = hidden
        descSecondLabel.isHidden = hidden
        
        epochTopConstraint.constant = hidden ? 10 : 30
    }
    
    func setup(bookModel: BookModel, isPremium: Bool = false){
        setDescViewsVisibility(hidden: false)
        
        descSecondImageView.isHidden = !bookModel.has_audio
        descSecondLabel.isHidden = !bookModel.has_audio
        if isPremium{
            
            authorLabel.textColor = whiteTextColor
            epochTitleLabel.textColor = whiteTextColor
            kindTitleLabel.textColor = whiteTextColor
            genreTitleLabel.textColor = whiteTextColor
            
            descFirstImageView.tintColor = whiteTextColor
            descFirstLabel.textColor = whiteTextColor
            descSecondImageView.tintColor = whiteTextColor
            descSecondLabel.textColor = whiteTextColor
            
            lineView.backgroundColor = whiteTextColor
            
            titleLabel.textColor = orangeTextColor
            epochLabel.textColor = orangeTextColor
            kindLabel.textColor = orangeTextColor
            genreLabel.textColor = orangeTextColor
        }
        
        authorLabel.text = bookModel.author
        titleLabel.text = bookModel.title
        epochLabel.text = bookModel.epoch
        kindLabel.text = bookModel.kind
        genreLabel.text = bookModel.genre
    }
    
    func setup(bookDetailsModel: BookDetailsModel){
        setDescViewsVisibility(hidden: false)
        
        authorLabel.text = bookDetailsModel.getAuthor()
        titleLabel.text = bookDetailsModel.title
        epochLabel.text = bookDetailsModel.getEpochs()
        kindLabel.text = bookDetailsModel.getKinds()
        genreLabel.text = bookDetailsModel.getGenres()
        
        if bookDetailsModel.totalChapters > 0 && bookDetailsModel.currentChapter <= bookDetailsModel.totalChapters{
            let progress = Double(bookDetailsModel.currentChapter)/Double(bookDetailsModel.totalChapters) * 100.0
            descFirstLabel.text = String(format: "reading_progress".localized, progress).uppercased()
        }
        else {
            descFirstLabel.text = ""
        }
        
        descSecondImageView.isHidden = bookDetailsModel.audio_length.count == 0
        descSecondLabel.isHidden = bookDetailsModel.audio_length.count == 0

        /*
        if bookDetailsModel.totalAudioChapters > 0 && bookDetailsModel.currentAudioChapter < bookDetailsModel.totalAudioChapters{
            let progress = Double(bookDetailsModel.currentAudioChapter + 1)/Double(bookDetailsModel.totalAudioChapters) * 100.0
            descSecondLabel.text = String(format: "listening_progress".localized, progress).uppercased()
        }
        else {
            descSecondLabel.text = ""
        }
        */
        setTrashButtonHidden(isHidden: false)
    }
    
    func setTrashButtonHidden(isHidden: Bool){
        trashButton.isHidden = isHidden
        titleLabelTrailingConstraint.constant = isHidden ? 0 : 30
    }

}
