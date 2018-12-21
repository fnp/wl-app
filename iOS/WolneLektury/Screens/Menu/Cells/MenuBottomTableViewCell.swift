//
//  MenuBottomTableViewCell.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 30/05/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit
protocol MenuBottomTableViewCellDelegate: class{
    
    func menuBottomProfileButtonTapped()
    func menuBottomSignOutButtonButtonTapped()
}

class MenuBottomTableViewCell: WLTableViewCell {
    weak var delegate: MenuBottomTableViewCellDelegate?
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var showProfileButton: UIButton!
    @IBOutlet weak var signOutButton: UIButton!
    
    class func instance(delegate: MenuBottomTableViewCellDelegate?) -> MenuBottomTableViewCell{
        let cell = MenuBottomTableViewCell.instance(type: MenuBottomTableViewCell.self)
        cell.delegate = delegate
        return cell
    }

    override func awakeFromNib() {
        super.awakeFromNib()
        
        showProfileButton.layer.cornerRadius = 13
        showProfileButton.layer.borderColor = UIColor.white.cgColor
        showProfileButton.layer.borderWidth = 1
        
        let titleText = NSMutableAttributedString(attributedString: NSAttributedString(string: "logged_as".localized + "\n", font: UIFont.systemFont(ofSize: 10)))
        titleText.append(NSAttributedString(string: "Zuzanna Stańska", font: UIFont.systemFont(ofSize: 12)))
        titleLabel.attributedText = titleText
        showProfileButton.text = "show_profile".localized
        signOutButton.text = "sign_out".localized
        
        let bgColor = Constants.Colors.menuTintColor()
        backgroundColor = bgColor
        contentView.backgroundColor = bgColor
    }
    
    override func getHeight() -> CGFloat {
        return 180
    }
    
    @IBAction func signoutButtonAction(_ sender: Any) {
        delegate?.menuBottomSignOutButtonButtonTapped()
    }
    
    @IBAction func profileButtonAction(_ sender: Any) {
        delegate?.menuBottomProfileButtonTapped()
    }
}
