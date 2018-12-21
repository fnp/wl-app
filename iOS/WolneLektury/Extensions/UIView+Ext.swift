//
//  UIView+Ext.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 30/05/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import Foundation

import UIKit

extension UIView{
    func addConstraintsFllingContainer(toView: UIView){
        
        toView.translatesAutoresizingMaskIntoConstraints = false
        let viewsDictionary:[String: AnyObject] = ["toView": toView]
        
        addConstraints(NSLayoutConstraint.constraints(withVisualFormat: "V:|[toView]|", options: [], metrics: nil, views: viewsDictionary))
        addConstraints(NSLayoutConstraint.constraints(withVisualFormat: "H:|[toView]|", options: [], metrics: nil, views: viewsDictionary))
    }
    
    func centerInSuperview() {
        self.centerHorizontallyInSuperview()
        self.centerVerticallyInSuperview()
    }
    
    func addConstraingWidth(width: CGFloat){
        let c: NSLayoutConstraint = NSLayoutConstraint(item: self, attribute: .width, relatedBy: .equal, toItem: nil, attribute: .notAnAttribute, multiplier: 1, constant: width)
        addConstraint(c)
    }
    
    func centerHorizontallyInSuperview(){
        let c: NSLayoutConstraint = NSLayoutConstraint(item: self, attribute: .centerX, relatedBy: .equal, toItem: self.superview, attribute: .centerX, multiplier: 1, constant: 0)
        self.superview?.addConstraint(c)
    }
    
    func centerVerticallyInSuperview(){
        let c: NSLayoutConstraint = NSLayoutConstraint(item: self, attribute: .centerY, relatedBy: .equal, toItem: self.superview, attribute: .centerY, multiplier: 1, constant: 0)
        self.superview?.addConstraint(c)
    }
    
    func addDropShadow(height: CGFloat = 2, radius: CGFloat = 3) {
        layer.masksToBounds = false
        layer.shadowColor = UIColor.black.cgColor
        layer.shadowOffset = CGSize(width: 0, height: height)
        layer.shadowOpacity = 0.2
        layer.shadowRadius = radius
    }
    
    func rotate(angle: Double) {
        let radians = angle / 180.0 * Double.pi
        let rotation = CGAffineTransform.init(rotationAngle:CGFloat(radians))// CGAffineTransformRotate(self.transform, CGFloat(radians));
        self.transform = rotation
    }
}
