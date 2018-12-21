//
//  SearchFilterCollectionViewCell.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 13/06/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit

class SearchFilterCollectionViewCell: UICollectionViewCell {

    @IBOutlet weak var closeImageView: UIImageView!
    @IBOutlet weak var bgView: UIView!
    @IBOutlet weak var titleLabel: UILabel!
    override func awakeFromNib() {
        super.awakeFromNib()
        bgView.layer.cornerRadius = 12
        bgView.layer.borderColor = UIColor.white.cgColor
        bgView.layer.borderWidth = 1
        closeImageView.tintColor = .white
    }
    
    func setup(categoryModel: CategoryModel) {
        titleLabel.text = categoryModel.name
    }
}
