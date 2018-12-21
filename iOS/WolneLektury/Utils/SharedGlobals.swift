//
//  SharedGlobals.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 23/08/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit

class SharedGlobals: NSObject {
    
    static let shared = SharedGlobals()
        
    let kFirstUse = "user_first_use"
    let kCanShowNewCardAlert = "can_show_new_card_alert"
    
    func isFirstUse()-> Bool{
        let defaults = UserDefaults.standard
        if (defaults.value(forKey: kFirstUse) as? Bool) != nil{
            return false
        }
        else{
            defaults.set(false, forKey: kFirstUse)
            defaults.synchronize()
            return true
        }
    }
    
    var canShowNewCardAlert: Bool{
        get{
            let defaults = UserDefaults.standard
            if let value = defaults.value(forKey: kCanShowNewCardAlert) as? Bool{
                return value
            }
            return true
        }
        set(newVal){
            let defaults = UserDefaults.standard
            defaults.set(newVal, forKey: kCanShowNewCardAlert)
            defaults.synchronize()
        }
    }
}
