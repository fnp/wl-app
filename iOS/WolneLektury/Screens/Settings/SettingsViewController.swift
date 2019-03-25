//
//  SettingsViewController.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 17/09/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit
import UserNotifications
class SettingsViewController: MainViewController {

    @IBOutlet weak var notificationsLabel: UILabel!
    @IBOutlet weak var notificationsSwitch: UISwitch!
    @IBOutlet weak var subscriptionLabel: UILabel!
    @IBOutlet weak var subscriptionStatusLabel: UILabel!
    @IBOutlet weak var deleteFilesLabel: UILabel!
    @IBOutlet weak var deleteFilesButton: UIButton!
    @IBOutlet weak var becomeFriendButton: UIButton!

    override func viewDidLoad() {
        super.viewDidLoad()
        title = "nav_settings".localized
        notificationsLabel.text = "settings_notifications".localized.uppercased()
        subscriptionLabel.text = "subscribtion_state".localized.uppercased()
        deleteFilesLabel.text = "delete_files".localized.uppercased()
        deleteFilesButton.text = "delete".localized.uppercased()
        deleteFilesButton.layer.cornerRadius = 15
        deleteFilesButton.layer.borderColor = UIColor.white.cgColor
        deleteFilesButton.layer.borderWidth = 1.0
        deleteFilesButton.backgroundColor = Constants.Colors.navbarBgColor()
        
        let userIsPremium = DatabaseManager.shared.isUserPremium()
        subscriptionStatusLabel.text = userIsPremium ? "active".localized.uppercased() : "inactive".localized.uppercased()
        
        var becomeFriendButtonHidden = DatabaseManager.shared.isUserPremium()
        if Constants.donateEnabled == false {
            becomeFriendButtonHidden = true
        }
        
        becomeFriendButton.isHidden = becomeFriendButtonHidden
        becomeFriendButton.layer.cornerRadius = 18
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        updateNotificationsSwitchValue()
    }

    func updateNotificationsSwitchValue() {
        if #available(iOS 10.0, *) {
            let current = UNUserNotificationCenter.current()
            current.getNotificationSettings(completionHandler: { [weak self] settings in
                DispatchQueue.main.async {
                    switch settings.authorizationStatus {
                    case .notDetermined:
                        self?.notificationsSwitch.setOn(false, animated: true)
                    // Authorization request has not been made yet
                    case .denied:
                        self?.notificationsSwitch.setOn(false, animated: true)
                        // User has denied authorization.
                    // You could tell them to change this in Settings
                    case .authorized:
                        self?.notificationsSwitch.setOn(true, animated: true)
                    case .provisional:
                        self?.notificationsSwitch.setOn(true, animated: true)
                    }
                }

            })
        } else {
            // Fallback on earlier versions
            if UIApplication.shared.isRegisteredForRemoteNotifications {
                notificationsSwitch.setOn(true, animated: true)
            } else {
                notificationsSwitch.setOn(false, animated: true)
            }
        }
    }
    
    @IBAction func deleteFilesButtonAction() {
        
        let alertController = UIAlertController(
            title: nil,
            message: "delete_files_question".localized,
            preferredStyle: UIAlertControllerStyle.alert
        )
        
        let yesAction = UIAlertAction(title: "yes".localized, style: UIAlertActionStyle.default) { [weak self]
            (result : UIAlertAction) -> Void in
            self?.deleteAllFiles()
        }
        
        let noAction = UIAlertAction(title: "no".localized, style: UIAlertActionStyle.cancel) { [weak self]
            (result : UIAlertAction) -> Void in
        }
        alertController.addAction(yesAction)
        alertController.addAction(noAction)

        self.present(alertController, animated: true, completion: nil)
    }
    
    private func deleteAllFiles() {
        
        PlayerController.shared.stopAndClear()
        DatabaseManager.shared.removeAllBooksFromDownloaded()
        presentToast(message: "all_files_removed".localized)
    }
    
    @IBAction func switchAction() {
        UIApplication.shared.openURL(URL(string: UIApplicationOpenSettingsURLString)!)
    }
    
    @IBAction func becomeFriendButtonAction() {
        appDelegate.mainNavigator.presentPayPalForm()
    }
}
