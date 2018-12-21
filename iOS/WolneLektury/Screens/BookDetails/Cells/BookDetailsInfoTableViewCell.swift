//
//  BookDetailsInfoTableViewCell.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 19/06/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit

class BookDetailsInfoTableViewCell: WLTableViewCell {

    @IBOutlet weak var epochTitleLabel: UILabel!
    @IBOutlet weak var epochDescLabel: UILabel!
    @IBOutlet weak var kindTitleLabel: UILabel!
    @IBOutlet weak var kindDescLabel: UILabel!
    @IBOutlet weak var genreTitleLabel: UILabel!
    @IBOutlet weak var genreDescLabel: UILabel!
 
    class func instance() -> BookDetailsInfoTableViewCell{
        let cell = BookDetailsInfoTableViewCell.instance(type: BookDetailsInfoTableViewCell.self)
        return cell
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
        epochTitleLabel.text = "book_epoch".localized.uppercased()
        kindTitleLabel.text = "book_kind".localized.uppercased()
        genreTitleLabel.text = "book_genre".localized.uppercased()
    }

    func setup(bookModel: BookDetailsModel){
        epochDescLabel.text = bookModel.getEpochs()
        kindDescLabel.text = bookModel.getKinds()
        genreDescLabel.text = bookModel.getGenres()
    }
}
