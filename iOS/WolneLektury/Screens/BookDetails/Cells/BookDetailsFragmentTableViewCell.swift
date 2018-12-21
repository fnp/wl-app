//
//  BookDetailsFragmentTableViewCell.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 20/06/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit

class BookDetailsFragmentTableViewCell: WLTableViewCell {

    @IBOutlet weak var htmlLabel: UILabel!
    @IBOutlet weak var titleLabel: UILabel!
    
    class func instance() -> BookDetailsFragmentTableViewCell{
        let cell = BookDetailsFragmentTableViewCell.instance(type: BookDetailsFragmentTableViewCell.self)
        return cell
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
    }
    
    func setup(fragmentTitle: String, fragmentHtml: String){
        
        if let htmlString = try? NSAttributedString(html: fragmentHtml){
            let mutableAttributedString = NSMutableAttributedString(attributedString: htmlString)
            mutableAttributedString.addAttribute(NSAttributedStringKey.font, value: UIFont.italicSystemFont(ofSize: 12), range: NSMakeRange(0, mutableAttributedString.length))
            htmlLabel.attributedText = mutableAttributedString
        }
        
        titleLabel.text = fragmentTitle
    }
}
