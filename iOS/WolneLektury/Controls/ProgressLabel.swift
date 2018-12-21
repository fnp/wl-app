//
//  ProgressLabel.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 20/09/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit

// from https://stackoverflow.com/a/46047570/871425
class ProgressLabel: UIView {
    
    private var privateColor = UIColor.green
    @IBInspectable var color: UIColor {
        get {
            return privateColor
        }set {
            privateColor = newValue
        }
    }

    var textColor = UIColor.white
    var font = UIFont.systemFont(ofSize: 15)
    var fullProgress: Bool = false
    var customText: String? {
        didSet{
            if customText != nil{
                setNeedsDisplay()
            }
        }
    }
    var progress: Float = 0 {
        didSet {
            customText = nil
            progress = Float.minimum(100.0, Float.maximum(progress, 0.0))
            self.setNeedsDisplay()
        }
    }
    
    override func draw(_ rect: CGRect) {
        let context = UIGraphicsGetCurrentContext()!
        
        // Set up environment.
        let size = self.bounds.size

        layer.borderColor = privateColor.cgColor
        layer.borderWidth = 1.0
        layer.cornerRadius = 10
        clipsToBounds = true
        
        var prg = progress
        var progressMessage = NSString(format:"Pobieranie %d%%", Int(prg))

        if let customText = customText{
            prg = fullProgress ? 100 : 0
            progressMessage = customText as NSString
        }
        
        // Prepare progress as a string.
        var attributes: [NSAttributedStringKey:Any] = [ NSAttributedStringKey.font : font ]
        let textSize = progressMessage.size(withAttributes: attributes)
        let progressX = ceil(CGFloat(prg) / 100 * size.width)
        let textPoint = CGPoint(x: ceil((size.width - textSize.width) / 2.0), y: ceil((size.height - textSize.height) / 2.0))
        
        // Draw background + foreground text
        privateColor.setFill()
        context.fill(self.bounds)
        attributes[NSAttributedStringKey.foregroundColor] = textColor
        let rct = CGRect(x: 20, y: 12, width: bounds.size.width - 20, height: bounds.size.height - 13)
        progressMessage.draw(in: rct, withAttributes: attributes)

        // Clip the drawing that follows to the remaining progress' frame.
        context.saveGState()
        let remainingProgressRect = CGRect(x: progressX, y: 0.0, width: size.width - progressX, height: size.height)
        context.addRect(remainingProgressRect)
        context.clip()
        
        // Draw again with inverted colors.
        textColor.setFill()
        context.fill(self.bounds)
        attributes[NSAttributedStringKey.foregroundColor] = privateColor
        progressMessage.draw(in: rct, withAttributes: attributes)

        context.restoreGState()
    }
}
