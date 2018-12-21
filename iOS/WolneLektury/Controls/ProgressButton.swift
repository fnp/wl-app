//
//  ProgressButton.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 20/09/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit

class ProgressButton: UIView {

    var progressView: UIView!
    var titleLabel: UILabel!
    var button: UIBarButtonItem!
    var progressWidthConstraint: NSLayoutConstraint!
    
    var bgColor: UIColor = .black{
        didSet {
            progressView.backgroundColor = bgColor
        }
    }
    
    var textColor: UIColor = .black{
        didSet {
            progressView.backgroundColor = bgColor
        }
    }

    override init(frame: CGRect) {
        super.init(frame: frame)
        
        setupView()
        setupConstraints()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func setBackgroundColor(color: UIColor) {
        
    }
    
    func setupView() {
    }
    
    func setupConstraints() {
        
    }
}
