//
//  SupportUsViewController.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 18/09/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit

class SupportUsViewController: MainViewController {

    @IBOutlet weak var textView: UITextView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        title = "support_us".localized
        
        if let htmlString = try? NSAttributedString(html: "support_us_text".localized){
            textView.attributedText =  htmlString
        }
        else {
            textView.text = "support_us_text".localized
        }
        
        textView.tintColor = Constants.Colors.darkGreenBgColor()
    }

}
