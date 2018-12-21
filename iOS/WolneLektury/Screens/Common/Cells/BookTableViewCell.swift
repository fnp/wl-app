//
//  BookTableViewCell.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 14/06/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit
import Kingfisher

protocol BookTableViewCellDelegate: class{
    func bookTableViewCellDelegateDidTapTrashButton(bookSlug: String, indexPath: IndexPath?)
}

class BookTableViewCell: UITableViewCell {
    
    weak var delegate: BookTableViewCellDelegate?
    
    @IBOutlet weak var coverImageView: UIImageView!
    @IBOutlet weak var authorLabel: UILabel!
    @IBOutlet weak var titleLabel: UILabel!
    
    @IBOutlet weak var epochTitleLabel: UILabel!
    @IBOutlet weak var kindTitleLabel: UILabel!
    @IBOutlet weak var genreTitleLabel: UILabel!
    
    @IBOutlet weak var epochLabel: UILabel!
    @IBOutlet weak var kindLabel: UILabel!
    @IBOutlet weak var genreLabel: UILabel!
    
    @IBOutlet weak var descFirstImageView: UIImageView!
    @IBOutlet weak var descFirstLabel: UILabel!
    @IBOutlet weak var descSecondImageView: UIImageView!
    @IBOutlet weak var descSecondLabel: UILabel!
    
    @IBOutlet weak var epochTopConstraint: NSLayoutConstraint!
    
    @IBOutlet weak var bookDescriptionView: BookDescriptionView!
    
    private var bookSlug = ""
    private var indexPath: IndexPath?
    
    override func awakeFromNib() {
        super.awakeFromNib()
        coverImageView.layer.cornerRadius = 4
    }

    func setup(bookModel: BookModel){
        
        bookDescriptionView.setup(bookModel: bookModel)
        
        coverImageView.image = nil
        bookSlug = bookModel.slug
        if let url = bookModel.getCoverThumbUrl(){
            
            coverImageView.kf.setImage(with: ImageResource(downloadURL: url),
                                           placeholder: #imageLiteral(resourceName: "list_nocover"),
                                           options: [.transition(.fade(1))],
                                           progressBlock: nil,
                                           completionHandler: { (image, error, cacheType, url) in
            })
        }
    }
    
    func setup(bookDetailsModel: BookDetailsModel, delegate: BookTableViewCellDelegate, indexPath: IndexPath){

        self.delegate = delegate
        self.indexPath = indexPath
        
        bookSlug = bookDetailsModel.slug
        bookDescriptionView.setup(bookDetailsModel: bookDetailsModel)
        bookDescriptionView.trashButton.removeTarget(nil, action: nil, for: .allEvents)
        bookDescriptionView.trashButton.addTarget(self, action: #selector(trashButtonAction), for: .touchUpInside)
        
        coverImageView.image = nil
        
        if let url = bookDetailsModel.getCoverThumbUrl(){
            
            coverImageView.kf.setImage(with: ImageResource(downloadURL: url),
                                       placeholder: #imageLiteral(resourceName: "list_nocover"),
                                       options: [.transition(.fade(1))],
                                       progressBlock: nil,
                                       completionHandler: { (image, error, cacheType, url) in
            })
        }
    }
    
    @objc func trashButtonAction(){
        delegate?.bookTableViewCellDelegateDidTapTrashButton(bookSlug: bookSlug, indexPath: indexPath)
    }
}
