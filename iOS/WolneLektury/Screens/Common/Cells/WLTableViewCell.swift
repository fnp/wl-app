//
//  WLTableViewCell.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 30/05/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit

class WLTableViewCell: UITableViewCell {

    override func awakeFromNib() {
        super.awakeFromNib()
        selectionStyle = .none
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    static var fullNameIdentifier: String {
        var fullName: String = NSStringFromClass(self)
        let range = fullName.range(of: ".", options: .backwards)
        if let range = range {
            fullName = fullName.substring(from: range.upperBound)
        }
        return fullName
    }

    static var reuseIdentifier: String {
        return fullNameIdentifier
    }
    
    class func instance<T>(type: T.Type) -> T{
        let cell = Bundle.main.loadNibNamed(fullNameIdentifier, owner: nil, options: nil)!.first as! T
        return cell
    }
    
    static var nib: UINib? {
        if((Bundle.main.path(forResource: fullNameIdentifier, ofType: "nib")) != nil)
        {
            return UINib(nibName: String(fullNameIdentifier), bundle: nil)
        }
        return nil
    }
    
    func getHeight() -> CGFloat {
        return UITableViewAutomaticDimension
    }

}
