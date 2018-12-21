//
//  MenuLineTableViewCell.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 30/05/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit

class MenuLineTableViewCell: WLTableViewCell {

    class func instance() -> MenuLineTableViewCell{
        let cell = MenuLineTableViewCell.instance(type: MenuLineTableViewCell.self)
        return cell
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
        backgroundColor = .clear
        contentView.backgroundColor = .clear
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    override func getHeight() -> CGFloat {
        return 23
    }
    
}
