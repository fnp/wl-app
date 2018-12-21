//
//  LoginViewController.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 18/09/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit

class LoginViewController: WLViewController {

    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var desc1: UILabel!
    @IBOutlet weak var desc2: UILabel!
    @IBOutlet weak var desc3: UILabel!
    @IBOutlet weak var desc1Checkmark: UIImageView!
    @IBOutlet weak var desc2Checkmark: UIImageView!
    @IBOutlet weak var desc3Checkmark: UIImageView!

    @IBOutlet weak var loginButton: UIButton!
    

    override func viewDidLoad() {
        super.viewDidLoad()
        
        titleLabel.text = "login_title".localized
        desc1.text = "login_benefits".localized
        desc2.text = "login_benefits_2".localized
        desc3.text = "login_benefits_3".localized
        loginButton.text = "menu_login".localized.uppercased()
        loginButton.layer.cornerRadius = 18
        loginButton.layer.borderWidth = 1.0
        loginButton.layer.borderColor = UIColor.white.cgColor
        let color = Constants.Colors.orangeColor()
        desc1Checkmark.tintColor = color
        desc2Checkmark.tintColor = color
        desc3Checkmark.tintColor = color

    }
    
    @IBAction func loginButtonAction(_ sender: Any) {
        appDelegate.login(fromViewController: self.navigationController!)
    }
}
