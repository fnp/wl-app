//
//  BookDetailsSeparatorTableViewCell.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 19/06/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit

class BookDetailsSeparatorTableViewCell: WLTableViewCell {

    class func instance() -> BookDetailsSeparatorTableViewCell{
        let cell = BookDetailsSeparatorTableViewCell.instance(type: BookDetailsSeparatorTableViewCell.self)
        return cell
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
    }
    
    override func getHeight() -> CGFloat {
        return 21
    }
}
