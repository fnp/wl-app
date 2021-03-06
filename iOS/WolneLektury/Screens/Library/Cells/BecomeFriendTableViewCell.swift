//
//  BecomeFriendTableViewCell.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 31/08/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit

protocol BecomeFriendTableViewCellDelegate: class{
    func becomeFriendTableViewCellTapped()
}

class BecomeFriendTableViewCell: WLTableViewCell {

    @IBOutlet weak var arrowImageView: UIImageView!
    @IBOutlet weak var button: UIButton!
    weak var delegate: BecomeFriendTableViewCellDelegate?
    
    class func instance(delegate: BecomeFriendTableViewCellDelegate?) -> BecomeFriendTableViewCell{
        let cell = BecomeFriendTableViewCell.instance(type: BecomeFriendTableViewCell.self)
        cell.delegate = delegate
        return cell
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
        button.layer.cornerRadius = 15
        button.text = "become_a_friend".localized.uppercased()
        arrowImageView.tintColor = UIColor.white
        contentView.backgroundColor = Constants.donateEnabled ? UIColor(red: 0, green: 103.0/255.0, blue: 109.0/255.0, alpha: 1.0) : UIColor.white
    }
    
    override func getHeight() -> CGFloat {
        
        return Constants.donateEnabled ? 40 : 44
    }
    
    @IBAction func buttonAction(_ sender: Any) {
        delegate?.becomeFriendTableViewCellTapped()
    }
}
