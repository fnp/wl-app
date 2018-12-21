//
//  AboutViewController.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 18/09/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit
import MessageUI

class AboutViewController: MainViewController, MFMailComposeViewControllerDelegate {
    
    @IBOutlet weak var firstTextView: UITextView!
    @IBOutlet weak var secondLabel: UILabel!
    @IBOutlet weak var thirdLabel: UILabel!
    @IBOutlet weak var contactButton: UIButton!
    @IBOutlet weak var becomeFriendButton: UIButton!
    @IBOutlet weak var logoTopConstraint: NSLayoutConstraint!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        title = "nav_about".localized

        firstTextView.textContainerInset = UIEdgeInsets.zero
        firstTextView.textContainer.lineFragmentPadding = 0
 
        becomeFriendButton.layer.cornerRadius = 18
        
        if DatabaseManager.shared.isUserPremium() || Constants.donateEnabled == false{
            becomeFriendButton.isHidden = true
            logoTopConstraint.constant = 30
        }
        else {
            becomeFriendButton.isHidden = false
            logoTopConstraint.constant = 70
        }
        
        
        if let htmlString = try? NSAttributedString(html: "about_text".localized){
            firstTextView.attributedText =  htmlString
        }
        else {
            firstTextView.text = "about_text".localized
        }
        firstTextView.tintColor = Constants.Colors.darkGreenBgColor()

        secondLabel.text = "about_text_fundation".localized
        thirdLabel.text = "about_text_mkdn".localized
        contactButton.text = "about_text_contact".localized
    }
    
    @IBAction func becomeFriendButtonAction(_ sender: Any) {
        onBecomeFriendButtonTapped()
    }
    
    @IBAction func contactButtonAction(_ sender: Any) {

        let email = contactButton.text
        if let url = URL(string: "mailto:\(email)") {
            if #available(iOS 10.0, *) {
                UIApplication.shared.open(url)
            } else {
                UIApplication.shared.openURL(url)
            }
        }
    }
}
