//
//  MenuTableViewCell.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 29/05/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit

class MenuTableViewCell: WLTableViewCell {

    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var iconImageView: UIImageView!
    @IBOutlet weak var selectedIndicator: UIView!
    private(set) var menuItem: MenuItem!
    
    class func instance(menuItem: MenuItem) -> MenuTableViewCell{
        let cell = MenuTableViewCell.instance(type: MenuTableViewCell.self)
        cell.setup(menuItem: menuItem, selected: false)
        return cell
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
        backgroundColor = UIColor.clear
        contentView.backgroundColor = UIColor.clear
    }
    
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
        selectedIndicator.isHidden = !selected
        iconImageView.tintColor = menuItem == .premium ? MenuItem.premium.tintColor : (selected ? UIColor.white : Constants.Colors.menuTintColor())
    }
    
    func setup(menuItem: MenuItem, selected:Bool) {
        setSelected(selected, animated: false)
        self.menuItem = menuItem
        iconImageView.image = menuItem.image
        titleLabel.text = menuItem.title
        let tintColor = menuItem.tintColor
        titleLabel.textColor = tintColor
    }
    
    override func getHeight() -> CGFloat {
        return 33
    }
}
