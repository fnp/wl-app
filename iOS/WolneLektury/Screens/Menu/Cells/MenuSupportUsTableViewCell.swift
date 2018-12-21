//
//  MenuSupportUsTableViewCell.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 30/05/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit

protocol MenuSupportUsTableViewCellDelegate: class{
    
    func menuSupportUsButtonTapped()
}

class MenuSupportUsTableViewCell: WLTableViewCell {
    weak var delegate: MenuSupportUsTableViewCellDelegate?
    @IBOutlet weak var button: UIButton!
    
    class func instance(delegate: MenuSupportUsTableViewCellDelegate?) -> MenuSupportUsTableViewCell{
        let cell = MenuSupportUsTableViewCell.instance(type: MenuSupportUsTableViewCell.self)
        cell.delegate = delegate
        return cell
    }

    override func awakeFromNib() {
        super.awakeFromNib()
        button.layer.cornerRadius = 15
        button.text = "support_us".localized.uppercased()
        backgroundColor = UIColor.clear
        contentView.backgroundColor = UIColor.clear
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    @IBAction func buttonAction(_ sender: Any) {
        delegate?.menuSupportUsButtonTapped()
    }
    
    override func getHeight() -> CGFloat {
        return 50
    }
}
