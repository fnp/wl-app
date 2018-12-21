//
//  WLNavigationController.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 28/09/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit

class WLNavigationController: UINavigationController {

    override var shouldAutorotate: Bool {
        return true
    }
    
    override var supportedInterfaceOrientations: UIInterfaceOrientationMask {
        
        return UIDevice.current.userInterfaceIdiom == .pad ? .all : .portrait
    }

}
