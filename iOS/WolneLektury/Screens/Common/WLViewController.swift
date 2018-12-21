//
//  WLViewController.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 25/09/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit
import MatomoTracker

class WLViewController: UIViewController {

    func name() -> String {
        
        return String(describing: type(of: self)).replacingOccurrences(of: "ViewController", with: "")
    }
    
    func parentNames() -> [String] {
        var namesList = [String]()
        if let controller = self.parent as? WLViewController {
            namesList.append(contentsOf: controller.parentNames())
            namesList.append(name())
        }
        else if let controller = self.parent as? UINavigationController {
            for vc in controller.childViewControllers{
                if let vc = vc as? WLViewController {
                    namesList.append(vc.name())
                }
            }
        }
        else {
            namesList.append(name())
        }
        return namesList
    }
    
    func trackScreen() {
        
        print("trackScreen \(parentNames())")
        MatomoTracker.shared.track(view: parentNames())
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        trackScreen()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
}
