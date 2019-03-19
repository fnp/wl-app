//
//  MainNavigator.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 29/05/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit
import SideMenu
import OAuthSwift
import SafariServices

class MainNavigator: NSObject {
    
    fileprivate var window: UIWindow
    fileprivate var navigator: WLNavigationController!
    private var menuViewController: MenuViewController!

    init(window: UIWindow){
        self.window = window
        super.init()
        
        SideMenuManager.default.menuPresentMode = .menuSlideIn
        SideMenuManager.default.menuFadeStatusBar = false
        SideMenuManager.default.menuWidth = 245
        
        navigator = WLNavigationController(rootViewController: LibraryViewController.instance())
        window.rootViewController = navigator
        window.makeKeyAndVisible()
        
        let menuLeftNavigationController = UISideMenuNavigationController.instance()
        SideMenuManager.default.menuLeftNavigationController = menuLeftNavigationController
        menuViewController = menuLeftNavigationController.viewControllers[0] as! MenuViewController
    }
    
    func updateSettingsViewIfPresented() {
        if let settingsViewController = navigator.viewControllers.last as? SettingsViewController {
            settingsViewController.updateNotificationsSwitchValue()
        }
    }
    
    func setLoggedIn(){
        menuViewController.setup()
    }
    
    func reset(){
        menuViewController.setup()
        presentLibrary(dismissSideMenu: false)
    }

    
    func presentLibrary(dismissSideMenu: Bool){
        setRootViewController(controller: LibraryViewController.instance(), dismissSideMenu: dismissSideMenu, menuItem: .wolne_lektury)
    }
    
    func presentSearch() {
        setRootViewController(controller: SearchViewController.instance(type: .search), menuItem: .catalog)
    }
    
    func presentNews() {
        setRootViewController(controller: NewsViewController.instance(dataSource: nil), menuItem: .news)
    }
    
    func presentPremiumBook(bookSlug: String) {
        //TODO:
        setRootViewController(controller: BookDetailsViewController.instance(bookSlug: bookSlug), menuItem: .premium)
    }
    
    func presentPayPalForm() {
        appDelegate.presentPaypal(fromViewController: navigator)
    }
    
    func presentSettings() {
        setRootViewController(controller: SettingsViewController.instance(), menuItem: .settings)
    }
    
    func presentAbout() {
        setRootViewController(controller: AboutViewController.instance(), menuItem: .about)
    }
    
    func presentSupportUs() {
        presentPayPalForm()
//        setRootViewController(controller: SupportUsViewController.instance())
    }

    func presentBookList(listViewControllerType: ListViewControllerType){
        let vc = BookListViewController.instance(listViewControllerType: listViewControllerType, dataSource: nil)
        
        setRootViewController(controller: vc, menuItem: listViewControllerType.menuItem)
    }
    
    func presentDownloaded(){
        let vc = DownloadedListViewController.instance()
        setRootViewController(controller: vc, menuItem: .downloaded)
    }
    
    func presentLogin(push: Bool){
        let vc = LoginViewController.instance()
        if push {
            navigator.pushViewController(vc, animated: true)
        }
        else {
            setRootViewController(controller: vc)
        }
    }
    
    
    public func setRootViewController(controller: UIViewController, dismissSideMenu: Bool = true, menuItem: MenuItem? = nil){
        if appDelegate.sideMenuNavigationController != nil && dismissSideMenu{
            appDelegate.sideMenuNavigationController?.dismiss(animated: true, completion: nil)
        }
        
        if let menuItem = menuItem {
            menuViewController.selectItem(menuItem: menuItem)
        }
        navigator.setViewControllers([controller], animated: false)
    }
}
