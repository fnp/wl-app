//
//  UIViewController+Ext.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 30/05/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import Foundation
import UIKit
import MBProgressHUD

extension UIViewController
{
    var syncManager:SyncManager{
        return appDelegate.syncManager
    }
}

extension UINavigationController{
    open override var preferredStatusBarStyle : UIStatusBarStyle {
        return .lightContent
    }
}

extension UIViewController{
    

    static func createViewControllerFromStoryboard(storyboardName:String = "Login") -> UIViewController {
        
        let viewController:UIViewController = UIStoryboard(name:storyboardName, bundle: nil).instantiateViewController(withIdentifier: String(describing: self)) as UIViewController
        return viewController
    }
    
    class func instance() -> Self
    {
        return instantiateFromStoryboardHelper()
    }
    
    private class func instantiateFromStoryboardHelper<T>() -> T
    {
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        let controller = storyboard.instantiateViewController(withIdentifier: "\(self)") as! T
        return controller
    }
}

extension UIViewController{
    
    func presentAlertViewController(title:String,message:String, okAction:UIAlertAction?)
    {
        let alertController = UIAlertController(title:title, message:message, preferredStyle: .alert)
        
        var cancelTitle = "button_cancel".localized
        if okAction == nil{
            cancelTitle = "button_ok".localized
        }
        
        let cancelAction = UIAlertAction(title: cancelTitle, style: .cancel, handler: nil)
        alertController.addAction(cancelAction)
        
        if okAction != nil
        {
            alertController.addAction(okAction!)
        }
        self.present(alertController, animated: true, completion:nil)
    }
    
    func presentProblemOccuredAlert(message: String){
        presentAlertViewController(title: "problem_occurred".localized, message: message, okAction: nil)
    }
    
    func presentToast(message: String) {
        view.makeToast(message, duration: 3.0, position: .bottom)
    }
    
    func share(string: String, button: UIButton) {
        let items = [string]
        let activity = UIActivityViewController(activityItems: items, applicationActivities: nil)
        
        // Pop style on iPad
        if let actv = activity.popoverPresentationController {
            actv.sourceView = button
        }

        self.present(activity, animated: true, completion: nil)
    }
    
    func onBecomeFriendButtonTapped(){
        guard Constants.donateEnabled else { return }

        if syncManager.isLoggedIn() {
            appDelegate.mainNavigator.presentPayPalForm()
        }
        else {
            appDelegate.mainNavigator.presentLogin(push: true)
        }
    }
}

extension UIViewController{
    func setIsPopup(){
        providesPresentationContextTransitionStyle = true
        definesPresentationContext = true
        modalPresentationStyle = .overCurrentContext
        modalTransitionStyle = .crossDissolve
    }
}

extension UIViewController{
    
    func containerAdd(childViewController: UIViewController, toView: UIView){
        
        addChildViewController(childViewController)
        childViewController.view.frame = toView.bounds
        toView.addSubview(childViewController.view)
        childViewController.view.translatesAutoresizingMaskIntoConstraints = false
        childViewController.view.topAnchor.constraint(equalTo: toView.topAnchor).isActive = true
        childViewController.view.leftAnchor.constraint(equalTo: toView.leftAnchor).isActive = true
        childViewController.view.rightAnchor.constraint(equalTo: toView.rightAnchor).isActive = true
        childViewController.view.bottomAnchor.constraint(equalTo: toView.bottomAnchor).isActive = true
        childViewController.didMove(toParentViewController: self)
    }
    
    func containerRemove(childViewController: UIViewController){
        
        childViewController.willMove(toParentViewController: nil)
        childViewController.view.removeFromSuperview()
        childViewController.removeFromParentViewController()
    }
}
