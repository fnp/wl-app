//
//  FilterCollectionViewCell.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 12/06/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit

class FilterCollectionViewCell: UICollectionViewCell {
    
    @IBOutlet weak var checkboxBgImageView: UIImageView!
    @IBOutlet weak var checkboxTickImageView: UIImageView!
    @IBOutlet weak var titleLabel: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        checkboxTickImageView.tintColor = .white
    }
    
    func setup(categoryModel: CategoryModel){
        
        titleLabel.text = categoryModel.name.uppercased()
        setChecked(value: categoryModel.checked)
    }
    
    func setChecked(value: Bool){
        
        checkboxTickImageView.isHidden = !value
        checkboxBgImageView.tintColor = value ? .white : UIColor(red:0.51, green:0.73, blue:0.73, alpha:1.00)
    }
}
