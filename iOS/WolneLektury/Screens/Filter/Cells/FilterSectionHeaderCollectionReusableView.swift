//
//  FilterSectionHeaderCollectionReusableView.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 12/06/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit

protocol FilterSectionHeaderCollectionReusableViewDelegate: class {
    func filterSectionRefreshButtonTapped(section: FilterSection)
}

class FilterSectionHeaderCollectionReusableView: UICollectionReusableView {
    var delegate: FilterSectionHeaderCollectionReusableViewDelegate?

    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var refreshButton: ActivityIndicatorButton!
    var filterSection : FilterSection!
    
    func setup(filterSection: FilterSection, isDownloading: Bool){
        self.filterSection = filterSection
        titleLabel.text = filterSection.title
        refreshButton.setIndicatorButtonState(state: isDownloading ?  .loading : .button)
    }
    
    @IBAction func refreshButtonAction(_ sender: Any) {
        delegate?.filterSectionRefreshButtonTapped(section: filterSection)
    }
}
