//
//  MainViewController.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 18/06/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit
import SideMenu

class MainViewController: WLViewController{
    
    var leftBarButtonItemShouldOpenMenu = true
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        if leftBarButtonItemShouldOpenMenu {
            navigationItem.leftBarButtonItem = UIBarButtonItem(image: #imageLiteral(resourceName: "navbar_menu"), style: .plain, target: self, action: #selector(presentMenu))
        }
    }
    
    deinit {
        print("deinit \(self)")
    }
    
    @objc func presentMenu() {
        present(SideMenuManager.default.menuLeftNavigationController!, animated: true, completion: nil)
    }
}
