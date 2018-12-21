//
//  FilterOnlyLecturesReusableView.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 12/06/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit

protocol FilterOnlyLecturesReusableViewDelegate: class {
    func filterOnlyLecturesReusableViewSwitchValueChanged(value: Bool, isAudiobook: Bool)
}

class FilterOnlyLecturesReusableView: UICollectionReusableView {
    var delegate: FilterOnlyLecturesReusableViewDelegate?
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var onSwitch: UISwitch!
    var isAudiobook: Bool = false {
        didSet{
            titleLabel.text = isAudiobook ? "has_audiobook".localized.uppercased() : "only_lecture".localized.uppercased()
        }
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
        titleLabel.text = "only_lecture".localized.uppercased()
    }
    
    func setup(isAudiobook: Bool){
        self.isAudiobook = isAudiobook
    }

    @IBAction func switchValueChanged(_ sender: Any) {
        delegate?.filterOnlyLecturesReusableViewSwitchValueChanged(value: onSwitch.isOn, isAudiobook: isAudiobook)
    }
}
