//
//  ActivityIndicatorButton.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 12/06/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit

enum ActivityIndicatorButtonState{
    case loading
    case button
    case hidden
}

class ActivityIndicatorButton: UIButton {

    private var indicatorButtonState: ActivityIndicatorButtonState = .loading
    
    private var activityIndicatorView: UIActivityIndicatorView!
    override init(frame: CGRect) {
        super.init(frame: frame)
        customInit()
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        customInit()
    }
    
    func customInit() {
        activityIndicatorView = UIActivityIndicatorView(activityIndicatorStyle: .whiteLarge)
        activityIndicatorView.hidesWhenStopped = true
        activityIndicatorView.tintColor = tintColor
        activityIndicatorView.color = tintColor

        activityIndicatorView.translatesAutoresizingMaskIntoConstraints = false
        self.addSubview(activityIndicatorView)
        activityIndicatorView.centerInSuperview()
        setIndicatorButtonState(state: .loading)
        titleLabel?.text = ""
    }
    
    func setIndicatorButtonState(state: ActivityIndicatorButtonState){
        indicatorButtonState = state
        
        switch state {
        case .loading:
            isHidden = false
            activityIndicatorView.startAnimating()
            setImage(nil, for: .normal)
        case .button:
            isHidden = false
            activityIndicatorView.stopAnimating()
            setImage(#imageLiteral(resourceName: "ic_reload"), for: .normal)
        case .hidden:
            isHidden = true
        }
    }
}
