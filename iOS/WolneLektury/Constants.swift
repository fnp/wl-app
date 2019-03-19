//
//  Constants.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 30/05/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit


import UIKit
import MZDownloadManager

struct Constants {
    
    static let SAMPLE_FLOAT: Float = 4.25
    static let ebookPath = MZUtility.baseFilePath + "/ebooks"
    static let audiobookPath = MZUtility.baseFilePath + "/audiobooks"
    static let callbackOauthHost = "oauth-callback"
    static let callbackPaypalSuccessHost = "paypal_return"
    static let callbackPaypalErrorHost = "paypal_error"
    static let authorizationUrlFormat = "https://wolnelektury.pl/api/oauth/authorize/?oauth_token=%@&oauth_callback=wolnelekturyapp://oauth-callback/"
    static let webPaypalFormUrl = "https://wolnelektury.pl/paypal/app-form/"
    static let donateEnabled: Bool = true

    struct StoryboardIds {
        static let SampleController = "SampleControllerID"
    }
    
    struct CellIds {
        //        static let SampleCell = "SampleCellId"
    }
    
    struct Segues {
        static let SampleSegue = "sampleSegue"
    }
    
    struct Notifications {
        static let SampleNotification = "SampleNotification"
    }
    
    struct Colors {
        static let menuTintColor = {
            return Constants.Colors.lightGreenBgColor()
        }
        
        static let navbarBgColor = {
            return Constants.Colors.lightGreenBgColor()
        }
        
        static let lightGreenBgColor = {
            return UIColor(red:0.00, green:0.51, blue:0.53, alpha:1.00)
        }

        static let darkGreenBgColor = {
            return UIColor(red:0.00, green:0.40, blue:0.42, alpha:1.00)
        }
        
        static let grayTextColor = {
            return UIColor(red:0.32, green:0.32, blue:0.32, alpha:1.00)
        }

        static let orangeColor = {
            return UIColor(red:1.0, green:165.0/255.0, blue:0, alpha:1.00)
        }

    }
}
